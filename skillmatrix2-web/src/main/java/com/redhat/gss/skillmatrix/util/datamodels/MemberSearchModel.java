package com.redhat.gss.skillmatrix.util.datamodels;

import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.MemberModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Richfaces model for searching in members. Typical usage is to add predicates first, and retrieve the result list in the end.
 * @author jtrantin
 *
 */
public class MemberSearchModel extends MemberModel {
	private static final String MEMBER_ALIAS = "m";
	
	private EntityManager em;
	
	private List<Predicate> predicates = new ArrayList<Predicate>();
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param manager member manager instance
	 */
	public MemberSearchModel(EntityManager em, MemberManager manager) {
		super(em, manager);
		
		this.em = em;
	}
	
	
	/**
	 * Adds constraining predicate, limiting members text attribute. Predicate checks whether members attribute named propName contains value propValue.
	 * @param propName name of members attribute
	 * @param propValue partial value of members attribute
	 * @return current MemberSearch instance
	 */
	public MemberSearchModel addPropertyContainsPredicate(String propName, String propValue) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Root<Member> member = cb.createQuery(Member.class).from(Member.class);
		member.alias(MEMBER_ALIAS);
		
		Predicate p = cb.like(cb.lower(member.get(propName).as(String.class)), "%" + propValue.toLowerCase() + "%");
		predicates.add(p);
		
		return this;
	}
	
	/**
	 * Adds constraining predicate, limiting count of members knowledges.
	 * @param atLevel only knowledges with this level will be counted, allowed values are from -1 to 2 inclusive,
	 *  -1 will count all knowledges with any level
	 * @param operator operator of comparison, permitted values are "g" for greater than,
	 *  "s" for smaller than, "e" for equal to, "ge" for greater than or equal to and "se" for smaller than or equal to
	 * @param mincount number of minimal (or maximal, depending on the operator) count of knowledges
	 * @return current MemberSearch instance
	 */
	public MemberSearchModel addKnowledgeCountPredicate(int atLevel,String operator, int mincount) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Knowledge> criteria = cb.createQuery(Knowledge.class);
		Root<Member> member = criteria.from(Member.class);
		member.alias(MEMBER_ALIAS);
		
		Subquery<Long> subquery =  criteria.subquery(Long.class);
		Root<Knowledge> know = subquery.from(Knowledge.class);
		
		Predicate[] subpreds = new Predicate[atLevel>=0? 2 : 1];
		subpreds[0] = cb.equal(know.get("member").as(Member.class), member);
		if(atLevel>=0)
			subpreds[1] = cb.equal(know.get("level").as(Integer.class), atLevel);
		
		subquery.select(cb.count(know)).where(subpreds);
		
		if(operator.equals("g")) {
			predicates.add(cb.greaterThan(subquery, (long)mincount));
		} else if (operator.equals("s")) {
			predicates.add(cb.lessThan(subquery, (long)mincount));
		} else if (operator.equals("e")) {
			predicates.add(cb.equal(subquery, (long)mincount));
		} else if (operator.equals("ge")) {
			predicates.add(cb.greaterThanOrEqualTo(subquery, (long)mincount));
		} else if (operator.equals("se")) {
			predicates.add(cb.lessThanOrEqualTo(subquery, (long)mincount));
		}
		 
		return this;
	}
	
	/**
	 * Adds constraining predicate that limits the members in knowledge of certain package.
	 * @param pkgMinLevels map of packages ID and minimal level of knowledge
	 * @return current MemberSearch instance
	 */
	public MemberSearchModel addPackageKnowledgePredicate(Map<Long, Integer> pkgMinLevels) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PackageKnowledge> criteria = cb.createQuery(PackageKnowledge.class);
		Root<Member> member = criteria.from(Member.class);
		member.alias(MEMBER_ALIAS);
		
		
		for (Map.Entry<Long, Integer> entry : pkgMinLevels.entrySet()) {
			Subquery<PackageKnowledge> subquery = criteria.subquery(PackageKnowledge.class);
			Root<PackageKnowledge> know = subquery.from(PackageKnowledge.class);
			
			subquery.select(know).where(cb.and(cb.equal(know.get("member"), member),
										cb.equal(know.get("pkg"), entry.getKey()),
										cb.greaterThanOrEqualTo(know.get("level").as(Integer.class), entry.getValue())));
			
			this.predicates.add(cb.exists(subquery));
		}
		
		return this;
	}
	
	
	//helpers
	
	

	@Override
	protected List<Predicate> generatePredicates(Root<Member> root,
			CriteriaBuilder cb) {
		root.alias(MEMBER_ALIAS);
		
		return predicates;
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Member> criteria) {
		// not needed
	}
	
}
