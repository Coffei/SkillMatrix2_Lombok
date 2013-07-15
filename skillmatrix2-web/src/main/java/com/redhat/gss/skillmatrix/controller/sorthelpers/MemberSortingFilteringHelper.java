package com.redhat.gss.skillmatrix.controller.sorthelpers;

import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.model.RoleEnum;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.MemberModel;
import org.richfaces.component.SortOrder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Sorting and filtering helper for Member.
 * @author jtrantin
 *
 */
public class MemberSortingFilteringHelper implements Serializable {
	private static final long serialVersionUID = -233150952694796526L;

	//model
	private MemberModel model;

	// sorting
	private SortOrder name = SortOrder.unsorted;
	private SortOrder nick = SortOrder.unsorted;
	private SortOrder email = SortOrder.unsorted;
	private SortOrder experts = SortOrder.unsorted;
	private SortOrder role = SortOrder.unsorted;
	private SortOrder geo = SortOrder.unsorted;
	private SortOrder extension = SortOrder.unsorted;

	//filtering
	private Map<String, Object> filters = new HashMap<String, Object>();

	/**
	 * Returns model, that can be used with RichFaces dataTables.
	 * @return
	 */
	public MemberModel getModel() {
		return this.model;
	}

	/**
	 * Sets model, that can be used with RichFaces dataTables.
	 * @param model model to be used with this helper.
	 */
	public void setModel(MemberModel model) {
		this.model = model;

		if(model!=null) {
			model.doSort("name", SortOrder.ascending);
		}
	}

	/**
	 * 
	 * @return filter for name
	 */
	public String getNameFilter() {
		return (String)filters.get("name");
	}

	/**
	 * Sets filter for name.
	 * @param nameFilter
	 */
	public void setNameFilter(String nameFilter) {
		filters.put("name", nameFilter);

		if (model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}

	/**
	 * 
	 * @return filter for nick
	 */
	public String getNickFilter() {
		return (String)filters.get("nick");
	}
	
	/**
	 * Sets filter for nick
	 * @param nickFilter
	 */
	public void setNickFilter(String nickFilter) {
		filters.put("nick", nickFilter);

		if(model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}

	/**
	 * 
	 * @return filter for email
	 */
	public String getEmailFilter() {
		return (String)filters.get("email");
	}

	/**
	 * Sets filter for email.
	 * @param emailFilter
	 */
	public void setEmailFilter(String emailFilter) {
		filters.put("email", emailFilter);

		if(model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}
	
	public void setLanguagesFilter(String langFilter) {
		filters.put("languages", langFilter);
		
		if(model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}
	
	public String getLanguagesFilter() {
		return (String)filters.get("languages");
	}
	
	public void setRoleFilter(RoleEnum roleFilter) {
		filters.put("role", roleFilter);
		
		if(model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}
	
	public RoleEnum getRoleFilter() {
		return (RoleEnum)filters.get("role");
	}
	
	public void setGeoFilter(GeoEnum geoFilter) {
		filters.put("geo", geoFilter);
		
		if(model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}
	
	public GeoEnum getGeoFilter() {
		return (GeoEnum)filters.get("geo");
	}
	
	public void setExtensionFilter(String extFilter) {
		filters.put("extension", extFilter);
		
		if(model!=null) {
			model.doFilter(Collections.unmodifiableMap(filters));
		}
	}
	
	public String getExtensionFilter() {
		return (String)filters.get("extension");
	}
	


	//sorting
	/**
	 * Sorts by name, sets all corresponding sort orders.
	 */
	public void sortByName() {
		nick = email = experts = role = geo = extension = SortOrder.unsorted;
		if(SortOrder.ascending.equals(name)) {
			setName(SortOrder.descending);
		} else {
			setName(SortOrder.ascending);
		}

		if(model!=null) {
			model.doSort("name", getName());
		}
	}
	
	public void sortByRole() {
		name = nick = experts = geo = extension = email = SortOrder.unsorted;
		if(SortOrder.ascending.equals(role)) {
			setRole(SortOrder.descending);
		} else {
			setRole(SortOrder.ascending);
		}
		
		if(model!=null) {
			model.doSort("role", getRole(), true);
		}
	}
	
	public void sortByGeo() {
		name = nick = experts = role = extension = email = SortOrder.unsorted;
		if(SortOrder.ascending.equals(geo)) {
			setGeo(SortOrder.descending);
		} else {
			setGeo(SortOrder.ascending);
		}
		
		if(model!=null) {
			model.doSort("geo", getGeo(), true);
		}
	}
	
	public void sortByExtension() {
		name = nick = experts = geo = role = email = SortOrder.unsorted;
		if(SortOrder.ascending.equals(extension)) {
			setExtension(SortOrder.descending);
		} else {
			setExtension(SortOrder.ascending);
		}
		
		if(model!=null) {
			model.doSort("extension", getExtension());
		}
	}

	/**
	 * Sorts by nick, sets all corresponding sort orders.
	 */
	public void sortByNick() {
		name = email = experts = role = geo = extension =  SortOrder.unsorted;
		if(SortOrder.ascending.equals(nick)) {
			setNick(SortOrder.descending);
		} else {
			setNick(SortOrder.ascending);
		}

		if(model!=null) {
			model.doSort("nick", getNick()); 
		}

	}

	/**
	 * Sorts by email, sets all corresponding sort orders.
	 */
	public void sortByEmail()  {
		name = nick = experts = role = geo = extension =  SortOrder.unsorted;
		if(SortOrder.ascending.equals(email)) {
			setEmail(SortOrder.descending);
		} else {
			setEmail(SortOrder.ascending);
		}

		if(model!=null) {
			model.doSort("email", getEmail()); 
		}
	}

	/**
	 * Sorts by experts, sets all corresponding sort orders.
	 */
	public void sortByExperts() {
		name = nick = email = role = geo = extension =  SortOrder.unsorted;
		if(SortOrder.ascending.equals(experts)) {
			setExperts(SortOrder.descending);
		} else {
			setExperts(SortOrder.ascending);
		}
	}

	
	//getters, setters, uninteresting
	/**
	 * @return sort order for name.
	 */
	public SortOrder getName() {
		return name;
	}
	/**
	 * Sets sort order for name
	 * @param name
	 */
	public void setName(SortOrder name) {
		this.name = name;
	}

	/**
	 * @return sort order for nick.
	 */
	public SortOrder getNick() {
		return nick;
	}
	/**
	 * Sets sort order for nick
	 * @param nick
	 */
	public void setNick(SortOrder nick) {
		this.nick = nick;
	}

	/**
	 * @return sort order for email.
	 */
	public SortOrder getEmail() {
		return email;
	}
	/**
	 * Sets sort order for email
	 * @param email
	 */
	public void setEmail(SortOrder email) {
		this.email = email;
	}

	/**
	 * @return sort order for experts.
	 */
	public SortOrder getExperts() {
		return experts;
	}
	/**
	 * Sets sort order for experts
	 * @param experts
	 */
	public void setExperts(SortOrder experts) {
		this.experts = experts;
	}

	/**
	 * @return the role
	 */
	public SortOrder getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(SortOrder role) {
		this.role = role;
	}

	/**
	 * @return the geo
	 */
	public SortOrder getGeo() {
		return geo;
	}

	/**
	 * @param geo the geo to set
	 */
	public void setGeo(SortOrder geo) {
		this.geo = geo;
	}

	/**
	 * @return the extension
	 */
	public SortOrder getExtension() {
		return extension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setExtension(SortOrder extension) {
		this.extension = extension;
	}



}
