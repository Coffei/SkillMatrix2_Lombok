package com.redhat.gss.skillmatrix.data.imports.diffs;

import java.util.List;

public class Diff<T> {
	
	
	private List<T> added;
	private List<T> deprecated;
	private List<T> undeprecated;
	
	
	/**
	 * @return the added
	 */
	public List<T> getAdded() {
		return added;
	}
	/**
	 * @param added the added to set
	 */
	public void setAdded(List<T> added) {
		this.added = added;
	}
	/**
	 * @return the deprecated
	 */
	public List<T> getDeprecated() {
		return deprecated;
	}
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(List<T> deprecated) {
		this.deprecated = deprecated;
	}
	/**
	 * @return the undeprecated
	 */
	public List<T> getUndeprecated() {
		return undeprecated;
	}
	/**
	 * @param undeprecated the undeprecated to set
	 */
	public void setUndeprecated(List<T> undeprecated) {
		this.undeprecated = undeprecated;
	}
	
	/**
	 * 
	 * @return the class type parameter, may not be accurate!
	 * 
	 */
	public Class<?> getType() { // returns T
		if(!added.isEmpty()) {
			return added.get(0).getClass();
		} else if (!deprecated.isEmpty()) {
			return deprecated.get(0).getClass();
		} else if (!undeprecated.isEmpty()) {
			return undeprecated.get(0).getClass();
		}
		
		return getClass();
		
	}
	
	public boolean isEmpty() {
		return (added == null || added.isEmpty()) && (deprecated==null || deprecated.isEmpty()) && (undeprecated==null || undeprecated.isEmpty());
	}
	
	@Override
	public String toString() {
		return String.format("Diff- %s added and %s deprecated and %s undeprecated.", added.size(), deprecated.size(), undeprecated.size());
	}
	
	
}
