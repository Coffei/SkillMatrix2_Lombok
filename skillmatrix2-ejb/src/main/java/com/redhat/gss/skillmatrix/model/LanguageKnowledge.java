package com.redhat.gss.skillmatrix.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("LANGUAGE")
public class LanguageKnowledge extends Knowledge {
	private static final long serialVersionUID = 821365656768507160L;
	
	@NotNull
	private String language;


	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}


	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	

}
