package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity representing a person.
 * Relationship with {@link SBR}- {@link Member} (this entity) is the owner entity.
 * @author jtrantin
 *
 */
@Entity
@ToString(includeFieldNames=true, exclude={"unifiedId","extension","geo","role","sbrs","knowledges"})
public class Member implements Serializable {

	/**
	 * Serial ID, change with care
	 */
	private static final long serialVersionUID = 5167447177946142914L;

	
	/**
	 * Database ID
	 */
	@Id
	@GeneratedValue
	@Getter @Setter
    private Long id;

	/**
	 * Members name
	 */
	@NotNull
	@Size(min = 1, max = 50)
	@Getter @Setter
	private String name;

	/**
	 * Unified Id
	 */
    @Column(unique = true)
    @Getter @Setter
    private String unifiedId;

    /**
     * Members email
     */
	@NotNull
	@NotEmpty
	@Email
	@Getter @Setter
	private String email;
	
	/**
	 * Members nick- usually kerberos
	 */
	@NotNull
	@NotEmpty
    @Column(unique = true)
	@Getter @Setter
	private String nick;
	
	/**
	 * Members extension- phone number
	 */
	@Pattern(regexp="[0-9]*", message="must contain only digits")
	@Getter @Setter
	private String extension;

	/**
	 * The geo members located in
	 */
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @Getter @Setter
	private Geo geo;
    
    /**
     * Members role
     */
    @Getter @Setter
	private String role;
	
    /**
     * SBRs member is in.
     */
	@ManyToMany()
	@Getter @Setter
    private List<SBR> sbrs;

	/**
	 * Knowledges member has
	 */
	@OneToMany(mappedBy="member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Getter @Setter
    private List<Knowledge> knowledges;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? (nick==null? 0 : nick.hashCode()) : id.hashCode());
		return result;
	}

	@Override
    // compares the ids, or nicks if id is null.
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
            //both ids are null, lets compare some more
            if(nick==null) {
                if(other.nick != null) // if nick is null and other nick is not null
                    return false;
            } else if (!nick.equals(other.nick)) //if nicks don't match
                return false;
		} else if (!id.equals(other.id))
			return false;

		return true;
	}

	
	



}