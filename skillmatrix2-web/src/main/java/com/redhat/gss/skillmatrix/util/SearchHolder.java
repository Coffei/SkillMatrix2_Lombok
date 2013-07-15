package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.controller.sorthelpers.PackageSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.util.datamodels.AllPackagesModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Holder for search. Stores values for searching over multiple requests.
 * This bean holds sorting and filtering helper with model, that can be used for 
 * data retrieval.
 * @author jtrantin
 *
 */
@Named
@ConversationScoped
public class SearchHolder implements Serializable {
	private static final long serialVersionUID = 3536981457537873230L;

    @Inject
    private Logger log;

	@Inject
	private transient FacesContext facesCtx;

	@Inject
	private transient EntityManager em;

	@Inject
	private transient PackageManager pkgMan;


	private Map<Package, Integer> packageMap;
	
	private AllPackagesModel model;
	private PackageSortingFilteringHelper pkgHelper;

	/**
	 * Returns sorting and filtering helper for Packages.
	 * @return
	 */
	public PackageSortingFilteringHelper getPkgHelper() {
		return pkgHelper;
	}

	/**
	 * Gets package-knowledge predicate map. Each entry represents predicate- member must have knowledge of certain package at certain or higher level.
	 * @return
	 */
	public Map<Package, Integer> getPackageMap() {
		return packageMap;
	}

	/**
	 * Setter for packageMap
	 * @see #getPackageMap()
	 * @param packageMap
	 */
	public void setPackageMap(Map<Package, Integer> packageMap) {
		this.packageMap = packageMap;
		
		this.model.setExcludedPackages(new ArrayList<Package>(packageMap.keySet()));
	}

	
	/**
	 * Adds package to packageMap and sets his minimum required level to 0
	 * @see #getPackageMap()
	 * @param pkg package to be added to map, cannot be null
	 * @throws NullPointerException when pkg is null
	 */
	public void addPackage(Package pkg) {
		if(pkg==null)
			throw new NullPointerException("pkg");
		FacesMessage m = new FacesMessage();
		m.setSeverity(FacesMessage.SEVERITY_INFO);

		if(!packageMap.containsKey(pkg)) {
			m.setSummary("Package " + pkg.getName() + " added!");
			packageMap.put(pkg, new Integer(0));
		} else {
			m.setSummary("Package " + pkg.getName() + " already added!");
		}

		facesCtx.addMessage(null, m);
		
		this.model.setExcludedPackages(new ArrayList<Package>(packageMap.keySet()));
	}

	/**
	 * Removes package from packageMap.
	 * @param pkg package to remove, cannot be null
	 * @throws NullPointerException when pkg is null
	 */
	public void removePackage(Package pkg){
		if(pkg==null)
			throw new NullPointerException("pkg");

		packageMap.remove(pkg);
		
		this.model.setExcludedPackages(new ArrayList<Package>(packageMap.keySet()));
	}

	//helpers
	@PostConstruct
	private void init() {
		this.packageMap = new HashMap<Package, Integer>();
		this.pkgHelper = new PackageSortingFilteringHelper();
		this.model = new AllPackagesModel(em, pkgMan);
		
		this.pkgHelper.setModel(model);

   	}


}
