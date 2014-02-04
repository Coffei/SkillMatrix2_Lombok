package com.redhat.gss.skillmatrix.controller.search.filter;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface MemberFilter {

    /**
     * ID of this filter. Should be unique as it is used as identifier.
     */
    public String id();

    /**
     * Name of this filter. Should be user-readable since it can be displayed to the user as identifier.
     */
    public String name();

    /**
     * xHTML page bound to this filter.
     */
    public String page();

    /**
     * Type of this filter.
     * @see FilterType
     */
    public FilterType type() default FilterType.BASIC;


}
