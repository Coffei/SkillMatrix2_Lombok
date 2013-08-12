package com.redhat.gss.skillmatrix.test.dao;

import com.redhat.gss.skillmatrix.data.dao.MemberDB;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAOInt;
import com.redhat.gss.skillmatrix.data.dao.producers.MemberProducerDB;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.Geo;
import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.model.Member;
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
import javax.persistence.Query;
import javax.transaction.*;

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
    private MemberDAOInt memberDao;

    @Inject
    private EntityManager em;

    @Inject
    private UserTransaction transaction;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "daotest.war")
                .addPackage(Member.class.getPackage()) //all model classes
                .addClasses(MemberDAOInt.class, MemberDB.class, MemberProducer.class, MemberProducerDB.class)
                .addClasses(MemberInvalidException.class, OperatorEnum.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                //.addAsWebInfResource("test-ds.xml", "test-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testCreate() throws Exception {
        Member newMember= new Member();
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
        Member newMember= new Member();
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
    }

    private void cleanup() throws Exception {
        transaction.begin();

        em.joinTransaction();
        Query query = em.createQuery("Delete from Member");
        query.executeUpdate();

        transaction.commit();
    }
}
