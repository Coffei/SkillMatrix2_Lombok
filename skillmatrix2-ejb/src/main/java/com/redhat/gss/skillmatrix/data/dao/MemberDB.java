package com.redhat.gss.skillmatrix.data.dao;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAOInt;
import com.redhat.gss.skillmatrix.data.dao.producers.MemberProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.Member;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Database implementation of member dao.
 * User: jtrantin
 * Date: 8/6/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class MemberDB implements MemberDAOInt {

    @Inject
    private EntityManager em;

    @Override
    public MemberProducer getProducerFactory() {
        return new MemberProducerDB(em);
    }

    @Override
    public void create(Member member) throws UnsupportedOperationException, MemberInvalidException {
        if(member==null)
            throw new NullPointerException("member");

        em.persist(member);
    }

    @Override
    public void update(Member member) throws UnsupportedOperationException, MemberInvalidException {
        if(member==null)
            throw new NullPointerException("member");
        if(member.getId()==null)  // member has no DB ID
            throw new MemberInvalidException("member has to have an ID in order to be updated", member);

        member = em.merge(member);
    }

    @Override
    public void delete(Member member) throws UnsupportedOperationException, MemberInvalidException {
        if(member==null)
            throw new NullPointerException("member");
        if(member.getId()==null)
            throw new MemberInvalidException("member has to have an ID in order to be deleted", member);

        if(!em.contains(member))
            member = em.merge(member);

        em.remove(member);
    }

    @Override
    public boolean canModify() {
        return true;
    }
}
