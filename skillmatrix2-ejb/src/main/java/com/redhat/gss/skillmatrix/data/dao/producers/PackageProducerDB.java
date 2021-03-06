package com.redhat.gss.skillmatrix.data.dao.producers;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Database implementation of {@link PackageProducer}.
 * User: jtrantin
 * Date: 8/15/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
@RequiredArgsConstructor
public class PackageProducerDB implements PackageProducer {

	@NonNull
    private EntityManager em;

    private List<Filter> filters = new LinkedList<Filter>();
    private List<Ordering> orderings = new LinkedList<Ordering>();

    private Integer startOffset;
    private Integer maxResults;

    @Override
    public PackageProducer filterId(final long id) {
        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                return cb.equal(root.get(Package_.id), id);
            }
        });

        return this;
    }

    @Override
    public PackageProducer filterName(@NonNull final String nameFragment) {
        if(nameFragment.isEmpty())
            throw new IllegalArgumentException("nameFragment cannot be empty");

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                return cb.like(cb.lower(root.get(Package_.name)), "%" + nameFragment.toLowerCase(Locale.ENGLISH) + "%");
            }
        });

        return this;
    }

    @Override
    public PackageProducer filterNameExact(@NonNull final String name) {
        if(name.isEmpty())
            throw new IllegalArgumentException("name cannot be empty");

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                return cb.equal(root.get(Package_.name), name);
            }
        });

        return this;
    }

    @Override
    public PackageProducer filterSBR(@NonNull final SBR sbr) throws SbrInvalidException {
        if(sbr.getId()==null)
            throw new SbrInvalidException("sbr has no DB ID", new NullPointerException("sbr.id"), sbr);

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                return cb.equal(root.get(Package_.sbr), sbr);
            }
        });

        return this;
    }

    @Override
    public PackageProducer filterPeopleAtKnowledgeLevel(final int level, @NonNull final OperatorEnum operator, final int count) {
        if(!(level==0 || level==1 || level==2))
            throw new IllegalArgumentException("invalid level, permitted values are 0, 1, 2");

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                val subquery = query.subquery(Long.class);
                val  pkgKnowRoot = subquery.from(PackageKnowledge.class);
                subquery.select(cb.count(pkgKnowRoot)).where(cb.equal(pkgKnowRoot.get(PackageKnowledge_.pkg), root), cb.equal(pkgKnowRoot.get(PackageKnowledge_.level), level));

                return operator.createPredicate(cb, subquery, cb.literal((long)count));

            }
        });

        return this;
    }

    @Override
    public PackageProducer filterKnowledgeByPerson(final Member member, final int level) throws MemberInvalidException{
        if(member==null)
            throw new MemberInvalidException("null member", new NullPointerException("member"));
        if(member.getId() == null)
            throw new MemberInvalidException("member has no DB ID", new NullPointerException("member.id"));

        filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                //get all the relevant pkgs
                val pkgCriteria = cb.createQuery(Package.class);
                val knowRoot = pkgCriteria.from(PackageKnowledge.class);
                pkgCriteria.select(knowRoot.get(PackageKnowledge_.pkg)).where(cb.equal(knowRoot.get(PackageKnowledge_.member),
                        member), cb.equal(knowRoot.get(PackageKnowledge_.level), level));
                val pkgs = em.createQuery(pkgCriteria).getResultList();

                //create predicate
                return root.in(pkgs);
            }
        });

        return this;
    }

    @Override
    public PackageProducer filterSbrName(@NonNull final String nameFragment) {
        if(nameFragment.trim().isEmpty())  // criteria is empty, just do nothing
            return this;

        this.filters.add(new Filter() {
            @Override
            public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                //first get all the matching sbrs and their packages
                val sbrQuery = cb.createQuery(SBR.class);
                val sbrRoot = sbrQuery.from(SBR.class);
                sbrQuery.select(sbrRoot).where(cb.like(cb.lower(sbrRoot.get(SBR_.name)), "%" + nameFragment.toLowerCase(Locale.ENGLISH) + "%"));

                val sbrs = em.createQuery(sbrQuery).getResultList();

                //add criteria

                return root.get(Package_.sbr).in(sbrs);
            }
        });

        return this;
    }

    @Override
    public PackageProducer sortName(final boolean ascending) {
        this.orderings.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                return createOrder(ascending, cb, cb.lower(root.get(Package_.name)));
            }
        });

        return this;
    }

    @Override
    public PackageProducer sortSbrName(final boolean ascending) {
        this.orderings.add(new Ordering() {
            @Override
            public Order apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query) {
                val join = root.join(Package_.sbr);
                return createOrder(ascending, cb, cb.lower(join.get(SBR_.name)));
            }
        });

        return this;
    }

    @Override
    public PackageProducer recordsStart(int offset) {
        if(offset > 0)
            this.startOffset = offset;

        return this;
    }

    @Override
    public PackageProducer recordsCount(int count) {
        if(count > 0)
            this.maxResults = count;

        return this;
    }

    @Override
    public List<Package> getPackages() {
        val cb = em.getCriteriaBuilder();
        val criteria = cb.createQuery(Package.class);
        val root = criteria.from(Package.class);

        //create filters
        val predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, root, criteria));
        }
        criteria.where(predicates.toArray(new Predicate[0]));

        //create orders
        // for now, just apply the first one
        if(!this.orderings.isEmpty())
            criteria.orderBy(orderings.get(0).apply(cb, root, criteria));

        val query = em.createQuery(criteria);

        //apply start offset and max results
        if(startOffset!=null)
            query.setFirstResult(startOffset);

        if(maxResults!=null)
            query.setMaxResults(maxResults);

        return query.getResultList();
    }

    @Override
    public long getCount() {
        val cb = em.getCriteriaBuilder();
        val criteria = cb.createQuery(Long.class);
        val root = criteria.from(Package.class);

        criteria.select(cb.count(root));

        //create filters
        val predicates = new LinkedList<Predicate>();
        for(Filter filter : this.filters) {
            predicates.add(filter.apply(cb, root, criteria));
        }
        criteria.where(predicates.toArray(new Predicate[0]));

        //ignore order, maxResult and startOffset


        return em.createQuery(criteria).getSingleResult();

    }

    private Order createOrder(boolean ascending, CriteriaBuilder cb, Expression<?> sortedExpression) {
        if(ascending) {
            return cb.asc(sortedExpression);
        } else {
            return cb.desc(sortedExpression);
        }
    }


    private static interface Filter {
        public Predicate apply(CriteriaBuilder cb, Root<Package> root, CriteriaQuery query);
    }

    private static interface Ordering {
        public javax.persistence.criteria.Order apply(CriteriaBuilder cb, Root<Package> root,  CriteriaQuery query);
    }
}
