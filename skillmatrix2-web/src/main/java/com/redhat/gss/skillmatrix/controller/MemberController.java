package com.redhat.gss.skillmatrix.controller;

import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.controller.sorthelpers.PackageSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.*;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.MemberformHolder;
import com.redhat.gss.skillmatrix.util.datamodels.AllPackagesModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Bean controller for memberform (and for some other member-related pages).
 * This bean holds sorting and filtering helper with model, that can be used 
 * for data retrieval.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class MemberController implements Serializable {
	private static final long serialVersionUID = -703623975637224956L;

	@Inject
	private transient FacesContext facesCtx;


	@Inject
	private transient Logger log;

	@Inject
	private transient PackageManager pkgMan;

	@Inject
	private transient EntityManager em;

	@Inject
	private transient MemberManager memberManager;

	@Inject
	private transient SbrManager sbrManager;

	@Inject
	private transient MemberSbrManager memberSbrManager;

	@Inject
	private transient MemberformHolder memberHolder;

	@Inject
	private transient PackageKnowledgeManager pkgKnowManager;
	
	@Inject
	private transient LanguageKnowledgeManager langKnowManager;

	@Inject
	private transient Conversation conversation;

	private Member editMember;

	private PackageSortingFilteringHelper helper;

	// getters and setters
	/**
	 * Returns SBRs as list of long
	 * @return list of long, may be empty
	 */
	public List<Long> getSbrs() {
		return memberHolder.getSbrs();
	}

	/**
	 * Returns sorting and filtering helper.
	 * @return
	 */
	public PackageSortingFilteringHelper getHelper() {
		return helper;
	}

	/**
	 * Sets sorting and filtering helper.
	 * @param helper
	 */
	public void setHelper(PackageSortingFilteringHelper helper) {
		this.helper = helper;
	}

	/**
	 * Returns a member, that is being edited or created.
	 * @return instance of Member
	 */
	public Member getEditMember() {
		return editMember;
	}

	/**
	 * Sets a member that is being edited or created.
	 * @param member can be null
	 */
	public void setEditMember(Member member) {
		this.editMember = member;
		memberHolder.loadWith(member);
	}

	/**
	 * @return true if the member is edited, false if new member is created.
	 */
	public boolean isEditing() {
		return editMember.getId()!=null;
	}

	/**
	 * Returns list of numbers (1,2,...) representing SBR's level
	 * @return list of integers
	 */
	public List<Integer> getSbrsCount() {
		List<Integer> result = Lists.newArrayList();
		for(int i = 1; i<=getSbrs().size(); i++) {
			result.add(i);
		}
		return result;
	}

	/**
	 * Knowledge map contains entries for packages and their corresponding knowledges.
	 *  If knowledge == -1, then there is no knowledge.
	 * @return
	 */
	public Map<Package, Integer> getKnowledgeMap() {
		return memberHolder.getKnowledge();
	}

	/**
	 * Get number of rows in the entire dataset.
	 * @return
	 */
	public int getRowCount(){
		return helper.getModel().getRowCount();
	}

	//Language stuff
	public List<String> getLanguages() {
		return memberHolder.getLanguages();
	}

	public void addLanguage() {
		memberHolder.getLanguages().add("");
	}
	
	public GeoEnum[] getGeoEnumValues() {
		return GeoEnum.values();
	}
	
	public RoleEnum[] getRoleEnumValues() {
		return RoleEnum.values();
	}

	// action methods
	/**
	 * Submits new member or creates new one. Should be used as action method.
	 * @return route string
	 */
	public String submitNewMember() {
		Member tosave = memberManager.getMemberById(editMember.getId());
		
		
		//if not found, create new
		if(tosave==null) {
			tosave = new Member();
		}

		//copy values
		tosave.setName(editMember.getName());
		tosave.setNick(editMember.getNick());
		tosave.setEmail(editMember.getEmail());
		tosave.setExtension(editMember.getExtension());
		tosave.setGeo(editMember.getGeo());
		tosave.setRole(editMember.getRole());

		boolean isEdit;
		if(tosave.getId() == null) {
			memberManager.create(tosave);
			isEdit = false;
		} else {
			tosave = memberManager.update(tosave);
			isEdit = true;
		}

		//SBRs
		memberSbrManager.deleteAllByMember(tosave); // delete all membersbrs
		for(int i = 0; i < getSbrs().size(); i++) { // create all selected membersbrs
			SBR sbr = sbrManager.getSbrById(getSbrs().get(i));
			if(sbr!=null) {
				MemberSbr ms = new MemberSbr();
				ms.setLevel(i+1);
				ms.setMember(tosave);
				ms.setSbr(sbr);

				memberSbrManager.create(ms);
			}
		}

		//Knowledge
		// update what is possible, create new knowledges
		for(Map.Entry<Package, Integer> entry : memberHolder.getKnowledge().entrySet()) {
			if(entry.getValue()>=0) { // is valid knowledge
				//try to fetch knowledge
				List<PackageKnowledge> knows = pkgKnowManager.getAllByPackageAndMember(entry.getKey(), tosave);
				if(!knows.isEmpty()) { // something was retrieved, can be updated
					PackageKnowledge know =  knows.get(0);
					if(!know.getLevel().equals(entry.getValue())) {
						know.setLevel(entry.getValue());

						pkgKnowManager.update(know);
					}
				} else { // nothing found, new must be created
					PackageKnowledge know = new PackageKnowledge();
					know.setMember(tosave);
					know.setPackage(entry.getKey());
					know.setLevel(entry.getValue());

					pkgKnowManager.create(know);
				}
			}
		}

		// delete removed knowledges
		pkgKnowManager.deleteAllWithMemberNotInPackages(tosave, memberHolder.getPackagesWithKnowledge());

		//Languages
		for(String language : getLanguages()) {
			if(language!=null && !language.trim().isEmpty()) {
				language = language.trim().toUpperCase(Locale.ENGLISH);
				log.info("processing " + language);
				if(!langKnowManager.existsByMemberLanguage(tosave, language)) {// record is not in DB, create one
					LanguageKnowledge know = new LanguageKnowledge();
					know.setLanguage(language);
					know.setMember(tosave);
					
					langKnowManager.create(know);
					log.info(language + " created");
				}
			}
		}
		
		// delete removed/changed languages
		log.info("deleting all but " + getLanguages());
		langKnowManager.deleteAllWithMemberNotInLanguages(tosave, getLanguages());
		

		FacesMessage m;
		if(isEdit) {
			m = new FacesMessage("User updated.");
			memberHolder.loadWith(memberManager.getMemberById(tosave.getId()));
		} else {
			postConstruct();
			memberHolder.loadWith(null);
			m = new FacesMessage("User created.");
		}

		m.setSeverity(FacesMessage.SEVERITY_INFO);

		facesCtx.addMessage(null, m);

		return null;
	}

	/**
	 * Deletes a member. Is intended to be used as action method.
	 * @param member member to delete
	 * @return route string
	 */
	public String delete(Member member) {
		memberManager.delete(member);

		facesCtx.getPartialViewContext().setRenderAll(true);
		return null;
	}

	/**
	 * Action method, adds another empty SBR slot.
	 */
	public void addSbr() {
		memberHolder.getSbrs().add(null);
	}

    public void sortByLevel() {
        memberHolder.sortByLevel((AllPackagesModel)this.helper.getModel());
    }


	//helper methods
	@PostConstruct
	private void postConstruct() {
		String editid = facesCtx.getExternalContext().getRequestParameterMap().get("editid");
		if(editid!=null) {
			try {
				Long id = Long.parseLong(editid);
				Member member = memberManager.getMemberById(id);
				if(member!=null) {
					setEditMember(member);
				} else {
					this.editMember = new Member();
				}
			} catch (NumberFormatException e) {
				this.editMember = new Member();

			}
		} else {
			this.editMember = new Member();
		}

		helper = new PackageSortingFilteringHelper();
		AllPackagesModel model = new AllPackagesModel(em, pkgMan);
		helper.setModel(model);
		model.setPreferred(memberHolder.getPackagesWithKnowledge());


	}


}
