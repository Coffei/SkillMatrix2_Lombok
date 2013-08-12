package com.redhat.gss.skillmatrix.test.dao;

import com.redhat.gss.skillmatrix.data.dao.MemberDB;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAOInt;
import com.redhat.gss.skillmatrix.data.dao.producers.MemberProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
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
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/7/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class MemberProducerTest {

    private boolean isSetUp = false;

    @Inject
    private MemberDAOInt memberDao;

    @Inject
    private UserTransaction transaction;

    @Inject
    private EntityManager em;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "prodtest.war")
                .addPackage(Member.class.getPackage()) //all model classes
                .addClasses(MemberDAOInt.class, MemberDB.class, MemberProducer.class, MemberProducerDB.class)
                .addClasses(MemberInvalidException.class, OperatorEnum.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    //SECTION: Filter tests

    @Test
    public void testFilterId() throws Exception {
        Member newMember= new Member();
        newMember.setNick("jtrantin");
        newMember.setEmail("jtrantin@redhat.com");
        newMember.setName("Jonas");
        newMember.setExtension("62918");
        newMember.setGeo(new Geo(GeoEnum.EMEA, 120));
        newMember.setRole("AITSE");

        transaction.begin();
        em.joinTransaction();
        memberDao.create(newMember);
        transaction.commit();

        MemberProducer producer = memberDao.getProducerFactory();

        List<Member> result = producer.filterId(newMember.getId()).getMembers();
        assertNotNull("null result from producer", result);
        assertEquals("no or more members retrieved from producer", 1, result.size());

        Member member = result.get(0);
        assertEquals("wrong nick", newMember.getNick(), member.getNick());
        assertEquals("wrong email", newMember.getEmail(), member.getEmail());
        assertEquals("wrong name", newMember.getName(), member.getName());
        assertEquals("wrong extension", newMember.getExtension(), member.getExtension());
        assertEquals("wrong geocode", newMember.getGeo().getGeocode(), member.getGeo().getGeocode());
        assertEquals("wrong offset", newMember.getGeo().getOffset(), member.getGeo().getOffset());
        assertEquals("wrong role", newMember.getRole(), member.getRole());

        transaction.begin();
        em.joinTransaction();
        newMember = em.merge(newMember);
        em.remove(newMember);
        transaction.commit();

        result = memberDao.getProducerFactory().filterId(newMember.getId()).getMembers();

        assertNotNull("null result when no members retrieved", result);
        assertEquals("some members returned, should be empty", 0, result.size());
    }

    @Test
    public void testFilterEmail() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterEmail("@redhat.com").getMembers();

        assertNotNull("results null", results);
        assertEquals("3 members expected", 3, results.size());

        results = memberDao.getProducerFactory().filterEmail("jtrantin").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterEmail("ri@re").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "akovari", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterEmail("lele").getMembers();

        assertNotNull("results null", results);
        assertEquals("0 member expected", 0, results.size());
    }

    @Test
    public void testFilterExtension() throws  Exception {
        List<Member> results = memberDao.getProducerFactory().filterExtension("629").getMembers();

        assertNotNull("results null", results);
        assertEquals("3 members expected", 3, results.size());

        results = memberDao.getProducerFactory().filterExtension("8").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterExtension("7").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterExtension("3").getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());
    }

    @Test
    public void testFilterNick() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterNick("a").getMembers();

        assertNotNull("results null", results);
        assertEquals("3 members expected", 3, results.size());

        results = memberDao.getProducerFactory().filterNick("ant").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterNick("g").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterNick("akor").getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());
    }

    @Test
    public void testFilterNickExact() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterNickExact("jtrantin").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterNickExact("akovari").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "akovari", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterNickExact("agertli").getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());
    }

    @Test
    public void testFilterGeo() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterGeo(GeoEnum.EMEA).getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterGeo(GeoEnum.APAC).getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterGeo(GeoEnum.NASA).getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "akovari", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterGeo(GeoEnum.Pune).getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());
    }

    @Test
    public void testFilterLanguage() throws  Exception {
        List<Member> results = memberDao.getProducerFactory().filterLanguage("en").getMembers();

        assertNotNull("results null", results);
        assertEquals("3 member expected", 3, results.size());

        results = memberDao.getProducerFactory().filterLanguage("sp").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterLanguage("en").filterLanguage("ge").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "akovari", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterLanguage("cs").getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterLanguage("sp").filterLanguage("ge").getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());
    }

    @Test
    public void testFilterName() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterName("a").getMembers();

        assertNotNull("results null", results);
        assertEquals("3 member expected", 3, results.size());

        results = memberDao.getProducerFactory().filterName("ant").getMembers();

        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterName("anton").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterName("trant").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterName("qor").getMembers();

        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());
    }

    @Test
    public void testFilterSBR() throws Exception {
        SBR wf = em.find(SBR.class, wf_id);
        SBR jbossas = em.find(SBR.class, jbossas_id);

        List<Member> results = memberDao.getProducerFactory().filterSBRMembership(jbossas).getMembers();

        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterSBRMembership(wf).getMembers();

        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterSBRMembership(jbossas).filterSBRMembership(wf).getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "agiertli", results.get(0).getNick());
    }

    @Test
    public void testFilterRole() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterRole("tSe").getMembers();

        assertNotNull("results null", results);
        assertEquals("3 member expected", 3, results.size());

        results = memberDao.getProducerFactory().filterRole("it").getMembers();

        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterRole("stS").getMembers();

        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong nick", "akovari", results.get(0).getNick());
    }

    @Test
    public void filterByKnowledgeLevelCount() throws Exception {
        List<Member> results = memberDao.getProducerFactory().filterKnowledgeLevelCount(2, 1, OperatorEnum.BIGGER).getMembers();
        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(2, 1, OperatorEnum.EQUAL).getMembers();
        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "jtrantin", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(1, 2, OperatorEnum.SMALLER).getMembers();
        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(1, 1, OperatorEnum.EQUAL).getMembers();
        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "akovari", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(0, 0, OperatorEnum.BIGGER).getMembers();
        assertNotNull("results null", results);
        assertEquals("3 member expected", 3, results.size());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(0, 2, OperatorEnum.EQUAL).getMembers();
        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(2, 0, OperatorEnum.EQUAL).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(0, 1, OperatorEnum.SMALLER).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(2, 3, OperatorEnum.BIGGER).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterKnowledgeLevelCount(2, 0, OperatorEnum.EQUAL).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

    }

    @Test
    public void filterKnowScoreOfSBR() throws Exception {
        SBR wf = em.find(SBR.class, wf_id);
        SBR jbossas = em.find(SBR.class, jbossas_id);

        List<Member> results = memberDao.getProducerFactory().filterKnowScoreOfSBR(4, OperatorEnum.BIGGER, wf).getMembers();
        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(4, OperatorEnum.SMALLER, wf).getMembers();
        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "akovari", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(5, OperatorEnum.EQUAL, jbossas).getMembers();
        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "agiertli", results.get(0).getNick());

        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(6, OperatorEnum.SMALLER, jbossas).getMembers();
        assertNotNull("results null", results);
        assertEquals("2 member expected", 2, results.size());

        //two filters applied
        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(5, OperatorEnum.EQUAL, wf)
                                                .filterKnowScoreOfSBR(4, OperatorEnum.BIGGER, jbossas).getMembers();
        assertNotNull("results null", results);
        assertEquals("1 member expected", 1, results.size());
        assertEquals("wrong member returned", "agiertli", results.get(0).getNick());

        //invalids
        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(4, OperatorEnum.EQUAL, wf).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(3, OperatorEnum.SMALLER, wf).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(4, OperatorEnum.EQUAL, jbossas).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());

        results = memberDao.getProducerFactory().filterKnowScoreOfSBR(5, OperatorEnum.BIGGER, jbossas)
                                                .filterKnowScoreOfSBR(4, OperatorEnum.BIGGER, wf).getMembers();
        assertNotNull("results null", results);
        assertEquals("no member expected", 0, results.size());



    }

    //SECTION: Order tests

    @Test
    public void testSortEmail() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortEmail(true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortEmail(false).getMembers();
        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(2).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(0).getNick());
    }

    @Test
    public void testSortExtension() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortExtension(true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "akovari", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortExtension(false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "akovari", results.get(2).getNick());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(0).getNick());
    }

    @Test
    public void testSortGeo() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortGeo(true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortGeo(false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(2).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(0).getNick());
    }

    @Test
    public void testSortName() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortName(true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "akovari", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortName(false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "akovari", results.get(2).getNick());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(0).getNick());
    }

    @Test
    public void testSortNick() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortNick(true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortNick(false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(2).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(0).getNick());
    }

    @Test
    public void testSortRole() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortRole(true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        //first two are not defined
       /* assertEquals("expected different member, wrong order!", "agiertli", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(1).getNick());*/
        assertEquals("expected different member, wrong order!", "akovari", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortRole(false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());

        assertEquals("expected different member, wrong order!", "akovari", results.get(0).getNick());
    }

    @Test
    public void testSortKnowledgesAtLevel() throws Exception {
        List<Member> results = memberDao.getProducerFactory().sortKnowledgesAtLevel(1,true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "akovari", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortKnowledgesAtLevel(2,false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortKnowledgesAtLevel(0,true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(2).getNick());
    }

    @Test
    public void testSortKnowScoreOfSbr() throws Exception {
        SBR wf = em.find(SBR.class, wf_id);
        SBR jbossas = em.find(SBR.class, jbossas_id);

        List<Member> results = memberDao.getProducerFactory().sortKnowScoreOfSBR(wf, true).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "akovari", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());

        results = memberDao.getProducerFactory().sortKnowScoreOfSBR(jbossas, false).getMembers();

        assertNotNull("null results returned", results);
        assertEquals("wrong number of records", 3, results.size());
        assertEquals("expected different member, wrong order!", "akovari", results.get(0).getNick());
        assertEquals("expected different member, wrong order!", "agiertli", results.get(1).getNick());
        assertEquals("expected different member, wrong order!", "jtrantin", results.get(2).getNick());
    }


    //SECTION: Utils tests
    //SECTION: Complete integration tests


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

        Package logging = new Package();
        logging.setName("Logging");
        logging.setSbr(jbossas);
        em.persist(logging);

        //members
        Member me= new Member();
        me.setNick("jtrantin");
        me.setEmail("jtrantin@redhat.com");
        me.setName("Jonas Trantina");
        me.setExtension("62918");
        me.setGeo(new Geo(GeoEnum.EMEA, 120));
        me.setRole("ITSE");
        me.setSbrs(Arrays.asList(wf));
        memberDao.create(me);




        Member akovari= new Member();
        akovari.setNick("akovari");
        akovari.setEmail("akovari@redhat.com");
        akovari.setName("Adam Kovari");
        akovari.setExtension("62915");
        akovari.setGeo(new Geo(GeoEnum.NASA, 120));
        akovari.setRole("STSE");
        akovari.setSbrs(Arrays.asList(jbossas));

        memberDao.create(akovari);

        Member agiertli= new Member();
        agiertli.setNick("agiertli");
        agiertli.setEmail("agiertli@redhat.com");
        agiertli.setName("Anton Giertli");
        agiertli.setExtension("62917");
        agiertli.setGeo(new Geo(GeoEnum.APAC, 120));
        agiertli.setRole("ITSE");
        agiertli.setSbrs(Arrays.asList(jbossas, wf));

        memberDao.create(agiertli);


        //create languages
        LanguageKnowledge me_en = new LanguageKnowledge();
        me_en.setLanguage("en");
        me_en.setMember(me);

        em.persist(me_en);

        LanguageKnowledge ako_en = new LanguageKnowledge();
        ako_en.setLanguage("en");
        ako_en.setMember(akovari);

        em.persist(ako_en);

        LanguageKnowledge ako_ge = new LanguageKnowledge();
        ako_ge.setLanguage("ge");
        ako_ge.setMember(akovari);

        em.persist(ako_ge);

        LanguageKnowledge agi_en = new LanguageKnowledge();
        agi_en.setLanguage("en");
        agi_en.setMember(agiertli);

        em.persist(agi_en);

        LanguageKnowledge agi_sp = new LanguageKnowledge();
        agi_sp.setLanguage("sp");
        agi_sp.setMember(agiertli);

        em.persist(agi_sp);

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
        ako_log.setPackage(logging);
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
        agi_log.setPackage(logging);
        agi_log.setLevel(2);
        em.persist(agi_log);





        transaction.commit();
    }
}
