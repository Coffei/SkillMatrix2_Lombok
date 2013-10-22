package com.redhat.gss.skillmatrix.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    private Long id;

    @OneToOne
    @NotNull
    private Member member;

    @ManyToOne()
    private SBR sbr;

    private String sbr_role;

    @Enumerated(EnumType.STRING)
    private GeoEnum geocode;

    /**
     * @return the member that is the coach
     */
    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * The sbr role of this coach
     * @return
     */
    public String getSbr_role() {
        return sbr_role;
    }

    public void setSbr_role(String sbr_role) {
        this.sbr_role = sbr_role;
    }

    public SBR getSbr() {
        return sbr;
    }

    public void setSbr(SBR sbr) {
        this.sbr = sbr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GeoEnum getGeocode() {
        return geocode;
    }

    public void setGeocode(GeoEnum geocode) {
        this.geocode = geocode;
    }

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
