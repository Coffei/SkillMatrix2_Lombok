package com.redhat.gss.skillmatrix.test.dao;

import com.redhat.gss.skillmatrix.data.dao.PackageDBDAO;
import com.redhat.gss.skillmatrix.data.dao.SbrDBDAO;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.PackageProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.SbrProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.Resources;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/15/13
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class PackageDaoTest {

    @Inject
    private PackageDAO pkgDao;

    @Inject
    private EntityManager em;

    @Inject
    private UserTransaction transaction;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "daotest.war")
                .addPackage(Package.class.getPackage()) //all model classes
                .addClasses(PackageDAO.class, PackageDBDAO.class, PackageProducer.class, PackageProducerDB.class, OperatorEnum.class)
                .addClasses(MemberInvalidException.class, SbrInvalidException.class, PackageInvalidException.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                        //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testCreate() throws Exception {
        cleanup();

        Package rich = new Package();
        rich.setName("RichFaces");
        rich.setDeprecated(true);
        pkgDao.create(rich);

        Package seam = new Package();
        seam.setName("Seam");
        pkgDao.create(seam);

        Package pkg = em.find(Package.class, rich.getId());
        assertNotNull("created package not found", pkg);
        assertEquals("name not persisted", rich.getName(), pkg.getName());
        assertEquals("deprecated not persisted", rich.isDeprecated(), pkg.isDeprecated());

        pkg = em.find(Package.class, seam.getId());
        assertNotNull("created package not found", pkg);
        assertEquals("name not persisted", seam.getName(), pkg.getName());
        assertEquals("deprecated not persisted", seam.isDeprecated(), pkg.isDeprecated());
    }

    @Test
    public void testUpdate() throws  Exception {
        cleanup();

        Package rich = new Package();
        rich.setName("RichFaces");
        rich.setDeprecated(true);
        pkgDao.create(rich);

        rich.setName("RF");
        rich.setDeprecated(false);
        pkgDao.update(rich);

        Package pkg = em.find(Package.class, rich.getId());
        assertNotNull("created package not found", pkg);
        assertEquals("name not updated", rich.getName(), pkg.getName());
        assertEquals("deprecated not updated", rich.isDeprecated(), pkg.isDeprecated());
    }

    @Test
    public void testDelete() throws Exception {
        cleanup();

        Package rich = new Package();
        rich.setName("RichFaces");
        rich.setDeprecated(true);
        pkgDao.create(rich);

        pkgDao.delete(rich);

        Package pkg = em.find(Package.class, rich.getId());
        assertNull("deleted package found", pkg);
    }

    private void cleanup() throws Exception {
        transaction.begin();
        em.joinTransaction();


        em.createNativeQuery("delete from MEMBER_SBR").executeUpdate();
        em.createQuery("delete from Knowledge").executeUpdate();
       /* em.createQuery("delete from PackageKnowledge");
        em.createQuery("delete from LanguageKnowledge");*/
        em.createQuery("delete from Coach").executeUpdate();
        em.createQuery("delete from Member").executeUpdate();
        em.createQuery("delete from Geo").executeUpdate();
        em.createQuery("delete from Package").executeUpdate();
        em.createQuery("delete from SBR").executeUpdate();

        transaction.commit();
    }
}
