package com.redhat.gss.skillmatrix.data.dao.interfaces;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/1/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PackageKnowledgeDAO {

    /**
     * Updates members package knowledges according to given knowledges map.
     * @param knowlegdes knowledge map, mapping packages to their level of knowledge, level -1 means no knowledge
     * @param member member who's knowledges should be updated
     * @throws MemberInvalidException when member is invalid
     * @throws UnsupportedOperationException when this operation is not supported
     */
    void update(Map<Package, Integer> knowlegdes, Member member) throws MemberInvalidException, UnsupportedOperationException;

    /**
     * Returns true if this particular DAO impl can edit backend data.
     * @return
     */
    boolean canModify();

}
