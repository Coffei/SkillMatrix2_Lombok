
package com.redhat.gss.skillmatrix.test.dao;

import com.redhat.gss.skillmatrix.data.dao.SbrDBDAO;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.SbrProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.model.SBR;
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

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/7/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class SbrDaoTest {

    @Inject
    private SbrDAO sbrDAO;

    @Inject
    private EntityManager em;

    @Inject
    private UserTransaction transaction;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "daotest.war")
                .addPackage(SBR.class.getPackage()) //all model classes
                .addClasses(SbrDAO.class, SbrDBDAO.class, SbrProducer.class, SbrProducerDB.class)
                .addClasses(MemberInvalidException.class, SbrInvalidException.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testCreate() throws Exception {
        cleanup();

        SBR wf = new SBR();
        wf.setName("Web Frameworks");
        sbrDAO.create(wf);

        SBR sbr = em.find(SBR.class, wf.getId());

        assertNotNull("SBR not found, probably not created", sbr);
        assertEquals("wrong SBR found, probably wrongly created", wf.getName(), sbr.getName());
    }

    @Test
    public void testUpdate() throws  Exception {
        cleanup();

        SBR wf = new SBR();
        wf.setName("Web Frameworks");
        sbrDAO.create(wf);

        wf.setName("WFK");
        sbrDAO.update(wf);

        SBR sbr = em.find(SBR.class, wf.getId());

        assertNotNull("updated sbrn not found", sbr);
        assertEquals("sbr not updated", wf.getName(), sbr.getName());
    }

    @Test
    public void testDelete() throws Exception {
        cleanup();

        SBR wf = new SBR();
        wf.setName("Web Frameworks");
        sbrDAO.create(wf);

        sbrDAO.delete(wf);

        assertNull("deleted SBR found", em.find(SBR.class, wf.getId()));
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
