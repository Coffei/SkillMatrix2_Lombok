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
    public PackageProducer getPackageProducer() {
        return new PackageProducerDB(em);
    }

    @Override
    public void create(Package pkg) throws PackageInvalidException {
        if(pkg==null)
            throw new NullPointerException("pkg");

        em.persist(pkg);
    }

    @Override
    public void update(Package pkg) throws PackageInvalidException {
        if(pkg==null)
            throw new NullPointerException("pkg");
        if(pkg.getId()==null)
            throw new PackageInvalidException("package has no DB ID", new NullPointerException("pkg.id"), pkg);

        em.merge(pkg);
    }

    @Override
    public void delete(Package pkg) throws PackageInvalidException {
        if(pkg==null)
            throw new NullPointerException("pkg");
        if(pkg.getId()==null)
            throw new PackageInvalidException("package has no DB ID", new NullPointerException("pkg.id"), pkg);

        if(!em.contains(pkg))
            pkg = em.merge(pkg);

        em.remove(pkg);
    }

    @Override
    public boolean canModify() {
        return true;
    }
}
