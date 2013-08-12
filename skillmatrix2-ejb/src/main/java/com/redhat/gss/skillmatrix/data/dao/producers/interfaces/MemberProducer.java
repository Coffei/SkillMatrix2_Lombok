package com.redhat.gss.skillmatrix.data.dao.producers.interfaces;

import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.SBR;

import java.util.List;

/**
 * Member producer factory. Used for querying for {@link Member}.
 * User: jtrantin
 * Date: 8/6/13
 * Time: 2:12 PM
 */
public interface MemberProducer {

    //traditional filters

    /**
     * Adds a filter of ID. Only records with specified ID are considered.
     * @param id id to filter
     * @return itself, good for chaining.
     */
    MemberProducer filterId(long id);

    /**
     * Adds a filter of nick fragment. Only records with a nick containing the fragment are considered.
     * @param nick fragment of nick, case insensitive
     * @return itself, good for chaining.
     */
    MemberProducer filterNick(String nick);

    /**
     * Adds a filter of nick. Only records with an exactly matched nick are considered.
     * @param nick, case sensitive
     * @return itself, good for chaining.
     */
    MemberProducer filterNickExact(String nick);

    /**
     * Adds afilter of name fragment. Only records with a name containing the fragment are considered.
     * @param name fragment of name, case insensitive
     * @return itself, good for chaining.
     */
    MemberProducer filterName(String name);

    /**
     * Adds a filter of email fragment. Only records with email containing the fragment are considered.
     * @param email fragment of email, case insensitive
     * @return itself, good for chaining.
     */
    MemberProducer filterEmail(String email);

    /**
     * Adds a filter or role fragment. Only records with role containing the fragment are considered.
     * @param role fragment of role, case insensitive
     * @return itself, good for chaining.
     */
    MemberProducer filterRole(String role);

    /**
     * Adds a filter of GEO. Only records with a specified GEO are considered.
     * @param geo geo to filter
     * @return itself, good for chaining.
     */
    MemberProducer filterGeo(GeoEnum geo);

    /**
     * Adds a filter of extension. Only records with an extension containing the fragment are considered.
     * @param extension fragment of extension
     * @return itself, good for chaining.
     */
    MemberProducer filterExtension(String extension);

    /**
     * Adds a SBR filter. Only members in the specified SBR are considered.
     * @param sbr sbr to filter
     * @return itself, good for chaining.
     */
    MemberProducer filterSBRMembership(SBR sbr);

    /**
     * Adds a language filter. Only members with language knowledge containing the fragment are considered.
     * @param language language fragment to filter, case insensitive.
     * @return itself, good for chaining.
     */
    MemberProducer filterLanguage(String language);


    //crazier filters

    /**
     * Adds a filter of knowledge count on certain level. Only members with count of knowledges on specified level greater,
     * smaller, or equal to (according to {@code operatorEnum}) the specified count are considered.
     * @param level level of knowledge considered in this filter.
     * @param count count of knowledges the member must meet.
     * @param operatorEnum operator, specifies whether the member must have more, less or exactly equal number of knowledges
     *                     then specified
     * @return itself, good for chaining.
     */
    MemberProducer filterKnowledgeLevelCount(int level, int count, OperatorEnum operatorEnum);

    //TODO: explain KnowScore
    /**
     * Adds a filter of KnowScore of certain SBR. Only members with knowscore higher, smaller, or exactly equal to
     * {@code score} are considered.
     * @param score score the member must meet.
     * @param operatorEnum specifies whether member must have higher, lower or exactly equal KnowScore
     * @param sbr KnowScore of this SBR is calculated.
     * @return itself, good for chaining.
     */
    MemberProducer filterKnowScoreOfSBR(int score, OperatorEnum operatorEnum, SBR sbr);

    //sorters

    /**
     * Adds an ordering by nick.
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortNick(boolean ascending);

    /**
     * Adds an ordering by KnowScore of certain SBR.
     * @param sbr
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortKnowScoreOfSBR(SBR sbr, boolean ascending);

    /**
     * Adds an ordering by name.
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortName(boolean ascending);

    /**
     * Adds an ordering by email.
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortEmail(boolean ascending);

    /**
     * Adds an ordering by number of knowledge at certain level
     * @param level level of knowledge
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortKnowledgesAtLevel(int level, boolean ascending);

    /**
     * Adds an ordering by role.
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortRole(boolean ascending);

    /**
     * Adds an ordering by GEO.
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortGeo(boolean ascending);

    /**
     * Adds an ordering by extension.
     * @param ascending specifies whether the ordering should be ascending or descending
     * @return itself, good for chaining.
     */
    MemberProducer sortExtension(boolean ascending);

    //other utils

    /**
     * Adds a limit of maximum records returned. After this method is called, {@link #getMembers()} should never return
     * more than specified number of records.
     * @param count max number of records.
     * @return itself, good for chaining.
     */
    MemberProducer recordsCount(int count);

    /**
     * Adds a start offset in records returned. This can be used with {@link #recordsCount(int)} to create paging.
     * @param start
     * @return
     */
    MemberProducer recordsStart(int start);

    //base methods

    /**
     * Executes the query and returns the result.
     * @return list of members as result of the query.
     */
    List<Member> getMembers();

    /**
     * Count on members that would be returned by {@link #getMembers()}.
     * @return number of records.
     */
    long getCount();

    /**
     * Total count of members that meet the condition of the query (ignoring {@link #recordsCount(int)} and
     * {@link #recordsStart(int)}. Can be handy when implementing paging.
     * @return total number of records
     */
    long getTotalCount();

}
