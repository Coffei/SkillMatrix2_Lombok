package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Member;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Stateless
public class LanguageKnowledgeManager {
	
	@Inject
	private EntityManager em;
	
	@Inject
	private Event<LanguageKnowledge> event;
	
	
	public void create(LanguageKnowledge knowledge) {
		if(knowledge==null) 
			throw new NullPointerException("knowledge");
		
		em.persist(knowledge);
		event.fire(knowledge);
	}
	
	public void update(LanguageKnowledge knowledge) {
		if(knowledge==null)
			throw new NullPointerException("knowledge");
		
		em.merge(knowledge);
		event.fire(knowledge);
	}
	
	public void delete(LanguageKnowledge knowledge) {
		if(knowledge==null)
			throw new NullPointerException("knowledge");
		
		if(!em.contains(knowledge)) {
			knowledge = em.merge(knowledge);
		}
			
		em.remove(knowledge);
		event.fire(knowledge);
	}
	
	public boolean existsByMemberLanguage(Member member, String language) {
		if(member==null)
			throw new NullPointerException("member");
		if(language==null)
			throw new NullPointerException("language");
		language = language.trim().toUpperCase(Locale.ENGLISH);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		
		Root<LanguageKnowledge> know =  criteria.from(LanguageKnowledge.class);
		criteria.select(cb.count(know)).where(cb.equal(know.get("member"), member), cb.equal(cb.upper(know.get("language").as(String.class)), language));
		
		return em.createQuery(criteria).getSingleResult() > 0;
	}
	
	public void deleteAllWithMemberNotInLanguages(Member member, List<String> listOfLanguages) {
		if(member==null)
			throw new NullPointerException("member");
		if(listOfLanguages==null)
			throw new NullPointerException("listOfLanguages");
		
		List<String> languages = upperList(listOfLanguages);
		
		Query query = em.createQuery("DELETE FROM LanguageKnowledge know WHERE know.member = :member " 
					+ (languages.isEmpty()? "" :  "AND UPPER(know.language) NOT IN :languages"));
		
		query.setParameter("member", member);
		if(!languages.isEmpty())
			query.setParameter("languages", languages);
		
		query.executeUpdate();
	}
	
	private List<String> upperList(List<String> source) {
		List<String> result = new ArrayList<String>(source.size());
		for (String element : source) {	
			result.add(element.trim().toUpperCase(Locale.ENGLISH));
		}
		
		return result;
	}
}
