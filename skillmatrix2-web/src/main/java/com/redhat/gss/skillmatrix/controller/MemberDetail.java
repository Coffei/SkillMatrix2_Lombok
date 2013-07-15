package com.redhat.gss.skillmatrix.controller;

import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.controller.sorthelpers.PackageSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.util.datamodels.PackageInMemberModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Bean controller for members detail page.
 * This bean holds sorting and filtering helpers for each knowledge level.
 * These helpers hold models, that can be used for data retrieval.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class MemberDetail implements Serializable {
	private static final long serialVersionUID = -4424656276388335600L;
	

	@Inject
	private transient FacesContext facesContext;
	
	@Inject
	private transient EntityManager em;
	
	@Inject
	private transient PackageManager pkgManager;
	
	@Inject
	private transient MemberManager memberManager;
	
	private List<PackageSortingFilteringHelper> helpers;
	
	private Member member;
	
	/**
	 * Retrieves list of sorting and filtering helpers.
	 * @return
	 */
	public List<PackageSortingFilteringHelper> getHelpers() {
		return helpers;
	}

	/**
	 * Returns currently opened member, or null if no member was correctly specified.
	 * @return
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * Sets currently opened member.
	 * @param member
	 */
	public void setMember(Member member) {
		this.member = member;
	}
	
	/**
	 * Returns number of packages (rows) at specified level.
	 * @param atLevel
	 * @return
	 */
	public int countPackages(int atLevel) {
		return helpers.get(atLevel).getModel().getRowCount();
		
	}
	
	
	/**
	 * @return false if there is at least one PackageKnowledge associated with this member, true if not.
	 */
	public boolean isPackageKnowledgesEmpty() {
		//efficient only if there is many PackageKnowledges to other Knowledges.
		for (Knowledge know : member.getKnowledges()) {
			if(know instanceof PackageKnowledge) {
				return false;
			}
		}
		
		return true;
	}

    public String getLanguages(){
        StringBuilder builder = new StringBuilder();
        for(Knowledge know : this.member.getKnowledges()) {
            if(know instanceof LanguageKnowledge) {
                if(builder.length() != 0)
                    builder.append(", ");

                builder.append(((LanguageKnowledge)know).getLanguage());
            }
        }

        if(builder.length()==0)
            return "none";

        return builder.toString();
    }
	
	@PostConstruct
	private void init() {
		String sid = facesContext.getExternalContext().getRequestParameterMap().get("id");
		if(sid!=null) {
			long id = Long.parseLong(sid);
			this.member = memberManager.getMemberById(id);
			Collections.sort(member.getMembersbrs(), new Comparator<MemberSbr>() {

				@Override
				public int compare(MemberSbr o1, MemberSbr o2) {
					return Integer.compare(o1.getLevel(), o2.getLevel());
				}
				
			});
			
			this.helpers = Lists.newArrayListWithCapacity(3);
			
			for(int i = 0; i < 3; i++) {
				PackageSortingFilteringHelper helper = new PackageSortingFilteringHelper();
				helper.setModel(new PackageInMemberModel(em, pkgManager, i, member));
				this.helpers.add(helper);
			}
			
		}
	}

}
