package com.redhat.gss.skillmatrix.controller.util;

import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.SBR;
import org.joda.time.Duration;
import org.joda.time.Period;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/22/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class MemberUtils {

    /**
     * Returns a readable GEO string of {@code member}.
     * @param member
     * @return
     */
    public String geo(Member member) {
        if(member.getGeo()==null)
            return "";

        StringBuilder builder = new StringBuilder(member.getGeo().getGeocode().toString());
        builder.append(" / ");

        int offset = member.getGeo().getOffset();
        if(offset < 0) {
            builder.append("-");
            offset *= -1;
        }

        Period period = new Duration(offset * 60 * 1000).toPeriod();
        builder.append(String.format("%02d:%02d", period.getHours(), period.getMinutes()));

        return builder.toString();
    }

    /**
     * Returns a readable SBRs string of {@code member}.
     * @param member
     * @return
     */
    public String sbrs(Member member) {
        if(member==null)
            return "";

        StringBuilder builder = new StringBuilder();
        for(SBR sbr : member.getSbrs()) {
            builder.append(sbr.getName());
            builder.append(", ");
        }

        if(builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder.toString();
    }
}
