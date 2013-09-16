package com.redhat.gss.skillmatrix.controllers.sorthelpers.util;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;

/**
* Created with IntelliJ IDEA.
* User: jtrantin
* Date: 9/13/13
* Time: 2:22 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class Filter<T> {
    protected String value;

    public abstract T apply(T producer);

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter filter = (Filter) o;

        if (value != null ? !value.equals(filter.value) : filter.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
