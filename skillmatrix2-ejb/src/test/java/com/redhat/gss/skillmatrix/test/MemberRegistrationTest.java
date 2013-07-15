package com.redhat.gss.skillmatrix.test;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.Resources;

@RunWith(Arquillian.class)
public class MemberRegistrationTest {
   @Deployment
   public static Archive<?> createTestArchive() {
      return ShrinkWrap.create(WebArchive.class, "test.war")
            .addClasses(Member.class, Resources.class, Knowledge.class, Package.class, PackageKnowledge.class, SBR.class)
            .addAsResource(/*"META-INF/test-persistence.xml",*/ "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
            // Deploy our test datasource
            //.addAsWebInfResource("test-ds.xml", "test-ds.xml");
   }

  

   @Inject
   Logger log;

   @Test
   public void testRegister() throws Exception {
      Member newMember = new Member();
      newMember.setName("Jane Doe");
      newMember.setEmail("jane@mailinator.com");
     
      assertNotNull(newMember.getId());
      log.info(newMember.getName() + " was persisted with id " + newMember.getId());
   }
   
}
