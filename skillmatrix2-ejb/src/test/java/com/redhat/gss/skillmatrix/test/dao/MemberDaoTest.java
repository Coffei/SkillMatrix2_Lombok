package com.redhat.gss.skillmatrix.test.dao;

import com.redhat.gss.skillmatrix.data.dao.MemberDBDAO;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;

import java.util.Arrays;

import lombok.val;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 8/7/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class MemberDaoTest {

    @Inject
    private MemberDAO memberDao;

    @Inject
    private EntityManager em;

    @Inject
    private UserTransaction transaction;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "daotest.war")
                .addPackage(Member.class.getPackage()) //all model classes
                .addClasses(MemberDAO.class, MemberDBDAO.class, MemberProducer.class, MemberProducerDB.class)
                .addClasses(MemberInvalidException.class, OperatorEnum.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testCreate() throws Exception {
        val newMember= new Member();
        newMember.setNick("jtrantin");
        newMember.setEmail("jtrantin@redhat.com");
        newMember.setName("Jonas");
        newMember.setExtension("62918");
        newMember.setGeo(new Geo(GeoEnum.EMEA, 120));
        newMember.setRole("AITSE");

        memberDao.create(newMember);

        long id = newMember.getId();
        Member member = em.find(Member.class, id);

        assertNotNull("no member retrieved, nothing was saved", member);

        assertEquals("nick not equal", newMember.getNick(), member.getNick());
        assertEquals("email not equal", newMember.getEmail(), member.getEmail());
        assertEquals("name not equal", newMember.getName(), member.getName());
        assertEquals("extension not equal", newMember.getExtension(), member.getExtension());
        assertEquals("geocode not equal", newMember.getGeo().getGeocode(), member.getGeo().getGeocode());
        assertEquals("offset not equal", newMember.getGeo().getOffset(), member.getGeo().getOffset());
        assertEquals("role not equal", newMember.getRole(), member.getRole());

    }

    @Test
    public void testUpdate() throws Exception {
        cleanup();
        val newMember= new Member();
        newMember.setNick("jtrantin");
        newMember.setEmail("jtrantin@redhat.com");
        newMember.setName("Jonas");
        newMember.setExtension("62918");
        newMember.setGeo(new Geo(GeoEnum.EMEA, 120));
        newMember.setRole("AITSE");

        memberDao.create(newMember);

        newMember.setNick("jtrantina");
        newMember.setEmail("jtrantian@redhat.com");
        newMember.setName("Jonas Trantina");
        newMember.setExtension("62917");
        newMember.setGeo(new Geo(GeoEnum.APAC, -60));
        newMember.setRole("SRM");
        memberDao.update(newMember);

        long id = newMember.getId();
        Member member = em.find(Member.class, id);
        assertNotNull("no member retrieved after update", member);

        assertEquals("nick not equal", newMember.getNick(), member.getNick());
        assertEquals("email not equal", newMember.getEmail(), member.getEmail());
        assertEquals("name not equal", newMember.getName(), member.getName());
        assertEquals("extension not equal", newMember.getExtension(), member.getExtension());
        assertEquals("geocode not equal", newMember.getGeo().getGeocode(), member.getGeo().getGeocode());
        assertEquals("offset not equal", newMember.getGeo().getOffset(), member.getGeo().getOffset());
        assertEquals("role not equal", newMember.getRole(), member.getRole());

    }

    @Test
    public void testDelete() throws Exception  {
        cleanup();

        //regular delete
        Member newMember= new Member();
        newMember.setNick("jtrantin");
        newMember.setEmail("jtrantin@redhat.com");
        newMember.setName("Jonas");
        newMember.setExtension("62918");
        newMember.setGeo(new Geo(GeoEnum.EMEA, 120));
        newMember.setRole("AITSE");

        memberDao.create(newMember);

        Member member = em.find(Member.class, newMember.getId());
        assertNotNull("member not saved before delete", member);

        memberDao.delete(member);

        member = em.find(Member.class, newMember.getId());
        assertNull("found a deleted record", member);

        //advanced delete
        transaction.begin();
        em.joinTransaction();
        SBR wf = new SBR();
        wf.setName("Web Frameworks");
        em.persist(wf);
        transaction.commit();


        newMember= new Member();
        newMember.setNick("jtrantin");
        newMember.setEmail("jtrantin@redhat.com");
        newMember.setName("Jonas");
        newMember.setExtension("62918");
        newMember.setGeo(new Geo(GeoEnum.EMEA, 120));
        newMember.setRole("AITSE");
        newMember.setSbrs(Arrays.asList(wf));
        memberDao.create(newMember);

        transaction.begin();
        em.joinTransaction();
        Package richfaces = new Package();
        richfaces.setName("Richfaces");
        richfaces.setSbr(wf);
        em.persist(richfaces);



        val know = new PackageKnowledge();
        know.setPackage(richfaces);
        know.setLevel(2);
        know.setMember(newMember);
        em.persist(know);

        transaction.commit();

        long know_id = know.getId();

        //try to delete
        member = em.find(Member.class, newMember.getId());

        memberDao.delete(member);

        transaction.begin();
        em.joinTransaction();
        assertNull("deleted member found", em.find(Member.class, member.getId()));
        assertNull("members knowledge found", em.find(Knowledge.class, know_id));
        assertTrue("SBR has some members", em.find(SBR.class, wf.getId()).getMembers().isEmpty());
        transaction.commit();


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
