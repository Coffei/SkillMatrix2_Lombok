package com.redhat.gss.skillmatrix.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing an SBR coach.
 * User: jtrantin
 * Date: 7/16/13
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Coach {

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    /**
     * The member that is this coach
     */
    @OneToOne
    @NotNull
    @Getter @Setter
    private Member member;

    @ManyToOne
    @Getter @Setter
    private SBR sbr;

    /**
     * SBR role of this coach
     */
    @Getter @Setter
    private String sbr_role;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private GeoEnum geocode;

    @Override
    // compares first the ids, then the members if ids are null
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coach coach = (Coach) o;

        if(id==null) {
            if(coach.id!=null)
                return false;

            if(member==null) {
                if(coach.member!=null)
                    return false;
            } else if (!member.equals(coach.member))
                return false;

        } else if (!id.equals(coach.id))
            return false;




        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? (member==null? 0 : member.hashCode()) : id.hashCode());
        return result;
    }
}
