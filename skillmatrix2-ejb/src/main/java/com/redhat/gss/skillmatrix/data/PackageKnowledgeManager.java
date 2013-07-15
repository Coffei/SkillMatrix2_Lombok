package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

/**
 * CRUD Manager for PackageKnowledge.
 * @author jtrantin
 *
 */
@Stateless
public class PackageKnowledgeManager {

	@Inject
	private EntityManager em;

	@Inject
	private Event<PackageKnowledge> event;

	/**
	 * Makes unmanaged entity managed and persistent.
	 * @param know unmanaged entity, not null
	 */
	public void create(PackageKnowledge know) {
		if(know==null)
			throw new NullPointerException("know");

		em.persist(know);
		event.fire(know);
	}

	/**
	 * Updates managed entity or crates unmanaged one.
	 * @param know managed or unmanaged entity, not null
	 */
	public void update(PackageKnowledge know) {
		if(know==null)
			throw new NullPointerException("know");

		em.merge(know);
		event.fire(know);
	}

	/**
	 * Deletes PackageKnowledge entity
	 * @param know not null
	 */
	public void delete(PackageKnowledge know) {
		if(know==null)
			throw new NullPointerException("know");

		if(!em.contains(know)) {
			know = em.merge(know);
		}

		em.remove(know);
		event.fire(know);
	}

	/**
	 * Retrieve PackageKnowledge by its ID.
	 * @param id
	 * @return PackageKnowledge or null if not found
	 */
	public PackageKnowledge getByID(long id) {
		return em.find(PackageKnowledge.class, id);
	}

	/**
	 * Retrieves list of all PackageKnowledges with specified Package.
	 * @param pkg package, not null
	 * @return
	 */
	public List<PackageKnowledge> getAllByPackage(Package pkg) {
		if(pkg==null)
			throw new NullPointerException("pkg");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PackageKnowledge> query = cb.createQuery(PackageKnowledge.class);
		Root<PackageKnowledge> root = query.from(PackageKnowledge.class);

		query.select(root).where(cb.equal(root.get("pkg"), pkg));
		return em.createQuery(query).getResultList();

	}

	/**
	 * Retrieves list of all PackageKnowledges with specified Package and Member
	 * @param pkg package, not null
	 * @param member member, not null
	 * @return
	 */
	public List<PackageKnowledge> getAllByPackageAndMember(Package pkg, Member member) {
		if(pkg==null) 
			throw new NullPointerException("pkg");
		if(member==null)
			throw new NullPointerException("member");	

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PackageKnowledge> criteria = cb.createQuery(PackageKnowledge.class);
		Root<PackageKnowledge> know = criteria.from(PackageKnowledge.class);


		criteria.select(know).where(cb.equal(know.get("member"), member),
				cb.equal(know.get("pkg"), pkg));

		return em.createQuery(criteria).getResultList();

	}

	/**
	 * Counts Members by level of knowledge of certain package.
	 * @param pkg package, not null
	 * @param level for sensible results use values between 0 and 2 inclusive
	 * @return number of members
	 */
	public Long getCountMembersByLevelOfKnowledge(Package pkg, int level) {
		if(pkg==null) 
			throw new NullPointerException("package");

		Query query = em.createQuery("SELECT COUNT(*) FROM PackageKnowledge know " +
				"WHERE know.level = :level AND " +
				"know.pkg = :package");

		query.setParameter("level", level);
		query.setParameter("package", pkg);

		return (Long) query.getSingleResult();
	}

	/**
	 * Deletes all PackageKnowledges with specified member and with package not in the specified collection.
	 * @param member member, not null
	 * @param packages collection of packages, not null, if empty all PackageKnowledges of the member are deleted.
	 */
	public void deleteAllWithMemberNotInPackages(Member member, Collection<Package> packages) {
		if(member==null)
			throw new NullPointerException("member");
		if(packages==null)
			throw new NullPointerException("packages");

		Query query = em.createQuery("DELETE FROM PackageKnowledge know WHERE know.member = :member" + (packages.isEmpty()? "" : " AND know.pkg NOT IN :packages"));

		query.setParameter("member", member);
		if(!packages.isEmpty()) {
			query.setParameter("packages", packages);
		}
		
		query.executeUpdate();
		
	}

}
