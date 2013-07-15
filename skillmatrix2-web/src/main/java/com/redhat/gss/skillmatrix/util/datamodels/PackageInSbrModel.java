package com.redhat.gss.skillmatrix.util.datamodels;

import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.datamodels.abstracts.PackageModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

/**
 * Richfaces model for packages in specified Sbr.
 * @author jtrantin
 *
 */
public class PackageInSbrModel extends PackageModel {

	private SBR sbr;
	
	/**
	 * Constructor
	 * @param em entity manager instance
	 * @param man package manager instance
	 * @param sbr sbr
	 */
	public PackageInSbrModel(EntityManager em, PackageManager man, SBR sbr) {
		super(em, man);
	
		this.sbr = sbr;
	}

	@Override
	protected List<Predicate> generatePredicates(Root<Package> root, CriteriaBuilder cb) {
		return Arrays.asList(cb.equal(root.get("sbr"), sbr));
	}

	@Override
	protected void postProcessCriteria(CriteriaQuery<Package> criteria) {
		// not needed
		
	}
	

}
