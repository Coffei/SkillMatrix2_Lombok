package com.redhat.gss.skillmatrix.controller;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.redhat.gss.skillmatrix.controller.sorthelpers.MemberSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.util.datamodels.AllMembersModel;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Simple bean controller for members page.
 * This bean holds sorting and filtering helper, which holds
 * model, that can be used for retrieving data.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class MembersController implements Serializable {
	private static final long serialVersionUID = -5869983743963737466L;

	@Inject
	private EntityManager em;

	@Inject
	private MemberManager manager;


	@Inject
	private transient List<Member> members;

	@Inject
	private transient MemberManager memberManager;


	private MemberSortingFilteringHelper sortHelper;

	private Map<Member, Long> experts;

	/**
	 * Returns sorting and filtering helper.
	 * @return
	 */
	public MemberSortingFilteringHelper getSortHelper() {
		return sortHelper;
	}

	/**
	 * Returns a map of members and number of their expert knowledges.
	 * @return
	 */
	public Map<Member, Long> getExperts() {
		return experts;
	}

	/**
	 * Sets a member-expert map.
	 * @param experts
	 */
	public void setExperts(Map<Member, Long> experts) {
		this.experts = experts;
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
	
	public GeoEnum[] getGeoEnumValues() {
		return GeoEnum.values();
	}
	
	public RoleEnum[] getRoleEnumValues() {
		return RoleEnum.values();
	}

	//helpers
	@PostConstruct
	private void init() {
		experts = Maps.newHashMap();
		for (Member member : members) {
			experts.put(member, memberManager.getNumberOfLevelKnowledge(member, 2));
		}

		sortHelper = new MemberSortingFilteringHelper();
		sortHelper.setModel(new AllMembersModel(em, manager));

	}


}
