package com.redhat.gss.skillmatrix.controller;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.redhat.gss.skillmatrix.controller.sorthelpers.MemberSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.datamodels.MemberSearchModel;

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
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Bean controller for search results page.
 * This bean holds sorting and filtering helper with model,
 * that can be used for retrieval of search-results.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class SearchResultsController implements Serializable {
	private static final long serialVersionUID = 2323431513729217243L;

	@Inject
	private transient FacesContext facesCtx;

	@Inject
	private transient EntityManager em;
	
	@Inject
	private transient MemberManager memberManager;
	
	@Inject
	private transient PackageManager pkgManager;
	
	private MemberSearchModel model;
	private MemberSortingFilteringHelper memberHelper;

	
	
	private Pattern pkgPattern = Pattern.compile("pkg_(\\d+)");

	private String name;
	private String email;
	private String nick;
	private String languages;

	private Integer numlevel;
	private String numop;
	private Integer numval;

	private Map<Long, Integer> packages;


	
	/**
	 * Returns sorting and filtering helper for search results.
	 * @return
	 */
	public MemberSortingFilteringHelper getMemberHelper() {
		return memberHelper;
	}

	/**
	 * Returns true if there is at least one member.
	 * @return
	 */
	public boolean hasMembers() {
		return memberHelper.getModel().getRowCount() > 0;
	}
	
	/**
	 * @return the languages
	 */
	public String getLanguages() {
		return languages;
	}

	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(String languages) {
		this.languages = languages;
	}

	/**
	 * 
	 * @return Name filter
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return Email filter
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @return Nick filter
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * 
	 * @return level of knowledge for knowledge count predicate.
	 */
	public Integer getNumlevel() {
		return numlevel;
	}

	/**
	 * 
	 * @return operator for knowledge count predicate
	 */
	public String getNumop() {
		return numop;
	}
	
	/**
	 * @see #getNumop()
	 * @return human readable operator
	 */
	public String getReadeableNumOp() {
		if(numop!=null) {
			if(numop.equals("g")) {
				return "greater than";
			} else if(numop.equals("s")) {
				return "smaller than";
			} else if(numop.equals("e")) {
				return "equal to";
			} else if(numop.equals("ge")) {
				return "greater than or equal to";
			} else if(numop.equals("se")) {
				return "smaller than or equal to";
			}
		}
		
		return "";


	}
	
	/**
	 * @see #getNumlevel()
	 * @return human readable level of knowledge
	 */
	public String getReadeableNumLevel() {
		if(numlevel!=null) {
			if(numlevel==-1) {
				return "any level";
			} else if (numlevel == 0) {
				return "beginner level";
			} else if (numlevel==1) {
				return "intermediate level";
			} else if (numlevel==2) {
				return "expert level";
			}
			
		}
		
		return "";
	}
	
	/**
	 * Converts integer level to human readable format.
	 * @param level level to convert, should be within 0 and 2
	 * @return converted level
	 */
	public String toReadeableLevel(int level) {
		if(level==0) {
			return "beginner";
		} else if (level==1) {
			return "intermediate";
		} else if (level==2) {
			return "expert";
		}

		return "";
	}

	/**
	 * 
	 * @return value to be compared against for knowledge count predicate.
	 */
	public Integer getNumval() {
		return numval;
	}

	/**
	 * Package predicate info, member must have knowledge of certain package at certain level (or better).
	 * @return map of Package (or his ID) and minimum level of knowledge
	 */
	public Map<Long, Integer> getPackages() {
		return packages;
	}
	
	/**
	 * Retrieves package by its ID.
	 * @param id packages id, null is not allowed
	 * @return Package instance or null if not found.
	 */
	public Package getPackageByID(long id) {
		return pkgManager.getPkgById(id);
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
	
	private void addPredicates() {
		if(name!=null)
			memberHelper.setNameFilter(name);
			
		if(email!=null) 
			memberHelper.setEmailFilter(email);

		if(nick!=null)
			memberHelper.setNickFilter(nick);
		if(languages!=null)
			memberHelper.setLanguagesFilter(languages);
			
		if(numlevel!=null && numop!=null && numval!=null)
			model.addKnowledgeCountPredicate(numlevel, numop, numval);

		if(packages!=null && !packages.isEmpty())
			model.addPackageKnowledgePredicate(packages);

	}

	@PostConstruct
	private void initOrRedirect() {
		loadParams();
		
		this.model = new MemberSearchModel(em, memberManager);
		this.memberHelper = new MemberSortingFilteringHelper();
		memberHelper.setModel(model);
		
		addPredicates();
	}

	private void loadParams() {
		Map<String, String> params = facesCtx.getExternalContext().getRequestParameterMap();

		//basic search
		this.name = params.get("name");
		this.email = params.get("email");
		this.nick = params.get("nick");
		this.languages = params.get("languages");
		
		//advanced
		this.numlevel = tryParseInteger(params.get("numlevel"));
		this.numop = params.get("numop");
		this.numval = tryParseInteger(params.get("numval"));

		//package search
		packages = Maps.newHashMap();
		for (Entry<String, String>  entry : params.entrySet()) {
			Matcher matcher = pkgPattern.matcher(entry.getKey());
			if(matcher.matches()) {
				String id = matcher.group(1);
				Integer level = tryParseInteger(entry.getValue());
				if(level!=null) {
					this.packages.put(Long.valueOf(id), level);
				}
			}
		}

	}
	
	


	private Integer tryParseInteger(String text) {
		try {
			return Integer.valueOf(text);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
