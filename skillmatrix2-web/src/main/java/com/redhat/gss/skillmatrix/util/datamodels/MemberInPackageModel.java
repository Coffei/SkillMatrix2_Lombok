package com.redhat.gss.skillmatrix.util.datamodels;

import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.MemberModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;

/**
 * Richfaces model for Members with knowledge of specified package at specified level.
 * @author jtrantin
 *
 */
public class MemberInPackageModel extends MemberModel {

	private Package pkg;
	private int atLevel;
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param manager member manager instance
	 * @param pkg package
	 * @param atLevel level of knowledge
	 */
	public MemberInPackageModel(EntityManager em, MemberManager manager, Package pkg, int atLevel) {
		super(em, manager);
		
		this.pkg = pkg;
		this.atLevel = atLevel;
	}

	@Override
	protected List<Predicate> generatePredicates(Root<Member> root, CriteriaBuilder cb) {
		Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
		Root<PackageKnowledge> know = subquery.from(PackageKnowledge.class);
		
		subquery.select(know.get("member").get("id").as(Long.class))
				.where(cb.equal(know.get("level"), atLevel),
					   cb.equal(know.get("pkg"), pkg));
		
		return Arrays.asList((Predicate)cb.in(root.get("id")).value(subquery));
		
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Member> criteria) {
		// not needed
		
	}

}
