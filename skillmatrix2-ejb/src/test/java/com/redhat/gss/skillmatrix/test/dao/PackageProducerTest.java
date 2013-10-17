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
 * Date: 8/16/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class PackageProducerTest {

    @Inject
    private UserTransaction transaction;

    @Inject
    private EntityManager em;

    @Inject
    private PackageDAO pkgDao;

    private boolean isSetUp = false;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "prodtest.war")
                .addPackage(Package.class.getPackage()) //all model classes
                .addClasses(PackageDAO.class, PackageDBDAO.class, PackageProducer.class, PackageProducerDB.class, OperatorEnum.class)
                .addClasses(MemberInvalidException.class, SbrInvalidException.class, PackageInvalidException.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                        //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    //SECTION: filter tests

    @Test
    public void testFilterId() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().filterId(rf_id).getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("wrong package returned", "RichFaces", pkgs.get(0).getName());

        pkgs = pkgDao.getPackageProducer().filterId(jbpm_id).getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("wrong package returned", "jBPM", pkgs.get(0).getName());
    }

    @Test
    public void testFilterName() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().filterName("IN").getPackages();

        Pattern namePattern = Pattern.compile("^Infinispan$|^Logging$|^Spring$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 3, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match",namePattern.matcher(pkg.getName()).matches());

        pkgs = pkgDao.getPackageProducer().filterName("bP").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("package name doesn't match", "jBPM", pkgs.get(0).getName());

        pkgs = pkgDao.getPackageProducer().filterName(" ").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

        pkgs = pkgDao.getPackageProducer().filterName(" rich").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

        pkgs = pkgDao.getPackageProducer().filterName("x").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

    }

    @Test
    public void testFilterNameExact() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().filterNameExact("Seam").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("package name doesn't match", "Seam", pkgs.get(0).getName());

        pkgs = pkgDao.getPackageProducer().filterNameExact("richfaces").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

        pkgs = pkgDao.getPackageProducer().filterNameExact("Infinispan").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("package name doesn't match", "Infinispan", pkgs.get(0).getName());

        pkgs = pkgDao.getPackageProducer().filterNameExact("EJB ").getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());
    }

    @Test
    public void testFilterSbr() throws Exception {
        SBR wf = em.find(SBR.class, wf_id);
        SBR jbossas = em.find(SBR.class, jbossas_id);
        SBR clust = em.find(SBR.class, clustering_id);

        List<Package> pkgs = pkgDao.getPackageProducer().filterSBR(wf).getPackages();

        Pattern namePattern = Pattern.compile("^RichFaces$|^Seam$|^Spring$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 3, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match", namePattern.matcher(pkg.getName()).matches());

        pkgs = pkgDao.getPackageProducer().filterSBR(jbossas).getPackages();

        namePattern = Pattern.compile("^EJB$|^Logging$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 2, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match", namePattern.matcher(pkg.getName()).matches());

        pkgs = pkgDao.getPackageProducer().filterSBR(clust).getPackages();

        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("package name doesn't match", "Infinispan", pkgs.get(0).getName());

    }

    @Test
    public void testFilterPeopleAtKnowledgeLevel() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(2, OperatorEnum.EQUAL, 2).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("package name doesn't match", "Logging", pkgs.get(0).getName());

        pkgs = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(1, OperatorEnum.BIGGER, 0).getPackages();
        Pattern namePattern = Pattern.compile("^EJB$|^Seam$|^Spring$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 3, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match", namePattern.matcher(pkg.getName()).matches());


        pkgs = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(0, OperatorEnum.SMALLER, 1).getPackages();
        namePattern = Pattern.compile("^RichFaces$|^jBPM$|^Infinispan$|^Spring$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 4, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match", namePattern.matcher(pkg.getName()).matches());


        pkgs = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(2, OperatorEnum.BIGGER, 2).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

        pkgs = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(1, OperatorEnum.BIGGER, 1).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

        pkgs = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(0, OperatorEnum.EQUAL, 2).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());
    }

    @Test
    public void testFilterSbrName() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().filterSbrName("fra").getPackages();
        Pattern namePattern = Pattern.compile("^RichFaces$|^Seam$|^Spring$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 3, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match", namePattern.matcher(pkg.getName()).matches());

        pkgs = pkgDao.getPackageProducer().filterSbrName("jbOSs").getPackages();
        namePattern = Pattern.compile("^EJB$|^Logging$|^Infinispan$");
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 3, pkgs.size());
        for(Package pkg : pkgs)
            assertTrue("package name doesn't match", namePattern.matcher(pkg.getName()).matches());


        pkgs = pkgDao.getPackageProducer().filterSbrName("someunknown").getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 0, pkgs.size());

    }

    //SECTION: order tests

    @Test
    public void testSortName() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().sortName(true).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 7, pkgs.size());
        assertEquals("wrong package order","EJB" ,pkgs.get(0).getName());
        assertEquals("wrong package order","Logging" ,pkgs.get(3).getName());
        assertEquals("wrong package order","Seam" ,pkgs.get(5).getName());
        assertEquals("wrong package order","jBPM" ,pkgs.get(2).getName());

        pkgs = pkgDao.getPackageProducer().sortName(false).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 7, pkgs.size());
        assertEquals("wrong package order","Logging" ,pkgs.get(3).getName());
        assertEquals("wrong package order","Infinispan" ,pkgs.get(5).getName());
    }

    @Test
    public void testSbrName() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().sortSbrName(true).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 7, pkgs.size());


        assertEquals("wrong package order","jBPM" ,pkgs.get(0).getName());
        assertEquals("wrong package order","Infinispan" ,pkgs.get(1).getName());

        Pattern namePattern = Pattern.compile("^EJB$|^Logging$");
        assertTrue("wrong package order", namePattern.matcher(pkgs.get(2).getName()).matches());
        assertTrue("wrong package order", namePattern.matcher(pkgs.get(3).getName()).matches());

        namePattern = Pattern.compile("^RichFaces$|^Seam$|^Spring$");
        assertTrue("wrong package order", namePattern.matcher(pkgs.get(4).getName()).matches());
        assertTrue("wrong package order", namePattern.matcher(pkgs.get(5).getName()).matches());
        assertTrue("wrong package order", namePattern.matcher(pkgs.get(6).getName()).matches());
    }

    //SECTION: utils tests

    @Test
    public void testUtilRecordsStart() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().recordsStart(5).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 2, pkgs.size());

        pkgs = pkgDao.getPackageProducer().sortName(true).recordsStart(2).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 5, pkgs.size());
        assertEquals("wrong package", "Logging", pkgs.get(1).getName());
        assertEquals("wrong package", "Seam", pkgs.get(3).getName());

        pkgs = pkgDao.getPackageProducer().sortName(false).recordsStart(4).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 3, pkgs.size());
        assertEquals("wrong package", "EJB", pkgs.get(2).getName());
        assertEquals("wrong package", "jBPM", pkgs.get(0).getName());
    }

    @Test
    public void testUtilRecordsCount() throws Exception {
        List<Package> pkgs = pkgDao.getPackageProducer().recordsCount(2).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 2, pkgs.size());

        pkgs = pkgDao.getPackageProducer().sortName(true).recordsCount(2).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 2, pkgs.size());
        assertEquals("wrong package", "EJB", pkgs.get(0).getName());
        assertEquals("wrong package", "Infinispan", pkgs.get(1).getName());

        pkgs = pkgDao.getPackageProducer().sortName(false).recordsCount(1).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 1, pkgs.size());
        assertEquals("wrong package", "Spring", pkgs.get(0).getName());

        pkgs = pkgDao.getPackageProducer().sortName(false).recordsCount(-1).getPackages();
        assertNotNull("null result returned", pkgs);
        assertEquals("wrong number of packages returned", 7, pkgs.size());
    }

    @Test
    public void testUtilGetCount() throws Exception {
        SBR wf = em.find(SBR.class, wf_id);

        long count = pkgDao.getPackageProducer().getCount();
        assertEquals("wrong number of records", 7, count);


        count = pkgDao.getPackageProducer().filterPeopleAtKnowledgeLevel(0, OperatorEnum.SMALLER, 1).getCount();
        assertEquals("wrong number of records", 4, count);

        count = pkgDao.getPackageProducer().filterSBR(wf).getCount();
        assertEquals("wrong number of records", 3, count);

        count = pkgDao.getPackageProducer().filterNameExact("richfaces").getCount();
        assertEquals("wrong number of records", 0, count);

        count = pkgDao.getPackageProducer().sortName(false).recordsCount(1).getCount();
        assertEquals("wrong number of records", 7, count);

        count = pkgDao.getPackageProducer().sortName(true).recordsStart(2).getCount();
        assertEquals("wrong number of records", 7, count);


    }

    //SECTION: complete integration tests

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

    private long rf_id;
    private long seam_id;
    private long ejb_id;
    private long log_id;
    private long spring_id;
    private long jbpm_id;
    private long infini_id;

    private void prepareData() throws Exception {
        transaction.begin();
        em.joinTransaction();
        SBR wf = new SBR();
        wf.setName("Web Frameworks");
        em.persist(wf);
        wf_id = wf.getId();

        SBR jbossas = new SBR();
        jbossas.setName("JBoss AS");
        em.persist(jbossas);
        jbossas_id = jbossas.getId();

        SBR brms = new SBR();
        brms.setName("BRMS");
        em.persist(brms);
        brms_id = brms.getId();

        SBR clustering = new SBR();
        clustering.setName("JBoss Clustering");
        em.persist(clustering);
        clustering_id = clustering.getId();

        //packages
        Package richfaces = new Package();
        richfaces.setName("RichFaces");
        richfaces.setSbr(wf);
        em.persist(richfaces);
        rf_id = richfaces.getId();

        Package seam = new Package();
        seam.setName("Seam");
        seam.setSbr(wf);
        em.persist(seam);
        seam_id = seam.getId();

        Package ejb = new Package();
        ejb.setName("EJB");
        ejb.setSbr(jbossas);
        em.persist(ejb);
        ejb_id = ejb.getId();

        Package spring = new Package();
        spring.setName("Spring");
        spring.setSbr(wf);
        em.persist(spring);
        spring_id = spring.getId();

        Package logging = new Package();
        logging.setName("Logging");
        logging.setSbr(jbossas);
        em.persist(logging);
        log_id = logging.getId();

        Package jbpm = new Package();
        jbpm.setName("jBPM");
        jbpm.setSbr(brms);
        em.persist(jbpm);
        jbpm_id = jbpm.getId();

        Package infinispan = new Package();
        infinispan.setName("Infinispan");
        infinispan.setSbr(clustering);
        em.persist(infinispan);
        infini_id = infinispan.getId();

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
        akovari.setGeo(new Geo(GeoEnum.NASA, 120));
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
        me_log.setPackage(logging);
        me_log.setLevel(0);
        em.persist(me_log);

        // akovari
        PackageKnowledge ako_infini = new PackageKnowledge();
        ako_infini.setMember(akovari);
        ako_infini.setPackage(infinispan);
        ako_infini.setLevel(2);
        em.persist(ako_infini);

        PackageKnowledge ako_seam = new PackageKnowledge();
        ako_seam.setMember(akovari);
        ako_seam.setPackage(seam);
        ako_seam.setLevel(0);
        em.persist(ako_seam);

        PackageKnowledge ako_ejb = new PackageKnowledge();
        ako_ejb.setMember(akovari);
        ako_ejb.setPackage(ejb);
        ako_ejb.setLevel(2);
        em.persist(ako_ejb);

        PackageKnowledge ako_log = new PackageKnowledge();
        ako_log.setMember(akovari);
        ako_log.setPackage(logging);
        ako_log.setLevel(2);
        em.persist(ako_log);

        // agiertli
        PackageKnowledge agi_jbpm = new PackageKnowledge();
        agi_jbpm.setMember(agiertli);
        agi_jbpm.setPackage(jbpm);
        agi_jbpm.setLevel(2);
        em.persist(agi_jbpm);

        PackageKnowledge agi_spring = new PackageKnowledge();
        agi_spring.setMember(agiertli);
        agi_spring.setPackage(spring);
        agi_spring.setLevel(1);
        em.persist(agi_spring);

        PackageKnowledge agi_ejb = new PackageKnowledge();
        agi_ejb.setMember(agiertli);
        agi_ejb.setPackage(ejb);
        agi_ejb.setLevel(0);
        em.persist(agi_ejb);

        PackageKnowledge agi_log = new PackageKnowledge();
        agi_log.setMember(agiertli);
        agi_log.setPackage(logging);
        agi_log.setLevel(2);
        em.persist(agi_log);





        transaction.commit();
    }
}
