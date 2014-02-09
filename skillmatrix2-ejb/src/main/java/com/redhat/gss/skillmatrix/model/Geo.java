package com.redhat.gss.skillmatrix.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a Geographic location. It is represented by a code of the geo (e.g. EMEA, NA/SA) and the time zone offset.
 * User: jtrantin
 * Date: 7/16/13
 * Time: 10:05 AM
 * @see GeoEnum
 */
@Entity
public class Geo implements Serializable {
    public Geo() {}
    public Geo(GeoEnum geocode, int offset) {
        this.geocode = geocode;
        this.offset = offset;
    }

    @Id
    @GeneratedValue
    @Getter @Setter
    private Long id;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private GeoEnum geocode;

    @Getter @Setter
    private int offset;



    @Override
    // compares ids, if ids are null, it compares geocode and offset
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Geo geo = (Geo) o;

        if(id==null) {
            if(geo.id!=null)
                return false;

            if(geocode==null? geo.geocode!=null : !geocode.equals(geo.geocode))
                return false;

            if(offset!=geo.offset)
                return false;

        } else if (!id.equals(geo.id))
            return false;


        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? (geocode==null? 0 : geocode.hashCode() * (offset==0? 1 : offset)) : id.hashCode());
        return result;
    }
}
