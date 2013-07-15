package com.redhat.gss.skillmatrix.util.datamodels;

import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.MemberModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;

/**
 * Richfaces model for members who have specified SBR at specified level.
 * @author jtrantin
 *
 */
public class MemberInSBRModel extends MemberModel {

	private SBR sbr;
	private int atLevel;
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param manager member manager instance
	 * @param sbr sbr
	 * @param atLevel level of sbr membership
	 */
	public MemberInSBRModel(EntityManager em, MemberManager manager, SBR sbr, int atLevel) {
		super(em, manager);
		this.sbr = sbr;
		this.atLevel = atLevel;
	}

	@Override
	protected List<Predicate> generatePredicates(Root<Member> root, CriteriaBuilder cb) {
		
		Subquery<MemberSbr> subquery = cb.createQuery().subquery(MemberSbr.class);
		
		Root<MemberSbr> msbr = subquery.from(MemberSbr.class);
		
		subquery.select(msbr).where(cb.equal(msbr.get("sbr"), this.sbr), cb.equal(msbr.get("level"), this.atLevel), cb.equal(msbr.get("member"), root));
		
		return Arrays.asList(cb.exists(subquery));
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Member> criteria) {
		// not needed
		
	}
}
