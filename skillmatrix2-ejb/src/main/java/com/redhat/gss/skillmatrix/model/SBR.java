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
 * Entity representing SBR.
 * @author jtrantin
 *
 */
@Entity
@XmlRootElement
public class SBR implements Serializable {
	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 3124032505555307073L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional=true)
    private Member coach;

	@NotNull
	@NotEmpty
	private String name;

	@OneToMany(mappedBy="sbr")
    private List<Package> packages;
	
	@OneToMany(mappedBy="sbr")
    private List<MemberSbr> membersbrs;

	/**
	 * @return ID
	 */
    @XmlAttribute
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return list of packages within this SBR
	 */
    @XmlTransient
	public List<Package> getPackages() {
		return packages;
	}

	public void setPackages(List<Package> packages) {
		this.packages = packages;
	}
	
	/**
	 * @return coach of this SBR or null if no one is assigned.
	 */
    @XmlTransient
	public Member getCoach() {
		return coach;
	}

	public void setCoach(Member coach) {
		this.coach = coach;
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
	 * @return list of MemberSbr.
	 * @see MemberSbr
	 */
    @XmlTransient
	public List<MemberSbr> getMembersbrs() {
		return membersbrs;
	}

	public void setMembersbrs(List<MemberSbr> membersbrs) {
		this.membersbrs = membersbrs;
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
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SBR [id=" + id + ", name=" + name + "]";
	}
	
	

}
