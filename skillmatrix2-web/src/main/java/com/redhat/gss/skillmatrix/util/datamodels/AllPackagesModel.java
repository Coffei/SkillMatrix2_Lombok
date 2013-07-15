package com.redhat.gss.skillmatrix.util.datamodels;

import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.PackageModel;
import org.richfaces.component.SortOrder;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Richfaces model for all packages. This model can be used to retrieve all packages.
 * Some packages may be retrieved at first positions using Preferred list.
 * Some packages may be excluded from this view using Excluded list.
 * @author jtrantin
 *
 */
public class AllPackagesModel extends PackageModel {

	private List<Package> excludedPackages;
	private List<Package> preferred;
	
	private EntityManager em;

	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param man package manager instance
	 */
	public AllPackagesModel(EntityManager em, PackageManager man) {
		super(em, man);
		this.em = em;
	}

	@Override
	protected List<Predicate> generatePredicates(Root<Package> root,
			CriteriaBuilder cb) {
		if(excludedPackages==null || excludedPackages.isEmpty()) {
			return Collections.emptyList();
		} else {
			In<Package> in = cb.in(root);
			for (Package pkg : excludedPackages) {
				in.value(pkg);
			}
			
			return Arrays.asList(in.not());
		}
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Package> criteria) {
		if(preferred!=null && !preferred.isEmpty()) {
			List<Order> orders = Lists.newArrayList(criteria.getOrderList());
			CriteriaBuilder cb = em.getCriteriaBuilder();

			Root<?> root = (Root<?>) criteria.getRoots().toArray()[0]; //get first root

			Order order = cb.desc(cb.selectCase().when(root.in(preferred), 1).otherwise(0));

			orders.add(0, order);

			criteria.orderBy(orders);
		}
	}
	
	/**
	 * Returns a list of excluded packages.
	 * @return
	 */
	public List<Package> getExcludedPackages() {
		return excludedPackages;
	}

	/**
	 * Sets a list of excluded packages. This list is valid until rewritten.
	 * @param excludedPackages list of packages not to retrieve
	 */
	public void setExcludedPackages(List<Package> excludedPackages) {
		this.excludedPackages = excludedPackages;
	}
	
	/**
	 * List of packages to retrieve at first positions (prefer them to others).
	 * @return list of packages
	 */
	public List<Package> getPreferred() {
		return preferred;
	}

	/**
	 * Sets list of packages to be preferred. 
	 * Note that every {@code doSort(..)} call clears this list.
	 * @see #doSort(String, SortOrder)
	 * @param preferred
	 */
	public void setPreferred(List<Package> preferred) {
		this.preferred = preferred;

	}

	@Override
	public void doSort(String attrName, SortOrder order) {
		if("pref".equals(attrName)) {
			super.doSort("name", order); // do not clear preferred, if set, then the ordering is applied
		} else {
			super.doSort(attrName, order);
			this.preferred = null;
		}
	}


}
