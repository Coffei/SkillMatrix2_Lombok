package com.redhat.gss.skillmatrix.data.dao.producers;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.model.SBR_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/14/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
@RequiredArgsConstructor
public class SbrProducerDB implements SbrProducer {
    private final Logger log = Logger.getLogger(getClass().getName());

    @NonNull
    private EntityManager em;

    @NonNull
    private UserTransaction transaction;
    
    private List<Filter> filters = new LinkedList<Filter>();
    private List<Ordering> orders = new LinkedList<Ordering>();
    
    

    private Integer maxRecords;
    private Integer startOffset;

    @Override
    public SbrProducer filterId(final long id) {
        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return cb.equal(root.get(SBR_.id), id);
            }
        });

        return this;
    }

    @Override
    public SbrProducer filterName(@NonNull final String nameFragment) {
        if(nameFragment.isEmpty())
            throw new IllegalArgumentException("nameFragment cannot be empty");

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(SBR_.name)), "%" + nameFragment.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public SbrProducer filterNameExact(@NonNull final String name) {
        if(name.isEmpty())
            throw new IllegalArgumentException("name cannot be empty");

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return cb.equal(root.get(SBR_.name), name);
            }
        });

        return this;
    }

    @Override
    public SbrProducer filterMember(@NonNull final Member member) throws MemberInvalidException {
        if(member.getId()==null)
            throw new MemberInvalidException("member has no db ID", new NullPointerException("member.id"), member);

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return cb.isMember(member, root.get(SBR_.members));
            }
        });

        return this;
    }

    @Override
    public SbrProducer sortName(final boolean ascending) {
        orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return createOrder(ascending, cb, root.get(SBR_.name));
            }
        });

        return this;
    }

    @Override
    public SbrProducer sortMembersCount(final boolean ascending) {
        this.orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return createOrder(ascending, cb, cb.size(root.get(SBR_.members)));
            }
        });

        return this;
    }

    @Override
    public SbrProducer sortPackagesCount(final boolean ascending) {
        this.orders.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query) {
                return createOrder(ascending, cb, cb.size(root.get(SBR_.packages)));
            }
        });

        return this;
    }

    @Override
    public SbrProducer recordsCount(int count) {
        if(count > 0)
            this.maxRecords = count;

        return this;
    }

    @Override
    public SbrProducer recordsStart(int start) {
        if(start >= 0)
            this.startOffset = start;

        return this;
    }

    @Override
    public List<SBR> getSbrs() {
        try {
            transaction.begin();
        } catch (NotSupportedException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        } catch (SystemException e) {
            log.severe(String.format("%s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        }

        val cb = em.getCriteriaBuilder();
        val criteria = cb.createQuery(SBR.class);
        val sbrRoot = criteria.from(SBR.class);

        criteria.select(sbrRoot);

        //apply filters
        val predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, sbrRoot, criteria));
        }
        criteria.where(predicates.toArray(new Predicate[0]));

        //add orders
        // for now just add the first one
        if(!this.orders.isEmpty())
            criteria.orderBy(this.orders.get(0).apply(cb, sbrRoot, criteria));

        val query = em.createQuery(criteria);

        if(this.maxRecords!=null)
            query.setMaxResults(this.maxRecords);

        if(this.startOffset!=null)
            query.setFirstResult(this.startOffset);

        val result = fetchCollections(query.getResultList());

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

        //return the results
        return result;
    }

    @Override
    public long getCount() {
        val cb = em.getCriteriaBuilder();
        val criteria = cb.createQuery(Long.class);
        val sbrRoot = criteria.from(SBR.class);

        criteria.select(cb.count(sbrRoot));

        //apply filters
        val predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, sbrRoot, criteria));
        }
        criteria.where(predicates.toArray(new Predicate[0]));

       // we can ignore order
       val query = em.createQuery(criteria);


        //return the results
        return query.getResultList().get(0);
    }

    private Order createOrder(boolean ascending, CriteriaBuilder cb, Expression<?> sortedExpression) {
        if(ascending) {
            return cb.asc(sortedExpression);
        } else {
            return cb.desc(sortedExpression);
        }
    }

    private SBR fetchCollections(@NonNull SBR sbr) {
        sbr.getCoaches().size();
        sbr.getMembers().size();
        sbr.getPackages().size();

        return sbr;
    }

    private List<SBR> fetchCollections(@NonNull List<SBR> sbrs) {
        for(SBR sbr : sbrs) {
            fetchCollections(sbr);
        }

        return sbrs;
    }

    private static interface Filter {
        public Predicate apply(CriteriaBuilder cb, Root<SBR> root, CriteriaQuery query);
    }

    private static interface Ordering {
        public javax.persistence.criteria.Order apply(CriteriaBuilder cb, Root<SBR> root,  CriteriaQuery query);
    }
}
