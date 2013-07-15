package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.data.imports.diffs.Diff;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named
@ConversationScoped
public class DiffHolder implements Serializable {
	private static final long serialVersionUID = 6655605456877046132L;

	@Inject
	private Conversation conversation;
	
	@Inject
	private Logger log;

	private List<Diff<?>> diffs;
	private int currentStep = -1;
	
	private int added;
	private int removed;
	private int deprecated;
	private int undeprecated;

	
	public Diff<?> getCurrentDiff() {
		return diffs.get(currentStep);
	}

	/**
	 * @return the diffs
	 */
	public List<Diff<?>> getDiffs() {
		return diffs;
	}
	
	/**
	 * @param diffs the diffs to set
	 */
	public void setDiffs(List<Diff<?>> diffs) {
		this.diffs = diffs;
	}

	/**
	 * @return the currentStep
	 */
	public int getCurrentStep() {
		return currentStep;
	}
	
	public void addAddedCount(int count) {
		this.added += count;
	}
	
	public void addRemovedCount(int count) {
		this.removed += count;
	}
	
	public void addDeprecatedCount(int count) {
		this.deprecated += count;
	}
	
	public void addUndeprecatedCount(int count) {
		this.undeprecated += count;
	}

	/**
	 * @return the added
	 */
	public int getAdded() {
		return added;
	}

	/**
	 * @return the removed
	 */
	public int getRemoved() {
		return removed;
	}

	/**
	 * @return the deprecated
	 */
	public int getDeprecated() {
		return deprecated;
	}

	/**
	 * @return the undeprecated
	 */
	public int getUndeprecated() {
		return undeprecated;
	}

	public void start() {
		if(conversation.isTransient()) {
			conversation.begin();
		}
	}
	
	
	public void finish() {
		if(!conversation.isTransient()) {
			conversation.end();
		}
	}

	public String next() {
		if(diffs.size() > (currentStep + 1)) {
			currentStep++;
			String type = diffs.get(currentStep).getType().getSimpleName().toLowerCase();
			log.info("next step is " + type + "diff.jsf");
			return type + "diff.jsf";
		}
		log.info("next step is importresults.jsf");
		return "importresults.jsf";
	}
	
}
