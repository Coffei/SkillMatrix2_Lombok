package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
	private Long id;

	private Integer level;

	@NotNull
	@ManyToOne(optional=false, fetch = FetchType.EAGER)
	private Member member;
	
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
	 * @return level of knowledge as Integer value
	 */
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return Member whose knowledge this is.
	 */
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
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
		Knowledge other = (Knowledge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	


}
