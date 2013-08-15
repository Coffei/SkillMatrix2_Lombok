package com.redhat.gss.skillmatrix.data.dao.producers.interfaces;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.SBR;

import java.util.List;

/**
 * Sbr producer factory inteface. Used for querying for {@link com.redhat.gss.skillmatrix.model.SBR} entity.
 * User: jtrantin
 * Date: 8/14/13
 * Time: 9:12 AM
 */
public interface SbrProducer {

    /**
     * Adds a filter of ID. Only records with the specified ID are considered (usually just one record).
     * @param id id to filter
     * @return itself, good for chaining.
     */
    SbrProducer filterId(long id);

    /**
     * Adds a filter of name fragment. Only records with the specified {@code nameFragment} in name are considered.
     * The comparison is case-insensitive.
     * @param nameFragment fragment of SBR name, cannot be empty or null
     * @return itself, good for chaining.
     */
    SbrProducer filterName(String nameFragment);


    /**
     * Adds a filter of exact name. Only records with exactly the name are considered. The comparison is case-sensitive.
     * @param name name of the SBR, cannot be empty or null
     * @return itself, good for chaining.
     */
    SbrProducer filterNameExact(String name);

    /**
     * Adds a filter of member. Only SBRs with the specified member are considered. This filter should choose the same
     * SBRs as in {@code member.getSbrs()}.
     * @param member valid member
     * @return itself, good for chaining.
     * @throws com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException when the member is not valid
     */
    SbrProducer filterMember(Member member) throws MemberInvalidException;

    /**
     * Adds an ordering by name.
     * @param ascending specifies whether the ordering should be ascending or descending.
     * @return itself, good for chaining.
     */
    SbrProducer sortName(boolean ascending);

    /**
     * Adds an ordering by members count.
     * @param ascending specifies whether the ordering should be ascending or descending.
     * @return itself, good for chaining.
     */
    SbrProducer sortMembersCount(boolean ascending);

    /**
     * Adds an ordering by packages count.
     * @param ascending specifies whether the ordering should be ascending or descending.
     * @return itself, good for chaining.
     */
    SbrProducer sortPackagesCount(boolean ascending);

    /**
     * Adds a limit of maximum records returned. After this method is called, {@link #getSbrs()} should never return
     * more than specified number of records. Invalid {@code count} is ignored.
     * @param count max number of records.
     * @return itself, good for chaining.
     */
    SbrProducer recordsCount(int count);

    /**
     * Adds a start offset in records returned. This can be used with {@link #recordsCount(int)} to create paging.
     * @param start index of first record (indexed from 0). Invalid value is ignored.
     * @return itself, good for chaining.
     */
    SbrProducer recordsStart(int start);

    //core methods

    /**
     * Executes the query and returns the result.
     * @return list of sbrs as result of the query.
     */
    List<SBR> getSbrs();

    /**
     * Count of sbrs that satisfy all the filters. This method ignores {@link #recordsCount(int)} and {@link #recordsStart(int)}.
     * @return number of records.
     */
    long getCount();
}
