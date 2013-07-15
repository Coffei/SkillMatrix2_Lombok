package com.redhat.gss.skillmatrix.controller;

import com.google.common.collect.Maps;
import com.redhat.gss.skillmatrix.controller.sorthelpers.SbrSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.datamodels.SbrsModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Simple bean controller for SBRs pages.
 * This bean holds sorting and filtering helper with model, 
 * that can be used for data retrieval.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class SbrsController implements Serializable {
	private static final long serialVersionUID = -241575074795424112L;
	
	@Inject
	private transient List<SBR> sbrs;
	
	@Inject
	private transient SbrManager sbrManager;
	
	@Inject
	private transient EntityManager em;

	private Map<SBR, Long> memberCountMap;
	
	private SbrSortingFilteringHelper helper;
	
	
	/**
	 * Returns sorting and filtering helper.
	 * @return
	 */
	public SbrSortingFilteringHelper getHelper() {
		return helper;
	}

	
	/**
	 * Returns map of SBR and corresponding number of members at any level.
	 * @return map of SBR - Long
	 */
	public Map<SBR, Long> getMemberCountMap() { //
		return memberCountMap;
	}

	
	@PostConstruct
	private void init() {
		memberCountMap = Maps.newHashMap();
		for (SBR sbr : sbrs) {
			memberCountMap.put(sbr, sbrManager.getDistinctMemberCount(sbr));
		}
		
		helper = new SbrSortingFilteringHelper();
		helper.setModel(new SbrsModel(em, sbrManager));
	}
}
