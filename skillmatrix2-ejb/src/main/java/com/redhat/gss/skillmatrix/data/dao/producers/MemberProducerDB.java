package com.redhat.gss.skillmatrix.data.dao.producers;

import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;

/**
 * Database implementation of {@link MemberProducer}.
 * User: jtrantin
 * Date: 8/7/13
 * Time: 10:48 AM
 */
@RequiredArgsConstructor
@Log
public class MemberProducerDB implements MemberProducer {
    private static final int EXPERT_LEVEL = 2;

    private List<Filter> filters = new LinkedList<Filter>();
    private List<Ordering> orders = new LinkedList<Ordering>();
    @NonNull //required in constructor
    private EntityManager em;
    @NonNull //required in constructor
    private UserTransaction transaction;

    private Integer maxRecords;
    private Integer startOffset;

    private Integer sortKnowledgesAtLevel;
    private SBR sortKnowScoreOfSBR;
    private boolean ascending = true;

    @Override
    public MemberProducer filterId(final long id) {
        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.equal(root.get(Member_.id), id);
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterNick(@NonNull final String nick) {
        if(nick.isEmpty())
            throw new IllegalArgumentException("nick cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.nick).as(String.class)), "%" + nick.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterNickExact(@NonNull final String nick) {
        if(nick.isEmpty())
            throw new IllegalArgumentException("nick cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.equal(root.get(Member_.nick), nick);
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterName(@NonNull final String name) {
        if(name.isEmpty())
            throw new IllegalArgumentException("name cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.name).as(String.class)), "%" + name.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterEmail(@NonNull final String email) {
        if(email.isEmpty())
            throw new IllegalArgumentException("email cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.email).as(String.class)), "%" + email.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterRole(@NonNull final String role) {
        if(role.isEmpty())
            throw new IllegalArgumentException("role cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.role).as(String.class)), "%" + role.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterGeo(@NonNull final GeoEnum geo) {
        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                Subquery<Geo> subquery = query.subquery(Geo.class);
                Root<Geo> geoRoot = subquery.from(Geo.class);

                subquery.select(geoRoot).where(cb.equal(root.get(Member_.geo), geoRoot), cb.equal(geoRoot.get(Geo_.geocode), geo));

                return cb.exists(subquery);
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterExtension(@NonNull final String extension) {
        if(extension.isEmpty())
            throw new IllegalArgumentException("extension cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(root.get(Member_.extension).as(String.class), "%" + extension + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterSBRMembership(@NonNull final SBR sbr) throws SbrInvalidException {
        if(sbr.getId()==null)
            throw new SbrInvalidException("sbr has no DB ID", new NullPointerException("sbr.id"), sbr);

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.isMember(sbr, root.get(Member_.sbrs));
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterLanguage(@NonNull final String language) {
        if(language.isEmpty())
            throw new IllegalArgumentException("language cannot be empty");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                Subquery<LanguageKnowledge> subquery = query.subquery(LanguageKnowledge.class);
                Root<LanguageKnowledge> lang = subquery.from(LanguageKnowledge.class);

                //subquery to find all language knowledges of the memberr that are like *language*
                subquery.select(lang).where(cb.equal(lang.get(LanguageKnowledge_.member), root), cb.like(cb.lower(lang.get(LanguageKnowledge_.language)), "%" + language.toLowerCase(Locale.ENGLISH) + "%"));

                //keep the member if such a language knowledge exists.
                return cb.exists(subquery);
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterKnowledgeOfPackage(final Package pkg, final int level, final OperatorEnum operatorEnum) throws PackageInvalidException {
        if(pkg==null)
            throw new PackageInvalidException("null package", new NullPointerException("pkg"));
        if(pkg.getId()==null)
            throw new PackageInvalidException("invalid package", new NullPointerException("pkg.id"));

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                //get members' ids that are in accordance with this predicate
                //TODO: convert to typed query
                String operator = operatorEnum.createSQLTextPredicate();
                Query idquery = em.createNativeQuery("SELECT member_id FROM PACKAGEKNOWLEDGE WHERE PKG_ID = :pkg AND level " + operator + " :level");
                idquery.setParameter("pkg", pkg.getId());
                idquery.setParameter("level", level);

                List ids = idquery.getResultList();

                return root.get(Member_.id).in(ids);
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterKnowledgeLevelCount(final int level, final int count, @NonNull final OperatorEnum operatorEnum) {
        if(!(level==0 || level == 1 || level == 2))
            throw new IllegalArgumentException("illegal level, permitted values are 0, 1, 2");
        if(count < 0)
            throw new IllegalArgumentException("count cannot be negative");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<PackageKnowledge> pkgKnow = subquery.from(PackageKnowledge.class);

                subquery.select(cb.count(pkgKnow)).where(cb.equal(pkgKnow.get(PackageKnowledge_.member), root), cb.equal(pkgKnow.get(PackageKnowledge_.level), level));

                return operatorEnum.createPredicate(cb, subquery, cb.literal((long)count));
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterKnowScoreOfSBR(final int score, @NonNull final OperatorEnum operatorEnum, @NonNull final SBR sbr) throws SbrInvalidException {
        if(sbr.getId()==null)
            throw new SbrInvalidException("sbr has no DB ID", new NullPointerException("sbr.id"), sbr);

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                Subquery<Integer> knowSubquery = query.subquery(Integer.class);
                Root<PackageKnowledge> pkgKnow = knowSubquery.from(PackageKnowledge.class);


                Subquery<Package> pkgSubquery = query.subquery(Package.class);
                Root<Package> pkg = pkgSubquery.from(Package.class);

                //select all packages in the specified sbr
                pkgSubquery.select(pkg).where(cb.equal(pkg.get(Package_.sbr), sbr));


                //select to get persons' KnowScore of certain SBR. KnowScore is actually 2^level, but there is no power
                // function in Criteria API so the formula used is 1/2x^2 + 1/2x + 1, which gives the same values for 0,1,2.
                // see notes below to understand how the function is composed
                knowSubquery.select(
                        cb.sum(// an aggregate function
                                cb.sum(// 1/2x^2 + 1/2x + 1
                                        cb.sum(// 1/2x^2 + 1/2x
                                                cb.prod( // 1/2x^2
                                                        cb.prod( // x^2
                                                                pkgKnow.get(PackageKnowledge_.level).as(Double.class),
                                                                pkgKnow.get(PackageKnowledge_.level).as(Double.class)),
                                                        0.5),
                                                cb.prod( // 1/2x
                                                        pkgKnow.get(PackageKnowledge_.level).as(Double.class),
                                                        0.5)),
                                        1)).as(Integer.class))
                        .where(cb.equal(pkgKnow.get(PackageKnowledge_.member), root), pkgKnow.get(PackageKnowledge_.pkg).in(pkgSubquery));

                return operatorEnum.createPredicate(cb, knowSubquery, cb.literal(score));

            }
        });

        return this;
    }


    //sorters
    @Override
    public MemberProducer sortNick(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return createOrder(ascending, cb, root.get(Member_.nick));
            }
        });

        return this;
    }

    @Override
    //WORKAROUND: as subquery cannot be used in order by clause in JPQL at the moment, we have to fallback to sorting in JAVA
    public MemberProducer sortKnowScoreOfSBR(@NonNull final SBR sbr, final boolean ascending) throws SbrInvalidException {
        if (sbr.getId()==null)
            throw new SbrInvalidException("sbr has no DB ID", new NullPointerException("sbr.id"), sbr);

        this.sortKnowScoreOfSBR = sbr;
        this.sortKnowledgesAtLevel = null; // because we use only one order, need to remember which was last
        this.orders.clear();
        this.ascending = ascending;

        // Order by subquery cannot be used, thank you JPQL!
       /* orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                Subquery<Integer> knowSubquery = query.subquery(Integer.class);
                Root<PackageKnowledge> pkgKnow = knowSubquery.from(PackageKnowledge.class);


                Subquery<Package> pkgSubquery = query.subquery(Package.class);
                Root<Package> pkg = pkgSubquery.from(Package.class);

                //select all packages in the specified sbr
                pkgSubquery.select(pkg).where(cb.equal(pkg.get(Package_.sbr), sbr));


                //select to get persons' KnowScore of certain SBR. KnowScore is actually 2^level, but there is no power
                // function in Criteria API so the formula used is 1/2x^2 + 1/2x + 1, which gives the same values for 0,1,2.
                // see notes below to understand how the function is composed
                knowSubquery.select(
                        cb.sum(// an aggregate function
                                cb.sum(// 1/2x^2 + 1/2x + 1
                                        cb.sum(// 1/2x^2 + 1/2x
                                                cb.prod( // 1/2x^2
                                                        cb.prod( // x^2
                                                                pkgKnow.get(PackageKnowledge_.level).as(Double.class),
                                                                pkgKnow.get(PackageKnowledge_.level).as(Double.class)),
                                                        0.5),
                                                cb.prod( // 1/2x
                                                        pkgKnow.get(PackageKnowledge_.level).as(Double.class),
                                                        0.5)),
                                        1)).as(Integer.class))
                        .where(cb.equal(pkgKnow.get(PackageKnowledge_.member), root), pkgKnow.get(PackageKnowledge_.pkg).in(pkgSubquery));


                return createOrder(ascending, cb, knowSubquery);

            }
        });*/

        return this;
    }

    @Override
    public MemberProducer sortName(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return createOrder(ascending, cb, root.get(Member_.name));
            }
        });

        return this;
    }

    @Override
    public MemberProducer sortEmail(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return createOrder(ascending, cb, root.get(Member_.email));
            }
        });

        return this;
    }

    @Override
    //WORKAROUND: as subquery cannot be used in order by clause in JPQL at the moment, we have to fallback to sorting in JAVA
    public MemberProducer sortKnowledgesAtLevel(final int level, final boolean ascending) {
        if(!(level==0 || level==1 || level==2))
            throw new IllegalArgumentException("invalid level, permitted values are 0, 1, 2");

        this.sortKnowledgesAtLevel = level;
        this.sortKnowScoreOfSBR = null; // because we use only one order, need to remember which was last
        this.orders.clear();
        this.ascending = ascending;

        // Order by subquery cannot be used, thank you JPQL!
       /* orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                Subquery<Long> expertQuery = query.subquery(Long.class);
                Root<PackageKnowledge> pkgKnow = expertQuery.from(PackageKnowledge.class);

                //subquery to find all packageknowledges of the member that have expert knowledge
                expertQuery.select(cb.count(pkgKnow)).where(cb.equal(pkgKnow.get(PackageKnowledge_.member), root), cb.equal(pkgKnow.get(PackageKnowledge_.level), level));

                return createOrder(ascending, cb, expertQuery);
            }
        });*/

        return this;

    }

    @Override
    public MemberProducer sortRole(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return createOrder(ascending, cb, root.get(Member_.role));
            }
        });

        return this;
    }

    @Override
    public MemberProducer sortGeo(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {

                Join<Member, Geo> geojoin =  root.join(Member_.geo, JoinType.LEFT);
                return createOrder(ascending, cb, geojoin.get(Geo_.geocode));
            }
        });

        return this;
    }

    @Override
    public MemberProducer sortExtension(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return createOrder(ascending, cb, root.get(Member_.extension));
            }
        });

        return this;
    }

    private Order createOrder(boolean ascending, CriteriaBuilder cb, Expression<?> sortedExpression) {
        if(ascending) {
            return cb.asc(sortedExpression);
        } else {
            return cb.desc(sortedExpression);
        }
    }

    @Override
    public MemberProducer recordsCount(int count) {
        if(count > 0)
            this.maxRecords = count;

        return this;
    }

    @Override
    public MemberProducer recordsStart(int start) {
        if(start >= 0)
            this.startOffset = start;

        return this;
    }

    @Override
    public List<Member> getMembers() {
        try {
            transaction.begin();
        } catch (NotSupportedException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (SystemException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        }
        em.joinTransaction();

        val cb = em.getCriteriaBuilder();
        val query = cb.createQuery(Member.class);
        val root = query.from(Member.class);

        query.select(root);

        //add filters
        val predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, root, query));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        //add orders, for now just add the first one
        if(this.orders.isEmpty() && (this.sortKnowledgesAtLevel != null || this.sortKnowScoreOfSBR != null)) {
            // some of the JPA unsupported order has to be used, fallback to JAVA handling

            return getMembersNoDB(query);
        }

        if(!orders.isEmpty()) {
            query.orderBy(orders.get(0).apply(cb, root, query));
        }

        val typedQuery = em.createQuery(query);

        //apply max count and start offset
        if(maxRecords!=null)
            typedQuery.setMaxResults(maxRecords);

        if(startOffset!=null)
            typedQuery.setFirstResult(startOffset);

        //and return the results
        val result = fetchCollections(typedQuery.getResultList());

        try {
            transaction.commit();
        } catch (RollbackException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (HeuristicMixedException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (HeuristicRollbackException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (SystemException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        }
        return result;
    }

    // Java fallback for unsupported sorters
    private List<Member> getMembersNoDB(CriteriaQuery<Member> criteria) {
        try {
            transaction.begin();
        } catch (NotSupportedException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (SystemException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        }
        em.joinTransaction();

        // get the members
        List<Member> members = em.createQuery(criteria).getResultList();

        //sort members
        if(this.sortKnowScoreOfSBR != null) {
            members = sortMembersByKnowScoreOfSbr(members);
        } else if (this.sortKnowledgesAtLevel != null) {
            members = sortMembersByKnowledgesAtLevel(members);
        }

        //start offset and max results
        int offset;
        if(this.startOffset == null) {
            offset = 0;
        } else {
            offset = this.startOffset.intValue();
        }

        int maxresults;
        if(this.maxRecords == null) {
            maxresults = members.size();
        } else {
            maxresults = this.maxRecords.intValue();
        }

        val result = fetchCollections(members.subList(offset, maxresults));

        try {
            transaction.commit();
        } catch (RollbackException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (HeuristicMixedException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (HeuristicRollbackException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (SystemException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        }

        return result;

    }

    @Override
    public long getCount() {
        val cb = em.getCriteriaBuilder();
        val query = cb.createQuery(Long.class);
        val root = query.from(Member.class);

        query.select(cb.count(root));

        //add filters
        val predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, root, query));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        //ignoring order

        val typedQuery = em.createQuery(query);

        //and return the results
        return typedQuery.getSingleResult();

    }

    /**
     * Helper method to do the sorting by number of knowledges at certain level.
     * @return
     */
    private List<Member> sortMembersByKnowledgesAtLevel(List<Member> tosort) {
       Collections.sort(tosort, new Comparator<Member>() {
            @Override
            public int compare(Member member1, Member member2) {
                int count1 = getNumberOfPKgsAtLevel(MemberProducerDB.this.sortKnowledgesAtLevel, member1);
                int count2 = getNumberOfPKgsAtLevel(MemberProducerDB.this.sortKnowledgesAtLevel, member2);

                if(ascending) {
                    return count1 - count2;
                } else {
                    return count2 - count1;
                }
            }

            private int getNumberOfPKgsAtLevel(int level, Member member) {
                int count = 0;
                for(Knowledge know : member.getKnowledges()) {
                    if(know instanceof PackageKnowledge) {
                        if(know.getLevel().intValue() == level) { //we found a package knowledge at the level!
                            count ++;
                        }
                    }
                }

                return count;
            }
        });

        return tosort;
    }

    /**
     * Helper method to do the sorting by KnowScore of SBR.
     * @return
     */
    private List<Member> sortMembersByKnowScoreOfSbr(@NonNull List<Member> tosort) {
        Collections.sort(tosort, new Comparator<Member>() {
            @Override
            public int compare(Member member1, Member member2) {
                int count1 = getKnowScore(MemberProducerDB.this.sortKnowScoreOfSBR, member1);
                int count2 = getKnowScore(MemberProducerDB.this.sortKnowScoreOfSBR, member2);

                if(ascending) {
                    return count1 - count2;
                } else {
                    return count2 - count1;
                }
            }

            private int getKnowScore(@NonNull SBR sbr, @NonNull Member member) {
                int score = 0;
                for(Knowledge know : member.getKnowledges()) {
                    if(know instanceof PackageKnowledge) {
                        val pkgKnow = (PackageKnowledge)know;
                        if(pkgKnow.getPackage().getSbr().getId().equals(sbr.getId())) { //we found a package knowledge at the level!
                            score += Math.pow(2, pkgKnow.getLevel());
                        }
                    }
                }

                return score;
            }
        });

        return tosort;
    }

    private Member fetchCollections(@NonNull Member member) {
        member.getSbrs().size();
        return member;
    }

    private List<Member> fetchCollections(@NonNull List<Member> members) {
        for(Member member : members) {
            fetchCollections(member);
        }

        return members;
    }


    private static interface Filter {
        public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query);
    }

    private static interface Ordering {
        public javax.persistence.criteria.Order apply(CriteriaBuilder cb, Root<Member> root,  CriteriaQuery query);
    }
}
