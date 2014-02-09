package com.redhat.gss.skillmatrix.controllers.sorthelpers.util;

import lombok.Getter;
import lombok.Setter;

/**
* Created with IntelliJ IDEA.
* User: jtrantin
* Date: 9/13/13
* Time: 2:22 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class Filter<T> {
	@Getter @Setter
    protected String value;

    public abstract T apply(T producer);

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
