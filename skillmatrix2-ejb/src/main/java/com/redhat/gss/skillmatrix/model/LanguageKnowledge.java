package com.redhat.gss.skillmatrix.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("LANGUAGE")
public class LanguageKnowledge extends Knowledge {
	private static final long serialVersionUID = 821365656768507160L;
	
	/**
	 * Language of this knowledge
	 */
	@NotNull
	@Getter @Setter
	private String language;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanguageKnowledge that = (LanguageKnowledge) o;

        if(getId()==null) {
            if(that.getId()!=null)
                return false;
            //compare other fields
            if(language==null? that.language!=null : !language.equals(that.language))
                return false;

            if(getLevel()==null? that.getLevel()!=null : !getLevel().equals(that.getLevel()))
                return false;

        } else if (!getId().equals(that.getId()))
            return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = 31 * (getId() != null ? getId().hashCode() : 1);
        if(getId()==null) {
            result += 37 * (language==null ? 1 : language.hashCode());
            result += 29 * (getLevel()==null? 1 : getLevel().hashCode());
        }
        return result;
    }
}
