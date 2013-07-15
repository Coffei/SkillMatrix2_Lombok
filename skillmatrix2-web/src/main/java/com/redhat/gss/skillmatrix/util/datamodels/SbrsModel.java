package com.redhat.gss.skillmatrix.util.datamodels;

import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.AbstractDataModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

/**
 * Richfaces model for all SBRs. This model applies to constraints,
 *  thus is ideal for retrieving all SBRs.
 * @author jtrantin
 *
 */
public class SbrsModel extends AbstractDataModel<SBR, SBR> {

	private EntityManager em;
	private SbrManager sm;
	
	private List<Predicate> lastPredicates;
	private Root<SBR> lastRoot;
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param manager sbr manager instance
	 */
	public SbrsModel(EntityManager em, SbrManager manager) {
		super(em);
		
		this.em = em;
		this.sm = manager;
	}

	@Override
	protected CriteriaQuery<SBR> createCriteriaQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SBR> criteria = cb.createQuery(SBR.class);
		
		Root<SBR> sbr = criteria.from(SBR.class);
		
		criteria.select(sbr);
		
		lastRoot = sbr;
		lastPredicates = Collections.emptyList();
		
		return criteria;
	}

	@Override
	protected CriteriaQuery<Long> createCountCriteriaQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		
		Root<SBR> sbr = criteria.from(SBR.class);
		
		criteria.select(cb.count(sbr));
		
		lastRoot = sbr;
		lastPredicates = Collections.emptyList();
		
		return criteria;
	}

	@Override
	protected Root<SBR> getRoot() {
		return this.lastRoot;
	}

	@Override
	protected List<Predicate> getExistingPredicates() {
		return this.lastPredicates;
	}

	@Override
	protected Object getId(SBR v) {
		return v.getId();
	}
	
	

	@Override
	protected void postProcessCriteria(CriteriaQuery<SBR> criteria) {
		
	}

	@Override
	public SBR getRowData() {
		return sm.getSbrById((Long)getRowKey());
	}

	

}
