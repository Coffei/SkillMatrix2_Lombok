package com.redhat.gss.skillmatrix.data.dao.producers;

import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Database implementation of {@link MemberProducer}.
 * User: jtrantin
 * Date: 8/7/13
 * Time: 10:48 AM
 */
public class MemberProducerDB implements MemberProducer {
    private static final int EXPERT_LEVEL = 2;

    private List<Filter> filters;
    private List<Ordering> orders;
    private EntityManager em;

    private Integer maxRecords;
    private Integer startOffset;

    private Integer sortKnowledgesAtLevel;
    private SBR sortKnowScoreOfSBR;
    private boolean ascending = true;

    public MemberProducerDB(EntityManager em) {
        if(em==null)
            throw new IllegalArgumentException("entity manager must be provided", new NullPointerException("em"));

        this.filters = new LinkedList<Filter>();
        this.orders = new LinkedList<Ordering>();
        this.em = em;
    }

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
    public MemberProducer filterNick(final String nick) {
        if(nick==null)
            throw new NullPointerException("nick");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.nick).as(String.class)), "%" + nick.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterNickExact(final String nick) {
        if(nick==null)
            throw new NullPointerException("nick");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.equal(root.get(Member_.nick), nick);
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterName(final String name) {
        if(name==null)
            throw new NullPointerException("name");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.name).as(String.class)), "%" + name.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterEmail(final String email) {
        if(email==null)
            throw new NullPointerException("email");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.email).as(String.class)), "%" + email.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterRole(final String role) {
        if(role==null)
            throw new NullPointerException("role");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Member_.role).as(String.class)), "%" + role.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterGeo(final GeoEnum geo) {
        if(geo==null)
            throw new NullPointerException("geo");

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
    public MemberProducer filterExtension(final String extension) {
        if(extension==null)
            throw new NullPointerException("extension");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.like(root.get(Member_.extension).as(String.class), "%" + extension + "%");
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterSBRMembership(final SBR sbr) {
        if(sbr==null)
            throw new NullPointerException("sbr");

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query) {
                return cb.isMember(sbr, root.get(Member_.sbrs));
            }
        });

        return this;
    }

    @Override
    public MemberProducer filterLanguage(final String language) {
        if(language==null)
            throw new NullPointerException("language");

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
    public MemberProducer filterKnowledgeLevelCount(final int level, final int count, final OperatorEnum operatorEnum) {
        if(operatorEnum==null)
            throw new NullPointerException("operatorEnum");

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
    public MemberProducer filterKnowScoreOfSBR(final int score, final OperatorEnum operatorEnum, final SBR sbr) {
        if(operatorEnum==null)
            throw new NullPointerException("operatorEnum");
        if(sbr==null)
            throw new NullPointerException("sbr");

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
    public MemberProducer sortKnowScoreOfSBR(final SBR sbr, final boolean ascending) {
        if(sbr==null)
            throw new NullPointerException("sbr");

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
    public MemberProducer sortKnowledgesAtLevel(final int level, final boolean ascending) {
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

                Join<Member, Geo> geojoin =  root.join(Member_.geo);
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
        this.maxRecords = count;

        return this;
    }

    @Override
    public MemberProducer recordsStart(int start) {
        this.startOffset = start;

        return this;
    }

    @Override
    public List<Member> getMembers() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);
        Root<Member> root = query.from(Member.class);

        query.select(root);

        //add filters
        List<Predicate> predicates = new LinkedList<Predicate>();
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

        TypedQuery<Member> typedQuery = em.createQuery(query);

        //apply max count and start offset
        if(maxRecords!=null)
            typedQuery.setMaxResults(maxRecords);

        if(startOffset!=null)
            typedQuery.setFirstResult(startOffset);

        //and return the results
        return typedQuery.getResultList();
    }

    //TODO: check- this may be possible to do via joins {@see sortGeo}
    // need  to check whether joins can or cannot intefere with other stuff
    // also efficiency isa question (one query can have both join and subquery for the same thing)
    private List<Member> getMembersNoDB(CriteriaQuery<Member> criteria) {
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

        return members.subList(offset, maxresults);

    }

    @Override
    public long getCount() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Member> root = query.from(Member.class);

        query.select(cb.count(root));

        //add filters
        List<Predicate> predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, root, query));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        //ignoring order

        TypedQuery<Long> typedQuery = em.createQuery(query);

        //apply max count and start offset
        if(maxRecords!=null)
            typedQuery.setMaxResults(maxRecords);

        if(startOffset!=null)
            typedQuery.setFirstResult(startOffset);

        //and return the results
        return typedQuery.getSingleResult();

    }

    @Override
    public long getTotalCount() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Member> root = query.from(Member.class);
        root.alias("member");

        query.select(cb.count(root));

        //add filters
        List<Predicate> predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, root, query));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        //ignoring order, max results and start offset

        TypedQuery<Long> typedQuery = em.createQuery(query);

        //and return the results
        return typedQuery.getSingleResult();

    }

    /**
     * Helper method to do the sorting by number of knowledges at certain level.
     * @return
     */
    private List<Member> sortMembersByKnowledgesAtLevel(List<Member> tosort) {
        System.out.println("Level: " + sortKnowledgesAtLevel + "\nBefore:");
        for(Member m : tosort)
            System.out.println("Member: " + m.getNick());

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
    private List<Member> sortMembersByKnowScoreOfSbr(List<Member> tosort) {
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

            private int getKnowScore(SBR sbr, Member member) {
                int score = 0;
                for(Knowledge know : member.getKnowledges()) {
                    if(know instanceof PackageKnowledge) {
                        PackageKnowledge pkgKnow = (PackageKnowledge)know;
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


    private static interface Filter {
        public Predicate apply(CriteriaBuilder cb, Root<Member> root, CriteriaQuery query);
    }

    private static interface Ordering {
        public javax.persistence.criteria.Order apply(CriteriaBuilder cb, Root<Member> root,  CriteriaQuery query);
    }
}
