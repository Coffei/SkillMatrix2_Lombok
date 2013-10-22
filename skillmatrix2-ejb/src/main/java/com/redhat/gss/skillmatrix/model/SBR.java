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

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity representing an SBR.
 * Relationship with {@link Member}- {@link Member} is the owner entity.
 * @author jtrantin
 *
 */
@Entity
public class SBR implements Serializable {
	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 3124032505555307073L;

	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(mappedBy = "sbr", cascade = CascadeType.ALL)
    private List<Coach> coaches;

	@NotNull
	@NotEmpty
	private String name;

	@OneToMany(mappedBy="sbr")
    private List<Package> packages;
	
	@ManyToMany(mappedBy = "sbrs")
    private List<Member> members;

	/**
	 * @return ID
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return list of packages within this SBR
	 */
	public List<Package> getPackages() {
		return packages;
	}

	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}

    /**
     * @return list of coaches of this SBR
     */
    public List<Coach> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<Coach> coaches) {
        this.coaches = coaches;
    }

    /**
	 * @return name of this SBR
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    /**
     * @return list of members of this sbr
     * @see Member
     */
    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

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

	@Override
	public String toString() {
		return "SBR [id=" + id + ", name=" + name + "]";
	}
	
	

}
