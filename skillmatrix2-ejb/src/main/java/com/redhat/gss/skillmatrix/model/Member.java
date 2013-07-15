package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity representing a person.
 * @author jtrantin
 *
 */
@Entity
@XmlRootElement
public class Member implements Serializable {


	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 5167447177946142914L;

	

	@Id
	@GeneratedValue
    private Long id;

	@NotNull
	@Size(min = 1, max = 50)
	private String name;

	@NotNull
	@NotEmpty
	@Email
	private String email;
	
	@NotNull
	@NotEmpty
	private String nick;
	
	@Pattern(regexp="[0-9]*", message="must contain only digits")
	private String extension;
	
	private GeoEnum geo;
	
	private RoleEnum role;
	

	@OneToMany(mappedBy="member")
    private List<MemberSbr> membersbrs;

	@OneToMany(mappedBy="member")
    private List<Knowledge> knowledges;
	
	/**
	 * @return persons nick
	 */
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return persons ID
	 */
    @XmlAttribute
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return persons name
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return the geo
	 */
	public GeoEnum getGeo() {
		return geo;
	}

	/**
	 * @param geo the geo to set
	 */
	public void setGeo(GeoEnum geo) {
		this.geo = geo;
	}

	/**
	 * @return the role
	 */
	public RoleEnum getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(RoleEnum role) {
		this.role = role;
	}

	/**
	 * @return persons email
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return List of knowledges, all of which belong to this member. No other knowledges belong to this member.
	 * @see Knowledge
	 */
    @XmlTransient
	public List<Knowledge> getKnowledges() {
		return knowledges;
	}

	public void setKnowledges(List<Knowledge> knowledge) {
		this.knowledges = knowledge;
	}
	
	/**
	 * @return List of MemberSbr.
	 * @see MemberSbr
	 */
    @XmlElement(name = "sbr")
    public List<MemberSbr> getMembersbrs() {
		return membersbrs;
	}

	public void setMembersbrs(List<MemberSbr> membersbrs) {
		this.membersbrs = membersbrs;
	}

    @Override
	public String toString() {
		return String.format("Person id:%s, name:%s, nick:%s, email:%s", this.id, this.name, this.nick, this.email);
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
		Member other = (Member) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	



}