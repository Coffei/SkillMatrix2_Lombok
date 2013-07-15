package com.redhat.gss.skillmatrix.controller;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redhat.gss.skillmatrix.controller.sorthelpers.MemberSortingFilteringHelper;
import com.redhat.gss.skillmatrix.controller.sorthelpers.PackageSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.util.datamodels.MemberInSBRModel;
import com.redhat.gss.skillmatrix.util.datamodels.PackageInSbrModel;

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
import java.util.Map;
import java.util.logging.Logger;

/**
 * Bean controller for SBR detail page.
 * This bean holds multiple sorting and filtering helpers with models,
 * that can be used for data retrieval.
 * @author jtrantin
 *
 */
@ViewScoped
@ManagedBean
public class SbrDetails implements Serializable {
	private static final long serialVersionUID = 2731612457573581536L;



	@Inject
	private transient SbrManager sbrManager;

	@Inject
	private transient PackageManager pkgManager;

	@Inject 
	private transient EntityManager em;

	@Inject
	private transient MemberManager memberManager;

	@Inject
	private transient FacesContext facesContext;

	@Inject
	private transient Logger log;

	private SBR sbr;

	private transient List<MemberSortingFilteringHelper> sortHelpers;

	private PackageSortingFilteringHelper pkgHelper;

	private Map<Member, Long> experts;

	private Map<Member, Long> bestPeople;



	/**
	 * Returns sorting and filtering helper for Package.
	 * @return
	 */
	public PackageSortingFilteringHelper getPkgHelper() {
		return pkgHelper;
	}

	/**
	 * Returns map of members and their expert knowledges of packages in this SBR.
	 * @return
	 */
	public Map<Member, Long> getExperts() {
		return experts;
	}

	/**
	 * Returns a list of sorting and filtering helpers for Members.
	 * @return
	 */
	public List<MemberSortingFilteringHelper> getSortHelpers() {
		return sortHelpers;
	}

	/**
	 * @return currently viewed SBR
	 */
	public SBR getSbr() {
		return sbr;
	}

	/**
	 * Sets currently viewed SBR
	 * @param sbr sbr to be viewed, usually not used
	 */
	public void setSbr(SBR sbr) {
		this.sbr = sbr;
	}

	/**
	 * @return a map of members and their percentage knowledge of current SBR.
	 */
	public Map<Member, Long> getBestPeople() {
		return bestPeople;
	}

	/**
	 * Counts members who have this SBR at specified level.
	 * @param level SBR level, should be within reasonable limits to return useful results.
	 * @return
	 */
	public int getMembersCount(int level) {
		if(level <= 0  || sortHelpers.size() < level || sortHelpers.get(level-1) == null)
			return 0;

		return sortHelpers.get(level-1).getModel().getRowCount();
	}

	/**
	 * @return true if this SBR has some members
	 */
	public boolean hasMembers() {
		for (MemberSortingFilteringHelper helper : sortHelpers) {

			if(helper!=null && !helper.getModel().isEmpty())
				return true;
		}

		return false;
	}

	/**
	 * Returns number of packages present.
	 * @return
	 */
	public int getPackagesCount() {
		return pkgHelper.getModel().getRowCount();
	}

	/**
	 * Returns true if this SBR has at least one Package.
	 * @return
	 */
	public boolean hasPackages() {
		return getPackagesCount() > 0;
	}


	/**
	 * Returns a list of numbers representing SBR levels, that are not empty.
	 * @return
	 */
	public List<Integer> getMemberSbrCount() {
		List<Integer> numbers = Lists.newArrayList();
		for (int i = 0; i < sortHelpers.size(); i++) {
			if(sortHelpers.get(i)!=null && !sortHelpers.get(i).getModel().isEmpty()) {
				numbers.add(i+1);
			}
		}

		return numbers;
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

	//actions


	//helpers
	@PostConstruct
	private void init() {
     	Long id = null;
		try {
			id = Long.parseLong(facesContext.getExternalContext().getRequestParameterMap().get("id"));
			sbr = sbrManager.getSbrById(id);
		} catch (NumberFormatException e) {
			//wrong param inserted, just ignore
		}

		experts = Maps.newHashMap();
		sortHelpers = Lists.newArrayList();

		if(sbr!=null) {//load other used stuff
			//Load members

			pkgHelper = new PackageSortingFilteringHelper();
			pkgHelper.setModel(new PackageInSbrModel(em, pkgManager, sbr));

			for (MemberSbr ms : sbr.getMembersbrs()) {
				log.info("processing membersbr " + ms);

				experts.put(ms.getMember(), memberManager.getNumberOfLevelKnowledge(ms.getMember(), ms.getLevel()));

				while(sortHelpers.size() < ms.getLevel() - 1) {
					sortHelpers.add(null);
				}

				if(sortHelpers.size() == (ms.getLevel() - 1)) {
					MemberSortingFilteringHelper helper = new MemberSortingFilteringHelper();
					helper.setModel(new MemberInSBRModel(em, memberManager, sbr, ms.getLevel()));  
					sortHelpers.add(helper);
				} else if (sortHelpers.get(ms.getLevel() - 1) == null) {
					MemberSortingFilteringHelper helper = new MemberSortingFilteringHelper();
					helper.setModel(new MemberInSBRModel(em, memberManager, sbr, ms.getLevel()));  
					sortHelpers.set(ms.getLevel() - 1, helper);
				}

				if(sortHelpers.size() >= ms.getLevel() && sortHelpers.get(ms.getLevel() - 1) == null) {
					MemberSortingFilteringHelper helper = new MemberSortingFilteringHelper();
					helper.setModel(new MemberInSBRModel(em, memberManager, sbr, ms.getLevel()));  
					sortHelpers.add(helper);
				}

			}


			bestPeople = Maps.newHashMap();
			Long sbrScore = (long)(sbr.getPackages()!=null? sbr.getPackages().size() * 4 : 0);
			if(sbrScore!=0l) {
				for (Object[] objects : memberManager.getPeopleWithBestKnowledgeOfSbr(sbr)) {
					if(objects[1]!=null)  {
						Long perc = (((Long)objects[1]) * 100) / sbrScore;
						if(perc>0) {
							bestPeople.put((Member) objects[0], perc);
						}
					}
				} 
			}
		}

	}
}


