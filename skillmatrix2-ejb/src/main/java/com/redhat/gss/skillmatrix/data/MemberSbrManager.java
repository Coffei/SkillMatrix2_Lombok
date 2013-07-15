package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.SBR;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * CRUD Manager for MemberSbr.
 * @author jtrantin
 *
 */
@Stateless
public class MemberSbrManager {

	@Inject
	private EntityManager em;
	
	/**
	 * Makes unmanaged entity managed and persistent.
	 * @param memberSbr unmanaged, not null
	 */
	public void create(MemberSbr memberSbr) {
		if(memberSbr==null)
			throw new NullPointerException("memberSbr");
		
		em.persist(memberSbr);
	}
	
	/**
	 * Updates managed entity or creates unmanaged entity.
	 * @param membersbr unmanaged or managed, not null
	 */
	public void delete(MemberSbr membersbr) {
		if(membersbr==null)
			throw new NullPointerException("membersbr");
		
		if(!em.contains(membersbr)) {
			membersbr = em.merge(membersbr);
		}
		em.remove(membersbr);
	}
	
	/**
	 * Deletes all MemberSbrs with specified member.
	 * @param member not null
	 */
	public void deleteAllByMember(Member member) {
		if(member==null)
			throw new NullPointerException("member");
		
		Query query = em.createQuery("DELETE FROM MemberSbr as ms " +
				"WHERE ms.member = :member");
		
		query.setParameter("member", member);
		
		query.executeUpdate();
	}
	
	/**
	 * Deletes all MemberSbrs with specified sbr and member.
	 * @param sbr not null
	 * @param member not null
	 */
	public void deleteAllByMemberAndSbr(SBR sbr, Member member) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		if(member==null)
			throw new NullPointerException("member");
		
		Query query = em.createQuery("DELETE FROM MemberSbr ms WHERE ms.sbr = :sbr AND ms.member = :member");
		
		query.setParameter("sbr", sbr);
		query.setParameter("member", member);
		
		query.executeUpdate();
	}
	
	/**
	 * Deletes all MemberSbrs with specified member and level.
	 * @param member not null
	 * @param level
	 */
	public void deleteAllByMemberAndLevel(Member member, int level) {
		if(member==null)
			throw new NullPointerException("member");
		
		Query query = em.createQuery("DELETE FROM MemberSbr as ms " +
				"WHERE ms.member = :member AND " +
				"ms.level = :level");
		
		query.setParameter("member", member);
		query.setParameter("level", level);
		
		query.executeUpdate();
	}
	
	/**
	 * Deletes all MemberSbrs with specified SBR and level.
	 * @param sbr not null
	 * @param level
	 */
	public void deleteAllBySbrAndLevel(SBR sbr, int level) {
		if(sbr==null)
			throw new NullPointerException("sbr");
		
		Query query = em.createQuery("DELETE FROM MemberSbr as ms " +
				"WHERE ms.sbr = :sbr AND " +
				"level = :level");
		
		query.setParameter("sbr", sbr);
		query.setParameter("level", level);
		query.executeUpdate();
	}
	
}
