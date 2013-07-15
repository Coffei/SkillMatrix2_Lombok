package com.redhat.gss.skillmatrix.controller;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.redhat.gss.skillmatrix.controller.sorthelpers.MemberSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.datamodels.MemberInPackageModel;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Bean controller for package detail page.
 * This bean holds sorting and filtering helpers for each knowledge level.
 * These helpers hold models, that can be used for data retrieval.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class PackageDetail implements Serializable {
	private static final long serialVersionUID = 10048209105811259L;

	@Inject 
	private transient FacesContext facesCtx;
	
	@Inject
	private transient PackageManager pkgMan;
	
	@Inject
	private transient MemberManager memberManager;
	
	@Inject
	private EntityManager em;
	
	private List<MemberSortingFilteringHelper> memberHelpers;
	
	private Package pkg;
	
	
	/**
	 * Returns a list of sorting and filtering helpers.
	 * Index of helper in the list equals to corresponding knowledge-level.
	 * @return
	 */
	public List<MemberSortingFilteringHelper> getMemberHelpers() {
		return memberHelpers;
	}

	/**
	 * Returns currently opened package.
	 * @return Instance of package or null
	 */
	public Package getPackage() {
		return pkg;
	}

	/**
	 * @see #getPackage()
	 * @param pkg
	 */
	public void setPackage(Package pkg) {
		this.pkg = pkg;
	}
	
	/**
	 * Returns number of expert knowledges for specified member.
	 * @param member
	 * @return number of experts
	 */
	public long getExpertsCount(Member member) {
		if(member==null)
			return 0;
		
		return memberManager.getNumberOfLevelKnowledge(member, 2);
	}
	
	/**
	 * Returns number of members with knowledge (of this package) at specified level.
	 * @param atLevel
	 * @return
	 */
	public int getMembersCount(int atLevel) {
		return memberHelpers.get(atLevel).getModel().getRowCount();
	}
	
	/**
	 * @param atLevel
	 * @return true if this package has members
	 */
	public boolean hasMembers(int atLevel) {
		return getMembersCount(atLevel) > 0;
	}
	
	public String joinLanguages(List<Knowledge> knowledges) {
		if(knowledges==null || knowledges.isEmpty())
			return "";
		
		Collection<Knowledge> languages = Collections2.filter(knowledges, new Predicate<Knowledge>() {// keep only LanguageKnowledges
			@Override
			public boolean apply(@Nullable Knowledge input) {
				return input instanceof LanguageKnowledge;
			}
		});
		
		return Joiner.on(", ").join(Collections2.transform(languages, new Function<Knowledge, String>() {// joins languages (eg. CS, EN, ...)
			@Override
			@Nullable
			public String apply(@Nullable Knowledge input) {
				return ((LanguageKnowledge) input).getLanguage();
			}
		}));
	}
	
	
	//helpers
	@PostConstruct
	private void init() {
      	String sid = facesCtx.getExternalContext().getRequestParameterMap().get("id");
		if(sid!=null) { //load package
			try {
				long id = Long.parseLong(sid);
				pkg = pkgMan.getPkgById(id);
						
			} catch (NumberFormatException e) {	} //nothing to do
			
			
			
			if(pkg!=null) {//load members by knowledge
				
				memberHelpers = Lists.newArrayListWithCapacity(3);
				for(int i = 0; i < 3; i++) {
					MemberSortingFilteringHelper helper = new MemberSortingFilteringHelper();
					helper.setModel(new MemberInPackageModel(em, memberManager, pkg, i));
					memberHelpers.add(helper);
				}
			}
		}
	}
	
	

}
