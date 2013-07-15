package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Member;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 7/5/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateful
public class MemberSearcher {

    @Inject
    private EntityManager em;

    private List<Predicate> predicates;
    private CriteriaQuery<Member> query;
    private Root<Member> root;


    public MemberSearcher startQuery() {
        predicates.clear();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        this.query = cb.createQuery(Member.class);
        this.root = query.from(Member.class);

        return this;
    }

    /**
     * Adds username predicate. Member must have <code>usernameFragment</code> in his username.
     * @param username fragment of username, it is not case-sensitive
     * @return
     */
    public MemberSearcher usernameFragment(String username){
        if(query==null)
            throw new IllegalStateException("no query started");
        if(username==null)
            throw new NullPointerException("username");


        return null;


    }

    /**
     * Adds language predicate. Member must have the exact <code>language</code>.
     * @param language language code
     * @return
     */
    public MemberSearcher language(String language) {
        if(query==null)
            throw new IllegalStateException("no query started");
        if(language==null)
            throw new NullPointerException("language");

        //create predicate using subquery
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Subquery<LanguageKnowledge> subquery = this.query.subquery(LanguageKnowledge.class);
        Root<LanguageKnowledge> lang = subquery.from(LanguageKnowledge.class);
        subquery.where(cb.equal(lang.get("member"), root), cb.equal(cb.upper(lang.get("language").as(String.class)), language.toUpperCase(Locale.ENGLISH)));
        //add predicate
        this.predicates.add(cb.exists(subquery));

        return this;
    }

    /**
     * Adds languages predicate. You can specify more languages, separating them by <code>','̈́</code>. Member must have all specified languages.
     * @param language string of multiple languages separated by <code>','</code>
     * @return
     */
    public MemberSearcher languagesString(String language) {
        if(query==null)
            throw new IllegalStateException("no query started");
        if(language==null)
            throw new NullPointerException("language");

        String[] langs = language.split(","); //split into separate languages
        for(String lang : langs) {
            if(!lang.trim().isEmpty()) //for every lang, create predicate
                language(lang.trim());
        }

        return this;
    }





    public List<Member> getResults(){
        if(query==null)
            throw new IllegalStateException("no query started");

        return em.createQuery(this.query).getResultList();
    }


    @PostConstruct
    private void init() {
        predicates = new ArrayList<Predicate>();


    }

}
