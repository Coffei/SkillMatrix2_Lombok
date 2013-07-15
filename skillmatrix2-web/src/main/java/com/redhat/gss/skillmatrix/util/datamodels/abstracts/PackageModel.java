package com.redhat.gss.skillmatrix.util.datamodels.abstracts;

import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Package;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Abstract class extending AbstractDaatModel, that makes implementing 
 * full-featured Package model a bit easier.
 * 
 * @see #generatePredicates(Root, CriteriaBuilder)
 * @author jtrantin
 *
 */
public abstract class PackageModel extends AbstractDataModel<Package, Package> {

	private EntityManager em;
	private PackageManager pm;
	
	private Root<Package> lastRoot;
	private List<Predicate> lastPredicates;
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param man package manager instance
	 */
	public PackageModel(EntityManager em, PackageManager man) {
		super(em);
		this.em = em;
		this.pm = man;
	}

	@Override
	protected CriteriaQuery<Package> createCriteriaQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Package> criteria = cb.createQuery(Package.class);
		
		Root<Package> root = criteria.from(Package.class);
		
		criteria.select(root);
		
		lastRoot = root;
		lastPredicates = generatePredicates(root, cb);
		
		return criteria;
		
	}

	@Override
	protected CriteriaQuery<Long> createCountCriteriaQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		
		Root<Package> root = criteria.from(Package.class);
		root.alias("pkg");
		
		criteria.select(cb.count(root));
		
		lastRoot = root;
		lastPredicates = generatePredicates(root, cb);
		
		return criteria;
	}

	@Override
	protected Root<Package> getRoot() {
		return lastRoot;
	}

	@Override
	protected List<Predicate> getExistingPredicates() {
		return lastPredicates;
	}

	@Override
	protected Object getId(Package v) {
		return v.getId();
	}
	
	@Override
	public Package getRowData() {
		return pm.getPkgById((Long)getRowKey());
	}
	
	/**
	 * Returns a list of predicates, that will be applied in this model.
	 * This is a key method in child-class implementation.
	 * @param root root to be used in predicates
	 * @param cb criteria builder that can build predicates.
	 * @return list of predicates for specified root
	 */
	protected abstract List<Predicate> generatePredicates(Root<Package> root, CriteriaBuilder cb);
	

}
