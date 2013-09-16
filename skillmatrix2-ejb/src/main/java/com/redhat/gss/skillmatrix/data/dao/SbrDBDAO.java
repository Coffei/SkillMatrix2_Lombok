package com.redhat.gss.skillmatrix.data.dao;

import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.SbrProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.model.SBR;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/14/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SbrDBDAO implements SbrDAO {

    @Inject
    private EntityManager em;

    @Inject
    private UserTransaction transaction;


    @Override
    public SbrProducer getSbrProducer() {
        return new SbrProducerDB(em, transaction);
    }

    @Override
    public void create(SBR sbr) throws SbrInvalidException {
        if(sbr==null)
            throw new NullPointerException("sbr");

        em.persist(sbr);
    }

    @Override
    public void update(SBR sbr) throws SbrInvalidException {
        if(sbr==null)
            throw new NullPointerException("sbr");
        if(sbr.getId() == null)
            throw new SbrInvalidException("sbr has no DB ID", new NullPointerException("sbr.id"), sbr);

        em.merge(sbr);
    }

    @Override
    public void delete(SBR sbr) throws SbrInvalidException {
        if(sbr==null)
            throw new NullPointerException("sbr");
        if(sbr.getId() == null)
            throw new SbrInvalidException("sbr has no DB ID", new NullPointerException("sbr.id"), sbr);

        if(!em.contains(sbr))
            sbr = em.merge(sbr);

        em.remove(sbr);
    }

    @Override
    public boolean canModify() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
