package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.Package;
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
 * CRUD Manager for SBR.
 * @author jtrantin
 *
 */
@Stateless
public class SbrManager {

	@Inject
	private EntityManager em;

	@Inject
	private PackageManager pkgMan;

	@Inject
	private Event<SBR> event;

	/**
	 * Makes unmanaged entity managed and persistent.
	 * @param sbr unmanaged, not null
	 */
	public void create(SBR sbr) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		
		em.persist(sbr);
		event.fire(sbr);
	}

	/**
	 * Updates managed entity or creates unmanaged entity.
	 * @param sbr managed or unmanaged, not null
	 */
	public void update(SBR sbr) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		
		em.merge(sbr);
		event.fire(sbr);
	}

	/**
	 * Deletes managed or unmanaged SBR
	 * @param sbr managed or unmanaged, not null
	 */
	public void delete(SBR sbr) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		
		if(!em.contains(sbr)) {
			sbr = em.merge(sbr);
		}

		for (Package pkg : new ArrayList<Package>(sbr.getPackages())) {
			pkgMan.delete(pkg);
		}

		for (MemberSbr memberSbr : sbr.getMembersbrs()) {
			em.remove(memberSbr);
		}

		em.remove(sbr); // remove should be propagated to membersbr
		event.fire(sbr);
	}

	//retrieval
	/**
	 * Retrieve SBR by it's ID
	 * @param id
	 * @return SBR or null if not found
	 */
	public SBR getSbrById(long id) {
		return ensureCollectionsFetched(em.find(SBR.class, id));
	}

	/**
	 * @return all SBRs sorted by their name.
	 */
	public List<SBR> getAllSbrsSortedByName() {
		CriteriaBuilder cb =  em.getCriteriaBuilder();
		CriteriaQuery<SBR> query = cb.createQuery(SBR.class);
		Root<SBR> sbr = query.from(SBR.class);

		query.select(sbr).orderBy(cb.asc(cb.lower(sbr.<String>get("name"))));
		return ensureCollectionsFetched(em.createQuery(query).getResultList());
	}

	/**
	 * Counts distinct members in SBR (at all levels)
	 * @param sbr not null
	 * @return
	 */
	public Long getDistinctMemberCount(SBR sbr) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		
		Query query= em.createQuery("SELECT COUNT(DISTINCT ms.member ) FROM MemberSbr as ms WHERE " +
				"ms.sbr = :sbr");

		query.setParameter("sbr", sbr);
		return (Long)query.getSingleResult();
	}

	/**
	 * Computes KnowScore of SBR, thus of all PackageKnowledges of Packages in this SBR.
	 * <p><b>KnowScore</b> is a way how to compare knowledges, it is a value 2^knowledge.level.
	 * @param sbr not null
	 * @return KnowScore
	 */
	public Long getKnowScoreOfSbr(SBR sbr) {
		if(sbr==null)
			throw new NullPointerException("sbr");

		Query query = em.createQuery("SELECT SUM(POWER(2,know.level)) FROM PackageKnowledge know WHERE know.pkg.sbr = :sbr");
		query.setParameter("sbr", sbr);

		Double result = (Double) query.getSingleResult();

		if(result==null) {
			return 0L;
		} else {
			return result.longValue();
		}

	}


	//helpers
	private TypedQuery<SBR> getStandartQuery(String filter) {
		CriteriaBuilder cb =  em.getCriteriaBuilder();
		CriteriaQuery<SBR> criteria = cb.createQuery(SBR.class);
		Root<SBR> sbr = criteria.from(SBR.class);

		criteria.select(sbr).orderBy(cb.asc(cb.lower(sbr.<String>get("name"))));
		if(filter!=null && !filter.isEmpty()) {
			criteria.where(cb.like(cb.lower(sbr.<String>get("name")), "%" + filter + "%"));
		}
		return em.createQuery(criteria);
	}

	// instead of fetchtype = eager
	private SBR ensureCollectionsFetched(SBR sbr) {
		if(sbr==null)
			return null;

		if(sbr.getMembersbrs()!=null)
			sbr.getMembersbrs().size();
		if(sbr.getPackages()!=null)
			sbr.getPackages().size();

		return sbr;
	}

	private List<SBR> ensureCollectionsFetched(List<SBR> sbrs) {
		if(sbrs==null)
			return null;

		for(SBR s : sbrs) {
			ensureCollectionsFetched(s);
		}
		return sbrs;
	}

}
