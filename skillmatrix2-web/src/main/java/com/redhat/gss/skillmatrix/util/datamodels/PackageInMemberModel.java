package com.redhat.gss.skillmatrix.util.datamodels;

import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.PackageModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;

/**
 * Richfaces model for packages that specified member has knowledge of at specified level.
 * @author jtrantin
 *
 */
public class PackageInMemberModel extends PackageModel {

	private int atLevel;
	private Member member;
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param man package manager instance
	 * @param atLevel level of knowledge
	 * @param member member
	 */
	public PackageInMemberModel(EntityManager em, PackageManager man, int atLevel, Member member) {
		super(em, man);
		
		this.atLevel = atLevel;
		this.member = member;
	}

	@Override
	protected List<Predicate> generatePredicates(Root<Package> root, CriteriaBuilder cb) {
		Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
		Root<PackageKnowledge> know = subquery.from(PackageKnowledge.class);
		
		subquery.select(know.get("pkg").get("id").as(Long.class)).where(cb.equal(know.get("level"), atLevel),
																 cb.equal(know.get("member"), member));
		
		return Arrays.asList((Predicate)cb.in(root.get("id")).value(subquery));
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Package> criteria) {
	
	}

}
