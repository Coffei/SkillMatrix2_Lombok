package com.redhat.gss.skillmatrix.controller;

import com.redhat.gss.skillmatrix.controller.sorthelpers.MemberSortingFilteringHelper;
import com.redhat.gss.skillmatrix.controller.sorthelpers.PackageSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.data.MemberSbrManager;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.SbrformHolder;
import com.redhat.gss.skillmatrix.util.datamodels.AllMembersModel;
import com.redhat.gss.skillmatrix.util.datamodels.AllPackagesModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Bean controller for SBR form and other sbr-related pages.
 * This bean holds sorting and filtering helper with model, that
 * can be used for data retrieval.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class SbrController implements Serializable {
	private static final long serialVersionUID = 2777874588910541772L;

	@Inject
	private transient EntityManager em;

	@Inject
	private transient SbrManager sbrManager;

	@Inject
	private transient SbrformHolder sbrformHolder;

	@Inject 
	private transient MemberManager memberMan;

	@Inject
	private transient MemberSbrManager memberSbrManager;

	@Inject
	private transient FacesContext facesCtx;

	@Inject
	private transient PackageManager packageMan;

	@Inject
	private transient Logger log;

	private MemberSortingFilteringHelper memberHelper;

	private PackageSortingFilteringHelper pkgHelper;

	private List<Member> availableCoaches;

	private Long coachID;

	private SBR sbr;


	//getters, setters
	/**
	 * Returns sorting and filtering helper for Member.
	 * @return 
	 */
	public MemberSortingFilteringHelper getMemberHelper() {
		return memberHelper;
	}
	
	/**
	 * Returns sorting and filtering helper for Package.
	 * @return
	 */
	public PackageSortingFilteringHelper getPkgHelper() {
		return pkgHelper;
	}
	
	/**
	 * Returns existing SBR that is being edited, or new SBR that is being created.
	 * @return
	 */
	public SBR getSbr() {
		return sbr;
	}

	/**
	 * @see #getSbr()
	 * @param sbr
	 */
	public void setSbr(SBR sbr) {
		if(sbr==null) {
			this.sbr = new SBR();
		} else {
			this.sbr = sbr;
			sbrformHolder.reloadWith(sbr);
			reloadCoaches();
			this.coachID = sbr.getCoach()!=null? sbr.getCoach().getId() : null;
		}
	}

	/**
	 * Returns current SBR coaches ID.
	 * @return Long id
	 */
	public Long getCoachID() {
		return coachID;
	}

	/**
	 * @see #getCoachID()
	 * @param coachID
	 */
	public void setCoachID(Long coachID) {
		this.coachID = coachID;
	}

	/**
	 * Returns a list of members which are eligible for SBR coach. Basically it is everybody, who has this SBR at any level.
	 * @return list of members
	 */
	public List<Member> getAvailableCoaches() {
		return availableCoaches;
	}


	/**
	 * @return true if current SBR is being edited, false if new one is being created.
	 */
	public boolean isEditing() {
		return sbr.getId()!=null;
	}




	// action methods
	/**
	 * Creates or updates SBR with current data. Intended to be used as action method.
	 * @return route String
	 */
	public String submitSbr() {
		//fetch
		SBR tosave = sbrManager.getSbrById(sbr.getId());

		if(tosave==null) { //if not exists, then create new
			tosave = new SBR();
		}

		//copy attributes
		List<Package> pkgToDelete = (tosave.getPackages()!=null? tosave.getPackages() : new ArrayList<Package>());
		tosave.setName(sbr.getName());
		tosave.setPackages(sbrformHolder.getSelectedPackages()); //copy packages
		//coach
		if(coachID!=null) {
			Member coach = memberMan.getMemberById(coachID);
			tosave.setCoach(coach);
		}

		pkgToDelete.removeAll(tosave.getPackages()); //packages to delete because they have no sbr

		log.info("deleting " + pkgToDelete);

		//package deletion- ones without SBR are deleted
		for(Package pkg : pkgToDelete) {
			packageMan.delete(pkg);
		}

		//save
		FacesMessage m;
		if(tosave.getId()==null) {
			sbrManager.create(tosave);
			m = new FacesMessage(FacesMessage.SEVERITY_INFO, "New SBR created.", "New sbr created.");
		} else {
			sbrManager.update(tosave);
			m = new FacesMessage(FacesMessage.SEVERITY_INFO, "SBR updated.", "SBR updated.");
		}

		//update packages
		for (Package p : tosave.getPackages()) {
			if(!p.getSbr().equals(tosave)) {
				p.setSbr(tosave);
				packageMan.update(p);
			}
		}

		//update, create MemberSbr
		
		memberSbrManager.deleteAllBySbrAndLevel(tosave, 1);

		for(Member member : sbrformHolder.getSelectedMembers()) {
			memberSbrManager.deleteAllByMemberAndLevel(member, 1);
			memberSbrManager.deleteAllByMemberAndSbr(tosave, member);// delete everything relevant
			MemberSbr ms = new MemberSbr();
			ms.setLevel(1);
			ms.setSbr(tosave);
			ms.setMember(member);

			memberSbrManager.create(ms);// create relevant objects
		}

		facesCtx.addMessage(null, m);

		return null;
	}


	/**
	 * Updates available coaches. Can be used as action method.
	 */
	public void reloadCoaches() {
		this.availableCoaches = sbrformHolder.getSelectedMembers();

		if(sbr!=null && sbr.getMembersbrs()!=null) {
			for (MemberSbr ms : sbr.getMembersbrs()) {
				if(ms.getLevel()!=1) {
					this.availableCoaches.add(ms.getMember());
				}
			}
		}

		Collections.sort(this.availableCoaches, new Comparator<Member>() {
			@Override
			public int compare(Member o1, Member o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}

	//this will delete all packages in this SBR, thus all knowledges of these packages will be deleted
	/**
	 * Starts SBR deleting procedure. Intended to be used as action method.
	 * @param sbr SBR to be deleted if null nothing happens.
	 * @return route String
	 */
	public String delete(SBR sbr) {
		if(sbr!=null) {
			sbrManager.delete(sbr);
		}
	
		return null;
	}

	// helper methods
	@PostConstruct
	private void init() {
		String strid = facesCtx.getExternalContext().getRequestParameterMap().get("editid");
		if(strid!=null && !strid.isEmpty()) {
			try {
				long id = Long.parseLong(strid);
				setSbr(sbrManager.getSbrById(id));
			} catch (NumberFormatException e) {
				this.sbr = new SBR();
			}
		} else {
			this.sbr = new SBR();
		}

		reloadCoaches();

		memberHelper = new MemberSortingFilteringHelper();
		AllMembersModel memberModel = new AllMembersModel(em, memberMan); 
		memberHelper.setModel(memberModel);
		memberModel.setPreferred(sbrformHolder.getSelectedMembers());
		
		
		pkgHelper = new PackageSortingFilteringHelper();
		AllPackagesModel model = new AllPackagesModel(em, packageMan);
		pkgHelper.setModel(model);
		model.setPreferred(sbrformHolder.getSelectedPackages());
		

	}


}
