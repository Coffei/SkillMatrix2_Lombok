package com.redhat.gss.skillmatrix.controller;

import com.redhat.gss.skillmatrix.data.PackageManager;
import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.data.imports.diffs.Diff;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.DiffHolder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class PackageDiffController implements Serializable {
	private static final long serialVersionUID = 6864761901734332552L;

	@Inject
	private DiffHolder holder;

	@Inject
	private FacesContext facesContext;

	@Inject
	private PackageManager packageManager;

	@Inject
	private SbrManager sbrManager;

	@Inject
	private Logger log;

	private Diff<? extends Package> diff;
	private Map<Package, Long> sbrs;
	private Map<Package, Boolean> actions;
	private Map<Package, Boolean> deletes;


	/**
	 * @return the deletes
	 */
	public Map<Package, Boolean> getDeletes() {
		return deletes;
	}

	/**
	 * @return the actions
	 */
	public Map<Package, Boolean> getActions() {
		return actions;
	}

	/**
	 * @return the sbrs
	 */
	public Map<Package, Long> getSbrs() {
		return sbrs;
	}

	/**
	 * @return the diff
	 */
	public Diff<? extends Package> getDiff() {
		return diff;
	}

	/**
	 * @param diff the Diff instance to set
	 */
	public void setDiff(Diff<? extends Package> diff) {
		this.diff = diff;
	}

	public int getCurrentStep() {
		return holder.getCurrentStep() + 1;
	}

	public int getSteps() {
		return holder.getDiffs().size();
	}

	public String submit() {
		int added = 0;
		int removed = 0;
		int deprecated =  0;
		int undeprecated = 0;

		for (Package pkg : diff.getAdded()) {
			log.info("add " + pkg.getName());
			log.info("   " + actions);
			Boolean add = actions.get(pkg);
			if(add!=null && add.booleanValue()) {
				log.info("trying to add " + pkg.getName());

				//set SBR
				Long sbrid = sbrs.get(pkg);
				if(sbrid!= null) {
					SBR sbr = sbrManager.getSbrById(sbrid);
					if(sbr!=null) {
						pkg.setSbr(sbr);
					}
				}

				//create package
				packageManager.create(pkg);
				added++;
			}
		}

		for (Package pkg : diff.getDeprecated()) {
			Boolean delete = deletes.get(pkg);
			Boolean deprecate = actions.get(pkg);
			if(delete!=null && delete.booleanValue()) {// should be deleted
				log.info("trying to delete " + pkg.getName());

				packageManager.delete(pkg);
				removed++;
			} else if (deprecate!=null && deprecate.booleanValue()) { // should be deprecated
				log.info("trying to deprecate " + pkg.getName());

				pkg.setDeprecated(true);
				packageManager.update(pkg);
				deprecated++;
			}
		}

		for (Package pkg : diff.getUndeprecated()) {
			Boolean undeprecate = actions.get(pkg);
			if(undeprecate != null && undeprecate.booleanValue()) {
				log.info("trying to undeprecate " + pkg.getName());

				pkg.setDeprecated(false);
				packageManager.update(pkg);
				undeprecated++;
			}

		}

		holder.addAddedCount(added);
		holder.addDeprecatedCount(deprecated);
		holder.addRemovedCount(removed);
		holder.addUndeprecatedCount(undeprecated);

		return holder.next();


	}

	public void reverseAdd() {
		for (Package pkg : diff.getAdded()) {
			Boolean old = actions.get(pkg);
			if(old==null)
				old = false;

			actions.put(pkg, !old);
		}
	}

	public void reverseDeprecate() {
		for (Package pkg : diff.getDeprecated()) {
			Boolean old = actions.get(pkg);
			if(old==null)
				old = false;

			actions.put(pkg, !old);
		}
	}

	public void reverseDelete()  {
		for (Package pkg : diff.getDeprecated()) {
			Boolean old = deletes.get(pkg);
			if(old==null)
				old = false;

			deletes.put(pkg, !old);
		}
	}

	public void reverseUndeprecate() {
		for (Package pkg : diff.getUndeprecated()) {
			Boolean old = actions.get(pkg);
			if(old==null)
				old = false;

			actions.put(pkg, !old);
		}
	}


	@PostConstruct
	private void init() {
		diff = (Diff<? extends Package>)holder.getCurrentDiff();
		sbrs = new HashMap<Package, Long>();
		actions = new HashMap<Package, Boolean>();
		deletes = new HashMap<Package, Boolean>();

		//fill in actions
		for (Package pkg : diff.getAdded()) {
			actions.put(pkg, true);			
		}

		for (Package pkg : diff.getDeprecated()) {
			actions.put(pkg, true);			
		}

		for (Package pkg : diff.getUndeprecated()) {
			actions.put(pkg, true);			
		}
	}

}
