package com.redhat.gss.skillmatrix.util.datamodels.abstracts;

import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Member;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * Abstract class extending AbstractDataModel, that makes implementing full-featured model for Members a bit easier.
 * @see #generatePredicates(Root, CriteriaBuilder)
 * @author jtrantin
 *
 */
public abstract class MemberModel extends AbstractDataModel<Member, Member> {

	private EntityManager em;
	private MemberManager mm;

	private List<Predicate> lastPredicates = new ArrayList<Predicate>();
	private Root<Member> lastRoot;

	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param manager member manager instance
	 */
	public MemberModel(EntityManager em, MemberManager manager) {
		super(em);
		this.em = em;
		this.mm = manager;
	}

	/**
	 * Returns a list of predicates, that will be applied in this model.
	 * This is a key method in child-class implementation.
	 * @param root root to be used in predicates
	 * @param cb criteria builder that can build predicates.
	 * @return list of predicates for specified root
	 */
	protected abstract List<Predicate> generatePredicates(Root<Member> root, CriteriaBuilder cb);

	@Override
	protected CriteriaQuery<Member> createCriteriaQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Member> criteria = cb.createQuery(Member.class);

		Root<Member> member = criteria.from(Member.class);

		this.lastRoot = member;
		this.lastPredicates = generatePredicates(member, cb);

		criteria.select(member);

		return criteria;
	}

	@Override
	protected CriteriaQuery<Long> createCountCriteriaQuery() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);

		Root<Member> member = criteria.from(Member.class);

		criteria.select(cb.count(member));

		this.lastRoot = member;
		this.lastPredicates = generatePredicates(member, cb);


		return criteria;

	}

	@Override
	protected Root<Member> getRoot() {
		return lastRoot;
	}

	@Override
	protected List<Predicate> getExistingPredicates() {
		return lastPredicates;
	}

	@Override
	protected Object getId(Member t) {
		return t.getId();
	}

	@Override
	public Member getRowData() {
		return mm.getMemberById((Long)getRowKey());
	}

	@Override
	protected Predicate createPredicate(Entry<String, Object> values) { // custom languages filter
		if("languages".equals(values.getKey())) {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			String[] languages = ((String)values.getValue()).split(",");
			List<Predicate> predicates = Lists.newArrayList();

			for (String language : languages) {

				if(!language.trim().isEmpty()) {
					Subquery<LanguageKnowledge> subquery = cb.createQuery().subquery(LanguageKnowledge.class);
					Root<LanguageKnowledge> know = subquery.from(LanguageKnowledge.class);
					subquery.select(know).where(cb.equal(know.get("member"), getRoot()), cb.like(know.get("language").as(String.class), "%" 
							+ language.trim().toUpperCase(Locale.ENGLISH) + "%"));

					predicates.add(cb.exists(subquery));
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));


		}

		return super.createPredicate(values);
	}





}
