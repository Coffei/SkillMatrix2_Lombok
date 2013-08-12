package com.redhat.gss.skillmatrix.data.dao.producers.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/6/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public enum OperatorEnum {

    BIGGER, SMALLER, EQUAL;

    public <Y extends Comparable<? super Y>> Predicate createPredicate(CriteriaBuilder cb, Expression<? extends Y> first, Expression<? extends Y> second) {
        switch (this) {
            case BIGGER:
                return cb.greaterThan(first, second);
            case EQUAL:
                return cb.equal(first, second);
            case SMALLER:
                return cb.lessThan(first, second);
            default:
                //should never happen!
                assert false;

        }
        return null;
    }
}
