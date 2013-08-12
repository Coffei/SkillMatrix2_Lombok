package com.redhat.gss.skillmatrix.data.dao.interfaces;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.Member;

/**
 * Member DAO interface. Contains all the important methods, DAO should be used through this interface only.
 * User: jtrantin
 * Date: 8/6/13
 * Time: 2:11 PM
 */
public interface MemberDAOInt {

    /**
     *
     * @return a Member Producer Factory, used for a retrieving members,
     */
    MemberProducer getProducerFactory();

    /**
     * Creates a new member.
     * @param member a member to create
     * @throws UnsupportedOperationException if this operation is not supported by this DAO implementation.
     * @throws MemberInvalidException when the member is invalid for some reason and cannot be created.
     */
    void create(Member member) throws UnsupportedOperationException, MemberInvalidException;

    /**
     * Updates an existing member.
     * @param member a member to be updated
     * @throws UnsupportedOperationException if this operation is not supported by this DAO implementation.
     * @throws MemberInvalidException when the member si invalid and cannot be updated by this DAO.
     */
    void update(Member member) throws UnsupportedOperationException, MemberInvalidException;

    /**
     * Deletes a member.
     * @param member A member to be deleted.
     * @throws UnsupportedOperationException if this operation is not supported by this DAO implementation.
     * @throws MemberInvalidException when the member is invalid for some reason and cannot be deleted.
     */
    void delete(Member member) throws UnsupportedOperationException, MemberInvalidException;

    /**
     * @return true if the content can be modified, false if not.
     * if true is returned, then methods {@link #create(Member)}, {@link #update(Member)} and {@link #delete(Member)}
     * can be called and should not throw {@code UnsupportedOperationException}..
     */
    boolean canModify();

}
