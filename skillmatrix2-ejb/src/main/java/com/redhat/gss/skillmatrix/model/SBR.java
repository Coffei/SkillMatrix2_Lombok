package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity representing an SBR.
 * Relationship with {@link Member}- {@link Member} is the owner entity.
 * @author jtrantin
 *
 */
@Entity
@ToString(includeFieldNames=true, exclude={"coaches", "packages", "members"})
public class SBR implements Serializable {
	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 3124032505555307073L;

	/**
	 * Database ID
	 */
	@Id
	@GeneratedValue
	@Getter @Setter
	private Long id;

	/**
	 * List of coaches of this SBR
	 */
	@OneToMany(mappedBy = "sbr", cascade = CascadeType.ALL)
	@Getter @Setter
    private List<Coach> coaches;

	/**
	 * Name of this SBR
	 */
	@NotNull
	@NotEmpty
	@Getter @Setter
	private String name;

	/**
	 * List of packages within this SBR
	 */
	@OneToMany(mappedBy="sbr")
	@Getter @Setter
    private List<Package> packages;
	
	/**
	 * List of members of this SBR
	 */
	@ManyToMany(mappedBy = "sbrs")
	@Getter @Setter
    private List<Member> members;


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
		SBR other = (SBR) obj;
		if (id == null) {
			if (other.id != null)
				return false;

            if(name==null? other.name!=null : !name.equals(other.name))
                return false;

		} else if (!id.equals(other.id))
			return false;


		return true;
	}

}
