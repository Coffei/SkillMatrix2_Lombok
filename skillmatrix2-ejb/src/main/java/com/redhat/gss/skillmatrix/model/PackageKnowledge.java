package com.redhat.gss.skillmatrix.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Entity representing members knowledge (skill) of certain package.
 * @author jtrantin
 *
 */
@Entity
@DiscriminatorValue("PACKAGE")
@ToString(includeFieldNames=true, callSuper=true)
@Accessors(prefix = "a")
public class PackageKnowledge extends Knowledge {

	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 8718176125991666863L;

	/**
	 * Package this knowledge refers to
	 */
	@NotNull
	@ManyToOne(optional=false)
        @Getter @Setter
	private Package aPackage;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		PackageKnowledge other = (PackageKnowledge) obj;
		return this.getId().equals(other.getId());
	}


	
}
