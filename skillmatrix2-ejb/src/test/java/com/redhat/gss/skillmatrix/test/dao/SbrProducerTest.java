package com.redhat.gss.skillmatrix.test.dao;

import com.redhat.gss.skillmatrix.data.dao.SbrDBDAO;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.SbrProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.Resources;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/7/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class SbrProducerTest {

    private boolean isSetUp = false;

    @Inject
    private SbrDAO sbrDAO;

    @Inject
    private UserTransaction transaction;

    @Inject
    private EntityManager em;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "prodtest.war")
                .addPackage(SBR.class.getPackage()) //all model classes
                .addClasses(SbrDAO.class, SbrDBDAO.class, SbrProducer.class, SbrProducerDB.class)
                .addClasses(MemberInvalidException.class, SbrInvalidException.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                        //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    //SECTION: Filter tests
    @Test
    public void testFilterId() throws Exception {
        List<SBR> wfs = sbrDAO.getProducerFactory().filterId(wf_id).getSbrs();
        List<SBR> jbossases = sbrDAO.getProducerFactory().filterId(jbossas_id).getSbrs();
        List<SBR> clusterings = sbrDAO.getProducerFactory().filterId(clustering_id).getSbrs();

        assertNotNull("null result returned", wfs);
        assertNotNull("null result returned", jbossases);
        assertNotNull("null result returned", clusterings);

        assertEquals("different number of SBRs returned", 1, wfs.size());
        assertEquals("different number of SBRs returned", 1, jbossases.size());
        assertEquals("different number of SBRs returned", 1, clusterings.size());

        SBR wf = wfs.get(0);
        SBR jbossas = jbossases.get(0);
        SBR clustering = clusterings.get(0);

        assertNotNull("sbr not found", wf);
        assertNotNull("sbr not found", jbossas);
        assertNotNull("sbr not found", clustering);

        assertEquals("wrong sbr found", "Web Frameworks", wf.getName());
        assertEquals("wrong sbr found", "JBoss AS", jbossas.getName());
        assertEquals("wrong sbr found", "JBoss Clustering", clustering.getName());

        //create an ID which is not used
        long id = wf_id + jbossas_id + brms_id + clustering_id;
        if(id < Long.MAX_VALUE / 10)
            id *= 7;

        List<SBR> nones = sbrDAO.getProducerFactory().filterId(id).getSbrs();

        assertNotNull("null result returned", nones);
        assertEquals("some sbrs found on non-existent id: " + id, 0, nones.size());
    }

    @Test()
    public void testFilterName() throws Exception {
        List<SBR> sbrs = sbrDAO.getProducerFactory().filterName("A").getSbrs();

        Pattern namePattern = Pattern.compile("^Web Frameworks$|^JBoss AS$");
        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 2, sbrs.size());
        assertTrue("incorrect SBR", namePattern.matcher(sbrs.get(0).getName()).matches());
        assertTrue("incorrect SBR", namePattern.matcher(sbrs.get(1).getName()).matches());


        sbrs = sbrDAO.getProducerFactory().filterName("lust").getSbrs();

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 1, sbrs.size());
        assertEquals("incorrect SBR", "JBoss Clustering", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().filterName(" ").getSbrs();

        namePattern = Pattern.compile("^Web Frameworks$|^JBoss AS$|^JBoss Clustering$");
        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 3, sbrs.size());
        assertTrue("incorrect SBR", namePattern.matcher(sbrs.get(0).getName()).matches());
        assertTrue("incorrect SBR", namePattern.matcher(sbrs.get(1).getName()).matches());
        assertTrue("incorrect SBR", namePattern.matcher(sbrs.get(2).getName()).matches());


        sbrs = sbrDAO.getProducerFactory().filterName("unkno").getSbrs();

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 0, sbrs.size());
    }

    @Test
    public void testFilterNameExact() throws Exception {
        List<SBR> sbrs = sbrDAO.getProducerFactory().filterNameExact("Web Frameworks").getSbrs();

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 1, sbrs.size());
        assertEquals("incorrect SBR", "Web Frameworks", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().filterNameExact("JBoss").getSbrs();

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 0, sbrs.size());

        sbrs = sbrDAO.getProducerFactory().filterNameExact("JBoss Clustering").getSbrs();

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 1, sbrs.size());
        assertEquals("incorrect SBR", "JBoss Clustering", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().filterNameExact("BRMS").getSbrs();

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 1, sbrs.size());
        assertEquals("incorrect SBR", "BRMS", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().filterNameExact("Jboss AS").getSbrs(); //search is case sensitive

        assertNotNull("null result returned", sbrs);
        assertEquals("incorrect number of SBRs returned", 0, sbrs.size());
    }

    @Test
    public void testFilterMember() throws Exception {
        Member jtrantin = em.find(Member.class, jtrantin_id);
        Member akovari = em.find(Member.class, akovari_id);
        Member agiertli = em.find(Member.class, agiertli_id);

        List<SBR> sbrs = sbrDAO.getProducerFactory().filterMember(jtrantin).getSbrs();

        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs", 1, sbrs.size());
        assertEquals("wrong sbr", "Web Frameworks", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().filterMember(akovari).getSbrs();

        Pattern namePattern = Pattern.compile("^JBoss AS$|^JBoss Clustering$|^BRMS$");
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs", 3, sbrs.size());
        assertTrue("wrong SBR", namePattern.matcher(sbrs.get(0).getName()).matches());
        assertTrue("wrong SBR", namePattern.matcher(sbrs.get(1).getName()).matches());
        assertTrue("wrong SBR", namePattern.matcher(sbrs.get(2).getName()).matches());

        sbrs = sbrDAO.getProducerFactory().filterMember(agiertli).getSbrs();

        namePattern = Pattern.compile("^JBoss AS$|^BRMS$");
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs", 2, sbrs.size());
        assertTrue("wrong SBR", namePattern.matcher(sbrs.get(0).getName()).matches());
        assertTrue("wrong SBR", namePattern.matcher(sbrs.get(1).getName()).matches());
    }

    //SECTION: Order tests

    @Test
    public void testSortName() throws Exception {
        List<SBR> sbrs = sbrDAO.getProducerFactory().sortName(true).getSbrs();

        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 4, sbrs.size());
        assertEquals("wrong sbr -> wrong order", "BRMS", sbrs.get(0).getName());
        assertEquals("wrong sbr -> wrong order", "JBoss AS", sbrs.get(1).getName());
        assertEquals("wrong sbr -> wrong order", "JBoss Clustering", sbrs.get(2).getName());
        assertEquals("wrong sbr -> wrong order", "Web Frameworks", sbrs.get(3).getName());
    }

    @Test
    public void testSortMembersCount() throws Exception  {
        List<SBR> sbrs = sbrDAO.getProducerFactory().sortMembersCount(false).getSbrs();
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 4, sbrs.size());

        Pattern firstPattern = Pattern.compile("^JBoss AS$|^BRMS$");
        Pattern secondPattern = Pattern.compile("^Web Frameworks$|^JBoss Clustering$");
        assertTrue("wrong SBR -> wrong order", firstPattern.matcher(sbrs.get(0).getName()).matches());
        assertTrue("wrong SBR -> wrong order", firstPattern.matcher(sbrs.get(1).getName()).matches());
        assertTrue("wrong SBR -> wrong order", secondPattern.matcher(sbrs.get(2).getName()).matches());
        assertTrue("wrong SBR -> wrong order", secondPattern.matcher(sbrs.get(3).getName()).matches());
    }


    //SECTION: Util tests

    @Test
    public void testRecordsStart() throws Exception {
        List<SBR> sbrs = sbrDAO.getProducerFactory().sortName(true).recordsStart(2).getSbrs();

        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 2, sbrs.size());
        assertEquals("wrong sbr", "JBoss Clustering", sbrs.get(0).getName());
        assertEquals("wrong sbr", "Web Frameworks", sbrs.get(1).getName());


        sbrs = sbrDAO.getProducerFactory().sortName(false).recordsStart(0).getSbrs();

        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 4, sbrs.size());
        assertEquals("wrong sbr", "BRMS", sbrs.get(3).getName());
        assertEquals("wrong sbr", "JBoss AS", sbrs.get(2).getName());
        assertEquals("wrong sbr ", "JBoss Clustering", sbrs.get(1).getName());
        assertEquals("wrong sbr", "Web Frameworks", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().sortName(false).recordsStart(3).getSbrs();
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 1, sbrs.size());
        assertEquals("wrong sbr ", "BRMS", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().sortName(true).recordsStart(5).getSbrs();
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 0, sbrs.size());

        sbrs = sbrDAO.getProducerFactory().sortName(true).recordsStart(-1).getSbrs(); // should be ignored
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 4, sbrs.size());
    }

    @Test
    public void testRecordsCount() throws Exception {
        List<SBR> sbrs = sbrDAO.getProducerFactory().sortName(true).recordsCount(2).getSbrs();
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 2, sbrs.size());
        assertEquals("wrong sbr", "BRMS", sbrs.get(0).getName());
        assertEquals("wrong sbr", "JBoss AS", sbrs.get(1).getName());

        sbrs = sbrDAO.getProducerFactory().sortName(true).recordsCount(1).recordsStart(2).getSbrs();
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 1, sbrs.size());
        assertEquals("wrong sbr", "JBoss Clustering", sbrs.get(0).getName());

        sbrs = sbrDAO.getProducerFactory().sortName(true).recordsCount(0).getSbrs(); // should be ignored
        assertNotNull("returned null result", sbrs);
        assertEquals("wrong number of sbrs returned", 4, sbrs.size());
    }

    @Test
    public void testGetCount() throws Exception {
        Member agiertli = em.find(Member.class, agiertli_id);

        long count = sbrDAO.getProducerFactory().filterNameExact("BRMS").getCount();

        assertEquals("wrong count returned", 1, count);

        count = sbrDAO.getProducerFactory().sortName(true).getCount();
        assertEquals("wrong count returned", 4, count);

        count = sbrDAO.getProducerFactory().filterName(" ").getCount();
        assertEquals("wrong count returned", 3, count);

        count =sbrDAO.getProducerFactory().filterMember(agiertli).getCount();
        assertEquals("wrong count returned", 2, count);

        count = sbrDAO.getProducerFactory().filterNameExact("JBoss").getCount();
        assertEquals("wrong count returned", 0, count);
    }


    //SECTION: Complete integration tests
    //TODO: finish

    // Setup stuff
    @Before
    public void setUp() throws Exception {
        if(!isSetUp) {
            clearData();
            prepareData();
            isSetUp = true;
        }
    }

    private void clearData() throws Exception{
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


    private long wf_id;
    private long jbossas_id;
    private long brms_id;
    private long clustering_id;

    private long jtrantin_id;
    private long akovari_id;
    private long agiertli_id;

    private void prepareData() throws Exception {
        transaction.begin();
        em.joinTransaction();
        SBR wf = new SBR();
        wf.setName("Web Frameworks");
        sbrDAO.create(wf);
        wf_id = wf.getId();

        SBR jbossas = new SBR();
        jbossas.setName("JBoss AS");
        sbrDAO.create(jbossas);
        jbossas_id = jbossas.getId();

        SBR brms = new SBR();
        brms.setName("BRMS");
        sbrDAO.create(brms);
        brms_id = brms.getId();

        SBR clustering = new SBR();
        clustering.setName("JBoss Clustering");
        sbrDAO.create(clustering);
        clustering_id = clustering.getId();

        //packages
        Package richfaces = new Package();
        richfaces.setName("RichFaces");
        richfaces.setSbr(wf);
        em.persist(richfaces);

        Package seam = new Package();
        seam.setName("Seam");
        seam.setSbr(wf);
        em.persist(seam);

        Package ejb = new Package();
        ejb.setName("EJB");
        ejb.setSbr(jbossas);
        em.persist(ejb);

        Package spring = new Package();
        spring.setName("Spring");
        spring.setSbr(wf);
        em.persist(spring);

        Package logging = new Package();
        logging.setName("Logging");
        logging.setSbr(jbossas);
        em.persist(logging);

        Package jbpm = new Package();
        jbpm.setName("jBPM");
        jbpm.setSbr(brms);
        em.persist(jbpm);

        Package infinispan = new Package();
        infinispan.setName("Infinispan");
        infinispan.setSbr(clustering);
        em.persist(infinispan);




        //members
        Member me= new Member();
        me.setNick("jtrantin");
        me.setEmail("jtrantin@redhat.com");
        me.setName("Jonas Trantina");
        me.setExtension("62918");
        me.setGeo(new Geo(GeoEnum.EMEA, 120));
        me.setRole("ITSE");
        me.setSbrs(Arrays.asList(wf));
        em.persist(me);
        jtrantin_id = me.getId();



        Member akovari= new Member();
        akovari.setNick("akovari");
        akovari.setEmail("akovari@redhat.com");
        akovari.setName("Adam Kovari");
        akovari.setExtension("62915");
        akovari.setGeo(new Geo(GeoEnum.NA, 120));
        akovari.setRole("STSE");
        akovari.setSbrs(Arrays.asList(jbossas, clustering, brms));
        em.persist(akovari);
        akovari_id = akovari.getId();

        Member agiertli= new Member();
        agiertli.setNick("agiertli");
        agiertli.setEmail("agiertli@redhat.com");
        agiertli.setName("Anton Giertli");
        agiertli.setExtension("62917");
        agiertli.setGeo(new Geo(GeoEnum.APAC, 120));
        agiertli.setRole("ITSE");
        agiertli.setSbrs(Arrays.asList(jbossas, brms));
        em.persist(agiertli);
        agiertli_id = agiertli.getId();

       /* not needed right now
        //package knowledge
        // me
        PackageKnowledge me_rich = new PackageKnowledge();
        me_rich.setMember(me);
        me_rich.setPackage(richfaces);
        me_rich.setLevel(2);
        em.persist(me_rich);

        PackageKnowledge me_seam = new PackageKnowledge();
        me_seam.setMember(me);
        me_seam.setPackage(seam);
        me_seam.setLevel(1);
        em.persist(me_seam);

        PackageKnowledge me_ejb = new PackageKnowledge();
        me_ejb.setMember(me);
        me_ejb.setPackage(ejb);
        me_ejb.setLevel(1);
        em.persist(me_ejb);

        PackageKnowledge me_log = new PackageKnowledge();
        me_log.setMember(me);
        me_log.setPackage(jbpm);
        me_log.setLevel(0);
        em.persist(me_log);

        // akovari
        PackageKnowledge ako_rich = new PackageKnowledge();
        ako_rich.setMember(akovari);
        ako_rich.setPackage(richfaces);
        ako_rich.setLevel(0);
        em.persist(ako_rich);

        PackageKnowledge ako_seam = new PackageKnowledge();
        ako_seam.setMember(akovari);
        ako_seam.setPackage(seam);
        ako_seam.setLevel(1);
        em.persist(ako_seam);

        PackageKnowledge ako_ejb = new PackageKnowledge();
        ako_ejb.setMember(akovari);
        ako_ejb.setPackage(ejb);
        ako_ejb.setLevel(2);
        em.persist(ako_ejb);

        PackageKnowledge ako_log = new PackageKnowledge();
        ako_log.setMember(akovari);
        ako_log.setPackage(jbpm);
        ako_log.setLevel(2);
        em.persist(ako_log);

        // agiertli
        PackageKnowledge agi_rich = new PackageKnowledge();
        agi_rich.setMember(agiertli);
        agi_rich.setPackage(richfaces);
        agi_rich.setLevel(0);
        em.persist(agi_rich);

        PackageKnowledge agi_seam = new PackageKnowledge();
        agi_seam.setMember(agiertli);
        agi_seam.setPackage(seam);
        agi_seam.setLevel(2);
        em.persist(agi_seam);

        PackageKnowledge agi_ejb = new PackageKnowledge();
        agi_ejb.setMember(agiertli);
        agi_ejb.setPackage(ejb);
        agi_ejb.setLevel(0);
        em.persist(agi_ejb);

        PackageKnowledge agi_log = new PackageKnowledge();
        agi_log.setMember(agiertli);
        agi_log.setPackage(jbpm);
        agi_log.setLevel(2);
        em.persist(agi_log);

        */



        transaction.commit();
    }
}
