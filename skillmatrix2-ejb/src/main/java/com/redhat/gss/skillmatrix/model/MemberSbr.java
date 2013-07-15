package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.*;

/**
 * Entity representing a bond between Member and SBR. Each instance has Member and SBR connecting these two and a level- what kind of bond this is.
 * <p>E.g. level = 1 represents primary SBR bond, level = 2 represents secondary SBR, ...
 * @author jtrantin
 *
 */
@Entity
@XmlRootElement
public class MemberSbr implements Serializable {
	private static final long serialVersionUID = -9191896647756263257L;

	@Id
	private int level;
	
	@Id
	@ManyToOne(optional=false)
    @XmlTransient
	private Member member;
	
	
	@ManyToOne(optional=false)
	private SBR sbr;

	/**
	 * @return level of this bond, 1 = primary SBR, 2 = secondary SBR,...
	 */
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return member instance
	 * @see Member
	 */
    @XmlTransient
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * @return SBR instance
	 * @see SBR
	 */
    @XmlElementRef
	public SBR getSbr() {
		return sbr;
	}

	public void setSbr(SBR sbr) {
		this.sbr = sbr;
	}

	@Override
	public String toString() {
		return "MemberSbr [level=" + level + ", member=" + (member==null? "null" : member.getName()) + ", sbr="
				+ (sbr==null? "null" : sbr.getName()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + ((member == null) ? 0 : member.hashCode());
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
		MemberSbr other = (MemberSbr) obj;
		if (level != other.level)
			return false;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		return true;
	}
	
	
	
	
	
	
	
}
