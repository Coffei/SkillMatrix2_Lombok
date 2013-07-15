package com.redhat.gss.skillmatrix.controller.sorthelpers;

import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.util.datamodels.SbrsModel;
import org.richfaces.component.SortOrder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Sorting and filtering helper for SBR
 * @author jtrantin
 *
 */
public class SbrSortingFilteringHelper implements Serializable {
	private static final long serialVersionUID = -3855996218668168469L;

	@Inject
	private EntityManager em;

	@Inject
	private SbrManager manager;

	private SortOrder nameOrder = SortOrder.unsorted;
	private SortOrder membersOrder = SortOrder.unsorted;
	private SortOrder packagesOrder = SortOrder.unsorted;

	private SbrsModel model;

	private Map<String, Object> filters = new HashMap<String, Object>();


	/**
	 * Returns model, that can be used with RichFaces dataTables.
	 * @return
	 */
	public SbrsModel getModel() {
		return this.model;
	}

	/**
	 * Sets a model, that can be used with RichFaces dataTables.
	 * @param model
	 */
	public void setModel(SbrsModel model) {
		this.model = model;

		if(model!=null) {
			model.doSort("name", SortOrder.ascending);
		}
	}

	/**
	 * @return name sort order
	 */
	public SortOrder getNameOrder() {
		return nameOrder;
	}

	/**
	 * Sets name sort order
	 * @param nameOrder
	 */
	public void setNameOrder(SortOrder nameOrder) {
		this.nameOrder = nameOrder;

	}

	/**
	 * @return members sort order
	 */
	public SortOrder getMembersOrder() {
		return membersOrder;
	}

	/**
	 * Sets members sort order
	 * @param membersOrder
	 */
	public void setMembersOrder(SortOrder membersOrder) {
		this.membersOrder = membersOrder;
	}

	/**
	 * @return packages sort order
	 */
	public SortOrder getPackagesOrder() {
		return packagesOrder;
	}

	/**
	 * Sets packages sort order
	 * @param packagesOrder
	 */
	public void setPackagesOrder(SortOrder packagesOrder) {
		this.packagesOrder = packagesOrder;
	}

	/**
	 * @return name filter
	 */
	public String getNameFilter() {
		return (String) filters.get("name");
	}

	/**
	 * Sets name filter 
	 * @param nameFilter
	 */
	public void setNameFilter(String nameFilter) {
		filters.put("name", nameFilter);

		if(model!=null) {
			model.doFilter(filters);
		}
	}

	/**
	 * Sorts by name and sets all corresponding sort orders.
	 */
	public void sortByName() {
		membersOrder = packagesOrder = SortOrder.unsorted;
		if(nameOrder.equals(SortOrder.ascending)) {
			setNameOrder(SortOrder.descending);
		} else {
			setNameOrder(SortOrder.ascending);
		}

		if(model!=null) {
			model.doSort("name", getNameOrder());
		}
	}

	/**
	 * Sorts by members and sets all corresponding sort orders.
	 */
	public void sortByMembers() {
		nameOrder = packagesOrder = SortOrder.unsorted;
		if(membersOrder.equals(SortOrder.ascending)) {
			setMembersOrder(SortOrder.descending);
		} else {
			setMembersOrder(SortOrder.ascending);
		}
	}

	/**
	 * Sorts by packages and sets all corresponding sort orders.
	 */
	public void sortByPackages() {
		nameOrder = membersOrder = SortOrder.unsorted;
		if(packagesOrder.equals(SortOrder.ascending)) {
			setPackagesOrder(SortOrder.descending);
		} else {
			setPackagesOrder(SortOrder.ascending);
		}
	}



}
