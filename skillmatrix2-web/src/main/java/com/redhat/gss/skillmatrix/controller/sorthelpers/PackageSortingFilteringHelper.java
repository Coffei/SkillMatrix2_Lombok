package com.redhat.gss.skillmatrix.controller.sorthelpers;

import com.redhat.gss.skillmatrix.util.datamodels.abstracts.PackageModel;
import org.richfaces.component.SortOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Sorting and filtering helper for Package.
 * @author jtrantin
 *
 */
public class PackageSortingFilteringHelper implements Serializable {
	private static final long serialVersionUID = 2437752496863772680L;

	private PackageModel model;

	private Map<String, Object> filters = new HashMap<String, Object>();

	private SortOrder nameOrder = SortOrder.unsorted;
	private SortOrder expertsOrder = SortOrder.unsorted;
	private SortOrder sbrOrder = SortOrder.unsorted;

	/**
	 * Returns model, that can be used with RichFaces dataTables.
	 * @return
	 */
	public PackageModel getModel() {
		return model;
	}

	/**
	 * Sets a model, that can be used with RichFaces dataTables.
	 * @param model model to be used with this helper.
	 */
	public void setModel(PackageModel model) {
		this.model = model;

		if(model!=null) {
			model.doSort("name", SortOrder.ascending);
		}
	}
	/**
	 * @return filter for name
	 */
	public String getNameFilter() {
		return (String)filters.get("name");
	}
	/**
	 * Sets filter for name
	 * @param nameFilter
	 */
	public void setNameFilter(String nameFilter) {
		filters.put("name", nameFilter);

		if(model!=null) {
			model.doFilter(filters);
		}

	}
	/**
	 * @return filter for sbr
	 */
	public String getSbrFilter() {
		return (String)filters.get("sbr.name");
	}
	/**
	 * Sets filter for sbr
	 * @param sbrFilter
	 */
	public void setSbrFilter(String sbrFilter) {
		filters.put("sbr.name", sbrFilter);

		if(model!=null) {
			model.doFilter(filters);
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

		if(model!=null) {
			model.doFilter(filters);
		}
	}
	/**
	 * @return experts sort order
	 */
	public SortOrder getExpertsOrder() {
		return expertsOrder;
	}

	/**
	 * Sets experts sort order
	 * @param expertsOrder
	 */
	public void setExpertsOrder(SortOrder expertsOrder) {
		this.expertsOrder = expertsOrder;
	}

	/**
	 * Sorts by name, sets all corresponding sort orders.
	 */
	public void sortByName() {
		expertsOrder = sbrOrder = SortOrder.unsorted;
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
	 * @return sbr sort order
	 */
	public SortOrder getSbrOrder() {
		return sbrOrder;
	}

	/**
	 * Sets sbr sort order
	 * @param sbrOrder
	 */
	public void setSbrOrder(SortOrder sbrOrder) {
		this.sbrOrder = sbrOrder;
	}

	/**
	 * Sorts by experts, sets all corresponding sort orders.
	 */
	public void sortByExperts() {
		nameOrder = sbrOrder = SortOrder.unsorted;
		if(expertsOrder.equals(SortOrder.ascending)) {
			setExpertsOrder(SortOrder.descending);
		} else {
			setExpertsOrder(SortOrder.ascending);
		}
	}

	/**
	 * Sorts by sbr, sets all corresponding sort orders.
	 */
	public void sortBySbr() {
		nameOrder = expertsOrder = SortOrder.unsorted;
		if(sbrOrder.equals(SortOrder.ascending)) {
			setSbrOrder(SortOrder.descending);
		} else {
			setSbrOrder(SortOrder.ascending);
		}

		if(model!=null) {
			model.doSort("sbr.name", getSbrOrder());
		}
	}

}
