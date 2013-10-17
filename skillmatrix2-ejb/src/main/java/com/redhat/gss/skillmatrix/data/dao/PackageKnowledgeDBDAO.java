package com.redhat.gss.skillmatrix.data.dao;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageKnowledgeDAO;
import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/1/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PackageKnowledgeDBDAO implements PackageKnowledgeDAO {

    @Inject
    private EntityManager em;

    @Override
    public void update(Map<Package, Integer> knowlegdes, Member member) throws MemberInvalidException, UnsupportedOperationException {
        //verify member is valid
        if(member==null)
            throw new MemberInvalidException("null member", new NullPointerException("member"));
        if(member.getId()==null) // invalid member instance
            throw new MemberInvalidException("member has no id");

        //delete all old knows
        Query query = em.createQuery("delete from PackageKnowledge know where know.member = :member");
        query.setParameter("member", member);
        query.executeUpdate();

        //create new knows
        if(knowlegdes!=null) {
            for(Map.Entry<Package, Integer> entry : knowlegdes.entrySet()) {
                if(entry.getValue() >= 0) { //level is valid
                    PackageKnowledge know = new PackageKnowledge();
                    know.setPackage(entry.getKey());
                    know.setLevel(entry.getValue());
                    know.setMember(member);

                    em.persist(know);
                }
            }

        }

    }

    @Override
    public boolean canModify() {
        return true;
    }
}
