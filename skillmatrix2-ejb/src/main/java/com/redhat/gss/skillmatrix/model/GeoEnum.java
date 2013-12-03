package com.redhat.gss.skillmatrix.model;

import java.util.Locale;

public enum GeoEnum {
	EMEA, NA, APAC, Pune, India, UNKNOWN, LATAM;

    public static GeoEnum parseGeo(String geo) {
        for(GeoEnum geoEnum : GeoEnum.values()) {
            if(geoEnum.toString().toUpperCase(Locale.ENGLISH).equals(geo.trim().toUpperCase(Locale.ENGLISH))) {
                return geoEnum;
            }
        }

        return null;
    }
}
