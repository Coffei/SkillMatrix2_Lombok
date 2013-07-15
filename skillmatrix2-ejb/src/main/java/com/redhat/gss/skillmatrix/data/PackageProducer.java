package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.Package;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Producer of Packages
 * @author jtrantin
 *
 */
@RequestScoped
public class PackageProducer {
	
	@Inject
	private EntityManager em;
	
	@Inject
	private PackageManager pkgMan;
	
	private List<Package> pkgs;
	
	/**
	 * @return all packages ordered by name
	 */
	@Named
	@Produces
	public List<Package> getPackages() {
		return pkgs;
	}
	
	/**
	 * Observer of packages
	 * @param pkg
	 */
	public void onMemberListChanged(@Observes final Package pkg) {
		retrieveAllPackagesOrderedByName();
	}

	@PostConstruct
	private void retrieveAllPackagesOrderedByName() {
		pkgs = pkgMan.getAllPackages();
	}
	
	

}
