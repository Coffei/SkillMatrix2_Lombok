package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.Member;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Producer of Members.
 * @author jtrantin
 *
 */
@RequestScoped
public class MemberProducer {

	@Inject 
	private EntityManager em; 
	
	@Inject
	private MemberManager memberMan;

	private List<Member> members;

	/**
	 * @return all members ordered by name
	 */
	@Produces
	@Named
	public List<Member> getMembers() {
		return members;
	}

	/**
	 * Observer for Memebr change
	 * @param member
	 */
	public void onMemberListChanged(@Observes final Member member) {
		loadMembersOrderedByName();
	}

	@PostConstruct
	private void loadMembersOrderedByName() {
		members = memberMan.getAllMembers();
	}

}
