package com.redhat.gss.skillmatrix.data.dao.producers.interfaces;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/15/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PackageProducer {

    /**
     * Adds a filter of ID
     * @param id ID to be filtered
     * @return itself, good for chaining
     */
    PackageProducer filterId(long id);

    /**
     * Adds a filter of name fragment. Only Packages with {@code nameFragment} in their name are considered.
     * The comparison is <b>case-insensitive</b>.
     * @param nameFragment fragment of the name, must not be null or empty
     * @return itself, good for chaining
     */
    PackageProducer filterName(String nameFragment);

    /**
     * Adds a filter of name. Only Packages with exactly the specified name are considered. The comparison is
     * <b>case-sensitive</b>.
     * @param name name of the package
     * @return itself, good for chaining.
     */
    PackageProducer filterNameExact(String name);

    /**
     * Adds a filter of SBR. Only Packages in the sbr are considered.
     * @param sbr valid sbr
     * @return itself, good for chaining
     * @throws SbrInvalidException when the sbr is invalid and cannot be used by this filter
     */
    PackageProducer filterSBR(SBR sbr) throws SbrInvalidException;

    /**
     * Adds a filter of people with specified level of knowledge. Only packages with more, equal or less people at
     * certain level of knowledge are considered. <br/>
     * Example queries: Give me Packages that more than 10 people are experts in. or
     * Give me Packages that less than 3 people are beginners in.
     * @param level level of knowledge considered
     * @param operator operator of the comparison, determines whether the count of people should be bigger, smaller or equal
     * @param count target count of people
     * @return itself, good for chaining
     */
    PackageProducer filterPeopleAtKnowledgeLevel(int level, OperatorEnum operator, int count);

    /**
     * Adds a filter of knowledge by specific person. Only packages that are known to the specified person at specified level
     * are considered.
     * @param person person that has to know the package
     * @param level level of knowledge required
     * @throws MemberInvalidException when member is not valid and cannot be used for this filter.
     * @return itself, good for chaining
     */
    PackageProducer filterKnowledgeByPerson(Member person, int level) throws MemberInvalidException;

    /**
     * Adds a filter of sbr name fragment. Only packages within an SBR whose name contains the fragment are considered.<br/>
     * Example queries. <i>Give me packages in SBR containing "cluster".</i>
     * @param nameFragment fragment of SBR name
     * @return itself, good for chaining
     */
    PackageProducer filterSbrName(String nameFragment);

    /**
     * Adds an ordering by package name.
     * @param ascending
     * @return itself, good for chaining
     */
    PackageProducer sortName(boolean ascending);

    /**
     * Adds an ordering by Sbr Name
     * @param ascending
     * @return itself, good for chaining
     */
    PackageProducer sortSbrName(boolean ascending);

    /**
     * Adds a start offset. First {@code offset} Packages will not be considered.
     * @param offset target offset
     * @return itself, good for chaining
     */
    PackageProducer recordsStart(int offset);

    /**
     * Specifies maximum count fo results to return. Less than or equal packages will be returned by {@link #getPackages()}.
     * @param count target count
     * @return itself, good for chaining
     */
    PackageProducer recordsCount(int count);

    /**
     * Executes query and returnes the list of results.
     * @return list of query results.
     */
    List<Package> getPackages();

    /**
     * Returns total count of packages that satisfy all the conditions. {@link #recordsCount(int)} and {@link #recordsStart(int)} are ignored.
     * @return number of packages
     */
    long getCount();
}
