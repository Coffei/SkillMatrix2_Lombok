package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 7/15/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class MemberManager {

    @Inject
    private EntityManager em;

    @Inject
    private Event<Member> memberEvent;

    @Inject
    private KnowledgeManager knowManager;

    @Inject
    private MemberSbrManager memberSbrManager;

    @Inject
    private Logger log;



    /**
     * Makes unmanaged entity managed and persistent.
     * @param member unmanaged entity
     */
    public void create(Member member) {
        if(member==null)
            throw new NullPointerException("member");

        em.persist(member);
        memberEvent.fire(member);
    }

    /**
     * Updates entity by its ID.
     * @param member member to be updated
     * @return
     */
    public Member update(Member member) {
        if(member==null)
            throw new NullPointerException("member");

        member = em.merge(member);
        memberEvent.fire(member);
        return member;
    }

    /**
     * Deletes member from persistent context.
     * @param member member to be deleted.
     */
   public void delete(Member member) {
        if(member==null)
            throw new NullPointerException("member");

        if(!em.contains(member)) { // merge if not managed
            member = em.merge(member);
        }

        for (Knowledge know : new ArrayList<Knowledge>(member.getKnowledges())) { //delete all knowledges
            knowManager.delete(know);
        }

        nullCoachIfExists(member);

        for (MemberSbr memberSbr : member.getMembersbrs()) {
            em.remove(memberSbr);
        }

        em.remove(member);
        memberEvent.fire(member);
    }

    public Member getMemberById(long id) {
        return ensureCollectionsFetched(em.find(Member.class, id));
    }

    public Member getMemberByUnifiedId(String id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);
        Root<Member> member = query.from(Member.class);

        query.where(cb.equal(member.get("unified_id"), id));

        TypedQuery<Member> typedQuery = em.createQuery(query);
        typedQuery.setMaxResults(1);

        List<Member> results = typedQuery.getResultList();
        if(results.isEmpty())
            return null;

        return results.get(0);
    }

    /**
     * Returns a Member with specified nick. If there are none null is returned.
     * @param nick not null
     * @return Member instance or null if not found
     */
   public Member getMemberByNick(String nick) {
        if(nick==null)
            throw new NullPointerException("nick");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
        Root<Member> member = criteria.from(Member.class);
        Predicate predicate = cb.equal(member.get("nick"), nick);

        criteria.select(member).where(predicate);
        List<Member> results = em.createQuery(criteria).getResultList();

        return ensureCollectionsFetched(results.isEmpty()? null : results.get(0));
    }

    /**
     * Retrieves all persistent members ordered by their name.
     * @return list of member
     */
     public List<Member> getAllMembers() {
        return ensureCollectionsFetched(getStandartQueryForAllMembers().getResultList());
    }

    /**
     * Retrieves all members ordered by their name, starting at {@code first} position, limiting to {@code howmany} result and filtering members by {@code filter}.
     * @param first where to start retrieving people
     * @param howmany howmany result maximum to retrieve
     * @param filter filter, at least name, nick, or email must contain filter.
     * @return list of members.
     */
    public List<Member> getAllMembersLimit(int first, int howmany, String filter) {
        if(first<0)
            throw new IllegalArgumentException("negative first, expected positive or zero");
        if(howmany<=0)
            throw new IllegalArgumentException("negative or zero howmany, expected positive");

        TypedQuery<Member> query = getStandartQueryForAllMembers(filter);
        query.setFirstResult(first);
        query.setMaxResults(howmany);

        return query.getResultList();
    }

    /**
     * Get number of members that satisfy filter predicate.
     * @param filter filter, at least name, nick, or email must contain filter.
     * @return number of members
     */
    public Long getFilteredCount(String filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<Member> member = criteria.from(Member.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(cb.count(member));
        if(filter!=null && !filter.isEmpty()) {
            criteria.where(cb.like(cb.lower(member.<String>get("name")), "%" + filter + "%"));
        }
        return em.createQuery(criteria).getSingleResult();
    }


    /**
     * Find members by knowledge at some level of some package.
     * @param pkg package
     * @param level level of knowledge
     * @param limit max number of records, if null no limit is applied
     * @return
     */
    public List<Member> getMembersByLevelOfKnowledge(Package pkg, int level, Integer limit) {
        if(pkg==null)
            throw new NullPointerException("package");
        if(level < 0 || level > 2)
            throw new IllegalArgumentException("level expected between 0 and 2 inclusive, got: " + level);

        Query query = em.createQuery("SELECT m FROM PackageKnowledge know INNER JOIN know.member as m WHERE " +
                "know.pkg = :package AND " +
                "know.level = :level " +
                "ORDER BY lower(m.name) ASC");


        query.setParameter("level", level);
        query.setParameter("package", pkg);

        if(limit!=null) {
            query.setMaxResults(limit);
        }

        @SuppressWarnings("unchecked")
        List<Member> results = query.getResultList();
        return ensureCollectionsFetched(results);
    }

    /**
     * Finds number of all package knowledges at specified level who's owner is specified Member.
     * @param member not null
     * @param level between 0 and 2 inclusive
     * @return number of knowledges.
     */
    public Long getNumberOfLevelKnowledge(Member member, int level) {
        if(member==null)
            return 0L;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<PackageKnowledge> know = criteria.from(PackageKnowledge.class);

        criteria.select(cb.count(know)).where(cb.and(cb.equal(know.get("member"), member), cb.equal(know.get("level"), level)));

        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * Retrieves members in SBR at specified level.
     * @param sbr not null
     * @param level
     * @return list of members
     */
    public List<Member> getMembersInSbr(SBR sbr, int level) {
        if(sbr==null)
            throw new NullPointerException("sbr");
        if(sbr.getId()==null)
            return new ArrayList<Member>();

        sbr = em.find(SBR.class, sbr.getId());

        if(sbr==null)  //if sbr is not persistent, don't bother with query
            return new ArrayList<Member>();

        Query query = em.createQuery("SELECT memsbr.member FROM MemberSbr as memsbr " +
                "WHERE memsbr.level = :level AND " +
                "memsbr.sbr = :sbr");

        query.setParameter("sbr", sbr);
        query.setParameter("level", level);

        return ensureCollectionsFetched(query.getResultList());

    }

    /**
     * Retrieves Members with best knowledge (highest KnowScore) of specified SBR. Returns list of arrays, every array has length of two,
     *  first element is <b>Member</b> instance, second is <b>KnowScore</b> as Double.
     * <p><b>KnowScore</b> is effective way how to compare knowledge, its value is 2^knowledge.level.
     * KnowScore usually means sum of these values from some Collection of knowledges.
     * @param sbr to which sbr should the results be limited, if null no limitation is applied
     * @return list of object arrays
     */
    //TODO: Refactor to make it type safe
    public List<Object []> getPeopleWithBestKnowledgeOfSbr(SBR sbr) {
        StringBuilder builder = new StringBuilder("SELECT m, (SELECT SUM(know.level + 1) FROM PackageKnowledge as know WHERE know.member = m ");
        if(sbr!=null)
            builder.append("AND know.pkg.sbr = :sbr");

        builder.append(") as knowscore FROM Member m ORDER BY knowscore DESC");

        Query query = em.createQuery(builder.toString());
        if(sbr!=null) {
            query.setParameter("sbr", sbr);
        }

        query.setMaxResults(10);
        List<Object[]> result = ensureCollectionsFetched(query.getResultList(),0);
        log.info("Size of best members " + String.valueOf(result.size()));
        for (Object[] objects : result) {
            objects[1] = countMembersKnowScore((Member)objects[0] ,sbr);
        }

        return result;
    }



    //private helpers

    private Long countMembersKnowScore(Member member, SBR sbr) {
        if(member==null)
            throw new NullPointerException("member");
        if(sbr==null)
            throw new NullPointerException("sbr");

        Long result  = 0L;

        for (Knowledge know : member.getKnowledges()) {
            if(know instanceof PackageKnowledge)
            {
                if(sbr.equals(((PackageKnowledge)know).getPackage().getSbr())) {
                    result +=  Double.valueOf(Math.pow(2, know.getLevel())).longValue();
                }
            }

        }

        return result;
    }

    private TypedQuery<Member> getStandartQueryForAllMembers() {
        return getStandartQueryForAllMembers(null);
    }

    private TypedQuery<Member> getStandartQueryForAllMembers(String filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> criteria = cb.createQuery(Member.class);
        Root<Member> member = criteria.from(Member.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(member).orderBy(cb.asc(cb.lower(member.<String>get("name"))));
        if(filter!=null && !filter.isEmpty()) {
            criteria.where(cb.like(cb.lower(member.<String>get("name")), "%" + filter + "%"));
        }
        return em.createQuery(criteria);
    }

    // instead of fetchtype = eager
    private Member ensureCollectionsFetched(Member member) {
        if(member==null)
            return null;
        if(member.getKnowledges()!=null)
            member.getKnowledges().size();
        if(member.getMembersbrs()!=null)
            member.getMembersbrs().size();

        return member;
    }

    private List<Member> ensureCollectionsFetched(List<Member> members) {
        if(members==null)
            return null;

        for(Member m : members) {
            ensureCollectionsFetched(m);
        }
        return members;
    }

    private List<Object[]> ensureCollectionsFetched(List<Object[]> members, int index) {
        for (Object[] objects : members) {
            ensureCollectionsFetched((Member)objects[index]);
        }

        return members;
    }

    private void nullCoachIfExists(Member member) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SBR> criteria = cb.createQuery(SBR.class);
        Root<SBR> sbrroot = criteria.from(SBR.class);


        criteria.select(sbrroot).where(cb.equal(sbrroot.get("coach"), member));

        for(SBR sbr : em.createQuery(criteria).getResultList()) {
            sbr.setCoach(null);
            em.merge(sbr);
        }
    }


}
