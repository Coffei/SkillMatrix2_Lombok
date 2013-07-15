package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Bean for holding (storing) sbr form data over requests.
 * It is conversation scoped and starts a conversation on creation.
 * @author jtrantin
 *
 */
@Named
@ConversationScoped
public class SbrformHolder implements Serializable {
	private static final long serialVersionUID = -3475666708972520386L;

	@Inject
	private Logger log;

	@Inject
	private Conversation conversation;

	private Map<Member, Boolean> membershipMap = new HashMap<Member, Boolean>();

	private Map<Package, Boolean> packageMap = new HashMap<Package, Boolean>();

	/**
	 * Returns Member membership map- if exists entry member-true, then member has (or is going have) SBR as his primary.
	 * @return
	 */
	public Map<Member, Boolean> getMembershipMap() {
		return membershipMap;
	}

	/**
	 * Returns Package membership map- if exists entry package-true, then package belongs (or is going to) to SBR.
	 * @return
	 */
	public Map<Package, Boolean> getPackageMap() {
		return packageMap;
	}
	
	/**
	 * Return members, that have true value in membershipMap.
	 * @return
	 */
	public List<Member> getSelectedMembers() {
		List<Member> result = new ArrayList<Member>();
		for (Map.Entry<Member, Boolean> entry : membershipMap.entrySet()) { //get available coaches
			if(entry.getValue()!=null && entry.getValue().booleanValue()) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}
	
	/**
	 * Returns members, that have true value in the packageMap.
	 * @return
	 */
	public List<Package> getSelectedPackages() {
		List<Package> result = new ArrayList<Package>();
		for (Map.Entry<Package, Boolean> entry : packageMap.entrySet()) {
			if(entry.getValue()!=null && entry.getValue().booleanValue()) {
				result.add(entry.getKey());
			}
		}
		
		return result;
	}

    /**
     * Reloads this holder with values valid for specified SBR.
     * @param sbr sbr to load information from, can be null
     */
	public void reloadWith(SBR sbr) {
		if(sbr==null)
			return;

		
		if(sbr.getMembersbrs()!=null) {
			for (MemberSbr ms : sbr.getMembersbrs()) {
				if(ms.getLevel()==1) {
					membershipMap.put(ms.getMember(), true);
				}
			}
		}
		
		if(sbr.getPackages()!=null) {
			for (Package pkg : sbr.getPackages()) {
				packageMap.put(pkg, true);
			}
		}

	}

	@PostConstruct
	private void init() {
		if(conversation.isTransient()) {
			conversation.begin();
			log.info("conversation started " + conversation.getId());
		}
	}

}
