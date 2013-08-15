package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity representing a person.
 * Relationship with {@link SBR}- {@link Member} (this entity) is the owner entity.
 * @author jtrantin
 *
 */
@Entity
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

    @Column(unique = true)
    private String unifiedId;

	@NotNull
	@NotEmpty
	@Email
	private String email;
	
	@NotNull
	@NotEmpty
    @Column(unique = true)
	private String nick;
	
	@Pattern(regexp="[0-9]*", message="must contain only digits")
	private String extension;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
	private Geo geo;
	
	private String role;
	

	@ManyToMany()
    private List<SBR> sbrs;

	@OneToMany(mappedBy="member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
     * @return the geo the member is based in
     * @see Geo
     */
    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    /**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
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
	public List<Knowledge> getKnowledges() {
		return knowledges;
	}

	public void setKnowledges(List<Knowledge> knowledge) {
		this.knowledges = knowledge;
	}

    /**
     * @return list of sbrs the member is joined in.
     * @see SBR
     */
    public List<SBR> getSbrs() {
        return sbrs;
    }

    public void setSbrs(List<SBR> sbrs) {
        this.sbrs = sbrs;
    }

    public String getUnifiedId() {
        return unifiedId;
    }

    public void setUnifiedId(String unifiedId) {
        this.unifiedId = unifiedId;
    }

    @Override
	public String toString() {
		return String.format("Person id:%s, name:%s, nick:%s, email:%s", this.id, this.name, this.nick, this.email);
	}

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