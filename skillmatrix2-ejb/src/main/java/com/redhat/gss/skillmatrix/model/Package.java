package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity representing package
 * @author jtrantin
 *
 */
@Entity
public class Package implements Serializable {
	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = -7877611721457332617L;

	@Id
	@GeneratedValue
	private Long id;


	private boolean deprecated;

	@NotNull
	@NotEmpty
	private String name;

	@ManyToOne(optional=true)
	@JoinColumn(nullable=true)
	private SBR sbr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * @return packages name
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return packages SBR
	 * @see SBR
	 */
	public SBR getSbr() {
		return sbr;
	}

	public void setSbr(SBR sbr) {
		this.sbr = sbr;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int id = this.id==null? (name==null? 0 : name.hashCode()*19) : this.id.hashCode()*7;
		result = prime * result + id;
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
		Package other = (Package) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			} else {
				if(name==null) {
					if(other.name!=null)
						return false;
				} else if (!name.equals(other.name)) {
					return false;
				}
			}
		} else if (!id.equals(other.id))
			return false;


		return true;
	}

	@Override
	public String toString() {
		return String.format("Package [id=%s, name=%s]", id==null? "null" : id.toString(), name);
	}

}
