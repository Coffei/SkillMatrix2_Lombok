package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;
import com.redhat.gss.skillmatrix.model.SBR;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;


/**
 * CRUD Manager for Package entities.
 * @author jtrantin
 *
 */
@Stateless
public class PackageManager {

	@Inject
	private EntityManager em;

	@Inject
	private Event<Package> event;

	@Inject
	private PackageKnowledgeManager pkgKnowMan;

	/**
	 * Makes unmanaged entity managed and persistent.
	 * @param pkg package to save, not null
	 */
	public void create(Package pkg) {
		if(pkg==null)
			throw new NullPointerException("pkg");
		
		em.persist(pkg);
		event.fire(pkg);
	}

	/**
	 * Updates managed entity or creates unmanaged one.
	 * @param pkg unmanaged or managed, not null
	 */
	public void update(Package pkg) {
		if(pkg==null)
			throw new NullPointerException("pkg");
		
		em.merge(pkg);
		event.fire(pkg);
	}

	/**
	 * Deletes package by it's ID.
	 * @param pkg package to delete, not null
	 */
	public void delete(Package pkg) {
		if(pkg==null)
			throw new NullPointerException("pkg");
		
		if(!em.contains(pkg)) {
			pkg = em.merge(pkg);
		}

		for (PackageKnowledge know : new ArrayList<PackageKnowledge>(pkgKnowMan.getAllByPackage(pkg))) {
			pkgKnowMan.delete(know);
		}
		em.remove(pkg);
		event.fire(pkg);
	}

	/**
	 * Retrieves all packages ordered by their name.
	 * @return list of packages.
	 */
	public List<Package> getAllPackages() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Package> criteria =  cb.createQuery(Package.class);
		Root<Package> pkg = criteria.from(Package.class);

		criteria.select(pkg).orderBy(cb.asc(cb.lower(pkg.<String>get("name"))));
		return em.createQuery(criteria).getResultList();
	}

	/**
	 * Retrieve packages by it's ID.
	 * @param id
	 * @return Package instance or null if not found
	 */
	public Package getPkgById(long id) {
		return em.find(Package.class, id);
	}

	/**
	 * Retrieves all packages ordered by name, starting at {@code first} position, returning max {@code howmany} records and filtering them by {@code filter}.
	 * @param first where to start
	 * @param howmany how many to retrieve
	 * @param filter filter, packages must contain the value in it's name, if null no filtering is applied
	 * @return list of packages ordered by name.
	 */
	public List<Package> getAllPackagesLimit(int first, int howmany, String filter) {
		if(first<0)
			throw new IllegalArgumentException("negative first, expected positive or zero");
		if(howmany<=0) 
			throw new IllegalArgumentException("negative or zero howmany, expected positive");

		TypedQuery<Package> query = getStandartQuery(filter);
		query.setFirstResult(first);
		query.setMaxResults(howmany);

		return query.getResultList();
	}

	/**
	 * Counts Packages that have {@code filter} in their name.
	 * @param filter if null no filtering is applied.
	 * @return number of packages.
	 */
	public Long getFilteredCount(String filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria =  cb.createQuery(Long.class);
		Root<Package> pkg = criteria.from(Package.class);

		criteria.select(cb.count(pkg));
		if(filter!=null && !filter.isEmpty()) {
			criteria.where(cb.like(cb.lower(pkg.<String>get("name")), "%" + filter + "%"));
		}
		return em.createQuery(criteria).getSingleResult();
	}

	/**
	 * Get all packages in SBR limited by {@code limit} value.
	 * @param sbr not null
	 * @param limit max records to retrieveS
	 * @return list of packages
	 */
	public List<Package> getAllPackagesInSbrLimit(SBR sbr, int limit) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		if(limit<=0)
			throw new IllegalArgumentException("limit expected positive");
		
		Query query = em.createQuery("SELECT pkg FROM Package as pkg WHERE " +
				"pkg.sbr = :sbr");

		query.setParameter("sbr", sbr);

		query.setMaxResults(limit);

		return query.getResultList();
	}
	
	
	//helpers

	private TypedQuery<Package> getStandartQuery(String filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Package> criteria =  cb.createQuery(Package.class);
		Root<Package> pkg = criteria.from(Package.class);

		criteria.select(pkg).orderBy(cb.asc(cb.lower(pkg.<String>get("name"))));
		if(filter!=null && !filter.isEmpty()) {
			criteria.where(cb.like(cb.lower(pkg.<String>get("name")), "%" + filter + "%"));
		}
		return em.createQuery(criteria);
	}


}
