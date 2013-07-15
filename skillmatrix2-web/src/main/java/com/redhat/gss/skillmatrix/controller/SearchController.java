package com.redhat.gss.skillmatrix.controller;

import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.SearchHolder;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;


/**
 * Bean controller for member searching.
 * This bean provides predicates for basic members attributes (name, nick, email) + predicate that filter by number of knowledges at certain level.
 * @author jtrantin
 * 
 *
 */
@RequestScoped
@Named
public class SearchController {

	@Inject
	private Conversation conversation;

	@Inject
	private SearchHolder searchHolder;
	
	private String name;

	private String nick;

	private String email;
	
	private String languages;

	private Integer numknowlevel;

	private String numknowtype;

	private Integer numknowvalue;
	
	
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
	 * Getter for name attribute. This attribute describes filter (predicate), member must contain this value in his name. If name is null, then this predicate is not applied.
	 * @return name filter, may be null
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name attribute. If null, then the predicate is not applied.
	 * @see #getName()
	 * @param name can be null
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for nick attribute. This attribute describes filter (predicate), member must contain this value in his nick. If nick is null, then this predicate is not applied.
	 * @return nick filter, may be null
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Setter for nick attribute. If null, then the predicate is not applied.
	 * @see #getNick()
	 * @param nick can be null.
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * Getter for email attribute. This attribute describes filter (predicate), member must contain this value in his email. If email is null, then this predicate is not applied.
	 * @return email filter, may be null
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Setter for email attribute. If null, then predicate is not applied.
	 * @see #getEmail()
	 * @param email can be null
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Getter for knowledge on certain level predicate. Returns selected level of knowledge or -1 as no level-limitation is applied. 
	 * If null then this predicate is not applied.
	 * @return level of knowledge or -1, may be null
	 */
	public Integer getNumknowlevel() {
		return numknowlevel;
	}

	/**
	 * Setter for knowledge on certain level predicate. Sets selected knowledge level.
	 * @see #getNumknowlevel()
	 * @param numknowlevel level of knowledge or -1 or null
	 */
	public void setNumknowlevel(Integer numknowlevel) {
		this.numknowlevel = numknowlevel;
	}

	/**
	 * Getter for knowledge on certain level predicate. Returns operator for this predicate (e.g. >, <, ==,...) in internal representation.
	 * If null then the predicate is not applied.
	 * <table border=1>
	 * <th>operation symbol</th>
	 * <th>internal value</th>
	 * 
	 * <tr><td><</td><td>"s"</td></tr>
	 * <tr><td><=</td><td>"se"</td></tr>
	 * <tr><td>></td><td>"g"</td></tr>
	 * <tr><td>>=</td><td>"ge"</td></tr>
	 * <tr><td>==</td><td>"e"</td></tr>
	 * </table>
	 * @return
	 */
	public String getNumknowtype() {
		return numknowtype;
	}

	/**
	 * Setter for knowledge on cetrain level predicate. Sets operator,
	 * @see #getNumknowtype()
	 * @param numknowtype internal string representation of operator
	 */
	public void setNumknowtype(String numknowtype) {
		this.numknowtype = numknowtype;
	}

	/**
	 * Getter for knowledge on certain level predicate. Returns value to be compared against. 
	 * If null, then the predicate is not applied.
	 * @return whatever value, can be null
	 */
	public String getNumknowvalue() {
		return numknowvalue==null? "" : numknowvalue.toString();
	}

	/**
	 * Setter for knowledge on certain level predicate. Sets value to be compared against.
	 * If null then the predicate is not applied.
	 * @param numknowvalue sensible value (to get sensible results), can be null
	 */
	public void setNumknowvalue(String numknowvalue) {
		try {
			this.numknowvalue = Integer.parseInt(numknowvalue);
		} catch (NumberFormatException e) {
			this.numknowvalue = null;
		}
	}


	//actions
	/**
	 * Starts search procedure. Builds predicates and redirects to search results. Intended to be used as action method.
	 * @return route String
	 */
	public String doSearch() {
		name = name.trim();
		email = email.trim();
		nick = nick.trim();

		StringBuilder urlbuilder = new StringBuilder("search.jsf?faces-redirect=true");

		urlbuilder.append("&cid=");
		urlbuilder.append(conversation.getId());
		if(name!=null && !name.isEmpty()) {
			urlbuilder.append("&name=");
			urlbuilder.append(name);
		}

		if(email!=null && !email.isEmpty()) {
			urlbuilder.append("&email=");
			urlbuilder.append(email);
		}

		if(nick!=null && !nick.isEmpty()) {
			urlbuilder.append("&nick=");
			urlbuilder.append(nick);
		}
		
		if(languages!=null && !languages.isEmpty()) {
			urlbuilder.append("&languages=");
			urlbuilder.append(languages);
		}

		if(numknowvalue!=null && numknowlevel != null && numknowtype != null) {
			urlbuilder.append("&numlevel=");
			urlbuilder.append(numknowlevel);
			urlbuilder.append("&numop=");
			urlbuilder.append(numknowtype);
			urlbuilder.append("&numval=");
			urlbuilder.append(numknowvalue);
		}

		for (Map.Entry<Package, Integer> entry : searchHolder.getPackageMap().entrySet()) {
			urlbuilder.append("&pkg_");
			urlbuilder.append(entry.getKey().getId());
			urlbuilder.append("=");
			urlbuilder.append(entry.getValue());
		}


		return urlbuilder.toString();
	}


}
