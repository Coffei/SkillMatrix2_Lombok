package com.redhat.gss.skillmatrix.data;

import com.redhat.gss.skillmatrix.model.SBR;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Producer of SBRs
 * @author jtrantin
 *
 */
@RequestScoped
public class SbrProducer {
	
	@Inject
	private SbrManager manager;
	
	@Produces
	@Named
	private List<SBR> SBRs;

	/**
	 * @return all SBRs ordered by their name
	 */
	public List<SBR> getSBRs() {
		return SBRs;
	}
	
	@PostConstruct
	private void loadSbrsOrderedByName() {
		this.SBRs = manager.getAllSbrsSortedByName();
	}
	
	/**
	 * Observer of SBR
	 * @param sbr
	 */
	public void onSbrListChanged(@Observes final SBR sbr) {
		loadSbrsOrderedByName();
	}
	

}
