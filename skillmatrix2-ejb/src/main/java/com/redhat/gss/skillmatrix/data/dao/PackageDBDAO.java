package com.redhat.gss.skillmatrix.data.dao;

import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.PackageProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import lombok.NonNull;
import lombok.val;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/15/13
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PackageDBDAO implements PackageDAO {

    @Inject
    private EntityManager em;

    @Override
    public PackageProducer getProducerFactory() {
        return new PackageProducerDB(em);
    }

    @Override
    public void create(@NonNull Package pkg) throws PackageInvalidException {
         em.persist(pkg);
    }

    @Override
    public void update(@NonNull Package pkg) throws PackageInvalidException {
        if(pkg.getId()==null)
            throw new PackageInvalidException("package has no DB ID", new NullPointerException("pkg.id"), pkg);

        em.merge(pkg);
    }

    @Override
    public void delete(@NonNull Package pkg) throws PackageInvalidException {
        if(pkg.getId()==null)
            throw new PackageInvalidException("package has no DB ID", new NullPointerException("pkg.id"), pkg);

        if(!em.contains(pkg))
            pkg = em.merge(pkg);

        //delete all package knowledges
        val query = em.createNativeQuery("DELETE FROM PACKAGEKNOWLEDGE WHERE pkg_id=:pkg");
        query.setParameter("pkg", pkg.getId());
        query.executeUpdate();
        em.remove(pkg);
    }

    @Override
    public boolean canModify() {
        return true;
    }
}
