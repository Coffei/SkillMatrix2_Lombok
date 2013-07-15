package com.redhat.gss.skillmatrix.controller;

import com.redhat.gss.skillmatrix.controller.sorthelpers.PackageSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.PackageKnowledgeManager;
import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.datamodels.AllPackagesModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Bean controller for package form (and other package-related pages).
 * This bean holds sorting and filtering helper with model, that can
 * be used for data retrieval.
 * 
 * @author jtrantin
 *
 */
@ViewScoped
@ManagedBean
public class PackageController implements Serializable {
	private static final long serialVersionUID = -2209006022581218357L;

	@Inject
	private transient EntityManager em;

	@Inject 
	private transient PackageManager packageMan;

	@Inject
	private transient PackageKnowledgeManager pkgKnowMan;

	@Inject
	private transient SbrManager sbrMan;

	@Inject
	private transient FacesContext facesCtx;

	private PackageSortingFilteringHelper sortHelper;

	private Long sbrID;

	private Package pkg;

	private Map<Package, Long> experts;


	/**
	 * Returns sorting and filtering helper.
	 * @return
	 */
	public PackageSortingFilteringHelper getSortHelper() {
		return sortHelper;
	}

	/**
	 * Returns package that is being edited or created.
	 * @return instance of Package
	 */
	public Package getPkg() {
		return pkg;
	}

	/**
	 * Returns a number of packages experts.
	 * @param pkg
	 * @return
	 */
	public Long getExperts(Package pkg) {
		if(pkg==null) 
			return 0L;

		if(experts.containsKey(pkg)) {
			return experts.get(pkg);

		} else {
			Long value = pkgKnowMan.getCountMembersByLevelOfKnowledge(pkg, 2);
			experts.put(pkg, value);
			return value;
		}
	}


	/**
	 * Setter for 
	 * @param pkg
	 * @see #getPkg()
	 */
	public void setPkg(Package pkg) {
		if(pkg==null) {
			this.pkg = new Package();
		} else {
			this.pkg = pkg;
			if(pkg.getSbr()!=null) {
				sbrID = pkg.getSbr().getId();
			} else {
				sbrID = null;
			}
		}
	}

	/**
	 * Get SBR of package in Long
	 * @return id of SBR set as a packages SBR.
	 */
	public Long getSbrID() {
		return sbrID;
	}

	/**
	 * @see #getSbrID()
	 * @param id
	 */
	public void setSbrID(Long id) {
		this.sbrID = id;
	}

	/** 
	 * @return true if existing package is being edited, false if new one is being created.
	 */
	public boolean isEditing() {
		return pkg.getId()!=null;
	}

	//actions
	/**
	 * Creates or updates package with current information. Intended to be used as action method.
	 * @return route String
	 */
	public String submitPackage() {
		//fetch
		Package tosave = null;
		if(pkg.getId()==null) {
			tosave= new Package();
		} else {
			tosave = packageMan.getPkgById(pkg.getId());
		}

		//not found
		if(tosave==null) {
			tosave = new Package();
		}

		//copy
		tosave.setName(pkg.getName());
		if(sbrID != -1) {
			SBR sbr = sbrMan.getSbrById(sbrID);


			if(!sbr.equals(tosave.getSbr())) {
				if(tosave.getSbr()!=null ) { //remove from old sbr
					SBR oldsbr = sbrMan.getSbrById(tosave.getSbr().getId());
					oldsbr.getPackages().remove(tosave);
					sbrMan.update(oldsbr);
				}
				sbr.getPackages().add(tosave); //add to new sbr
				sbrMan.update(sbr);
			}

			if(sbr!=null) {
				tosave.setSbr(sbr);
			}
		} else {
			if(tosave.getSbr()!=null ) { //remove from old sbr
				SBR oldsbr = sbrMan.getSbrById(tosave.getSbr().getId());
				oldsbr.getPackages().remove(tosave);
				sbrMan.update(oldsbr);
				tosave.setSbr(null);
			}
		}

		//save
		FacesMessage m;
		if(tosave.getId()==null) {
			packageMan.create(tosave);
			setPkg(new Package());
			m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Package created!", "Package " + tosave.getName() + " successfully created.");
		} else {
			packageMan.update(tosave);
			m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Package updated!", "Package " + tosave.getName() + " successfully updated.");
		}



		facesCtx.addMessage(null, m);


		return null;
	}


	/**
	 * Starts package deleting procedure. Intended to be used as action method.
	 * @param pkg package to be deleted.
	 * @return route String
	 */
	public String deletePackage(Package pkg) {
		packageMan.delete(pkg);

		return null;
	}


	//helpers
	@PostConstruct
	private void init() {
		String strid = facesCtx.getExternalContext().getRequestParameterMap().get("editid");
		if(strid!=null && !strid.isEmpty()) {
			try {
				long id = Long.parseLong(strid);
				setPkg(packageMan.getPkgById(id));
			} catch (NumberFormatException e) {
				this.pkg = new  Package();
			}
		} else {
			this.pkg = new Package();
		}

		sortHelper = new PackageSortingFilteringHelper();
		sortHelper.setModel(new AllPackagesModel(em, packageMan));
		experts = new HashMap<Package, Long>();
	}

}
