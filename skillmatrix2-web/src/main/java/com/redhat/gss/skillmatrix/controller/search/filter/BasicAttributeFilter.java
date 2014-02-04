package com.redhat.gss.skillmatrix.controller.search.filter;

/**
 * Interfaces used to ensure type compatibilty of all filters that use basic.xhtml as their filter page.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BasicAttributeFilter {

    String getValue();
    void setValue(String value);

}
