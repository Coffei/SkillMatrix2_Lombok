package com.redhat.gss.skillmatrix.util.datamodels;

import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.MemberModel;
import org.richfaces.component.SortOrder;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;

/**
 * Richfaces model for all members. This model doesn't limit members in any way.
 * Some members may be positioned at first places using Preferred list.
 * @author jtrantin
 *
 */
public class AllMembersModel extends MemberModel {

	private List<Member> preferred;
	private EntityManager em;

	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param manager member manager instance
	 */
	public AllMembersModel(EntityManager em, MemberManager manager) {
		super(em, manager);
		this.em = em;
	}

	@Override
	protected List<Predicate> generatePredicates(Root<Member> root,
			CriteriaBuilder cb) {
		return Collections.emptyList();
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Member> criteria) {
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
	 * Returns list of preferred members, these members are put on the top of the list.
	 * Note that every call {@code doSort(..)} clears this list.
	 * @see #doSort(String, SortOrder)
	 * @return
	 */
	public List<Member> getPreferred() {
		return preferred;
	}

	/**
	 * Setter for preferred members list.
	 * Note that every call {@code doSort(..)} clears this list.
	 * @see #getPreferred()
	 * @see #doSort(String, SortOrder)
	 * @param preferred
	 */
	public void setPreferred(List<Member> preferred) {
		this.preferred = preferred;
	}

	@Override
	public void doSort(String attrName, SortOrder order) {
		this.preferred = null;
		super.doSort(attrName, order);
	}

}
