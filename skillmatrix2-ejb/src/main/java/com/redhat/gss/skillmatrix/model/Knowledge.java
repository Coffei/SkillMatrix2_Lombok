package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity, representing some knowledge of member. Good for extending.
 * @author jtrantin
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "KNOWLEDGE_TYPE")
public class Knowledge implements Serializable {

	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 7986161260600024439L;
	
	@Id
	@GeneratedValue
	@Getter @Setter
	private Long id;

	/**
	 * Level of knowledge as Integer value
	 */
	@Getter @Setter
	private Integer level;

	/**
	 * Owner of this knowledge
	 */
	@NotNull
	@ManyToOne(optional=false, fetch = FetchType.EAGER)
	@Getter @Setter
	private Member member;
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Knowledge other = (Knowledge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
