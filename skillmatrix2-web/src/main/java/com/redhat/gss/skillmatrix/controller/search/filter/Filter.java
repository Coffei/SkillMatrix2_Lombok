package com.redhat.gss.skillmatrix.controller.search.filter;

import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Filter {

    /**
     * Encode this filter into a String. This string can be used as a "serialized" form of this filter. To decode, use
     * #{@link #decode(String)}. Note the serialized form must not be too complicated, it must be URL-safe and preferably
     * even human readable.
     * @see #decode(String)
     * @return serialized form of this filter
     */
    public String encode();

    /**
     * Fills this instance of a Filter with a values from the serialized form, i.e. creates a instance of a serialized filter.
     * Note that no new instance is created, the current instance is used, only its data is changed.
     * Also note that the serialized filter must be a filter of the same type.
     * @param filter serialized form of a filter
     * @throws TypeMismatchException when the serialized filter is not compatible with this particular implementation of a filter.
     * @throws IllegalArgumentException when the filter is not a valid serialized filter
     */
    public void decode(String filter) throws TypeMismatchException, IllegalArgumentException;

    /**
     * Apply the filter on {@link MemberModelHelper}. The return value indicates, whether this application was enough
     * or if the filter should be applied on a producer.
     * @param modelHelper modelHelper on which the filter will be applied, must not be null
     * @return true if no further application is needed, false if filter should be applied to {@link MemberProducer}.
     * @see #applyOnProducer(com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer)
     */
    public boolean apply(MemberModelHelper modelHelper);

    /**
     * Apply filter straight to {@link MemberProducer}.
     * @param producer producer on which the filter wil be applied, must not be null.
     */
    public void applyOnProducer(MemberProducer producer);

    /**
     * Explain the function of this filter to a user. The description has to be user readable and acceptable.
     * @return human-level description of this filter
     */
    public String explain();

}
