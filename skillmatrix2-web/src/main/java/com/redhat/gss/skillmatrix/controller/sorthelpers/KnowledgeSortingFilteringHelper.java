package com.redhat.gss.skillmatrix.controller.sorthelpers;

import org.richfaces.component.SortOrder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Sorting and filtering helper for Knowledge 
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class KnowledgeSortingFilteringHelper implements Serializable {
	private static final long serialVersionUID = 7631886204122119686L;

	private SortOrder nameOrder = SortOrder.ascending;
	private SortOrder expertsOrder = SortOrder.unsorted;
	
	private String nameFilter;

	/**
	 * Returns current sort order for name attribute.
	 * @return
	 */
	public SortOrder getNameOrder() {
		return nameOrder;
	}

	/**
	 * Sets sort order for name attribute.
	 * @param nameOrder
	 */
	public void setNameOrder(SortOrder nameOrder) {
		this.nameOrder = nameOrder;
	}

	/**
	 * Returns current sort order for experts attribute.
	 * @return
	 */
	public SortOrder getExpertsOrder() {
		return expertsOrder;
	}

	/**
	 * Sets sort order for experts attribute.
	 * @param expertsOrder
	 */
	public void setExpertsOrder(SortOrder expertsOrder) {
		this.expertsOrder = expertsOrder;
	}

	/**
	 * 
	 * @return filter for name
	 */
	public String getNameFilter() {
		return nameFilter;
	}

	/**
	 * Sets filter for name
	 * @param nameFilter
	 */
	public void setNameFilter(String nameFilter) {
		this.nameFilter = nameFilter;
	}
	
	/**
	 * Sorts by name. This method sets all corresponding sort orders.
	 */
	public void sortByName() {
		expertsOrder = SortOrder.unsorted;
		if(nameOrder.equals(SortOrder.ascending)) {
			setNameOrder(SortOrder.descending);
		} else {
			setNameOrder(SortOrder.ascending);
		}
	}
	
	/**
	 * Sorts by experts. This method sets all corresponding sort orders.
	 */
	public void sortByExperts() {
		nameOrder = SortOrder.unsorted;
		if(expertsOrder.equals(SortOrder.ascending)) {
			setExpertsOrder(SortOrder.descending);
		} else {
			setExpertsOrder(SortOrder.ascending);
		}
	}
	
	
}
