package com.redhat.gss.skillmatrix.controller.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.val;

import org.joda.time.Duration;
import org.joda.time.Period;

import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.SBR;

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

        val builder = new StringBuilder(member.getGeo().getGeocode().toString());
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

        val builder = new StringBuilder();
        for(SBR sbr : member.getSbrs()) {
            builder.append(sbr.getName());
            builder.append(", ");
        }

        if(builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder.toString();
    }

    private Member lastMember;
    private String lastLangs;
    public String langs(Member member) {
        if(lastMember!=null && lastMember.equals(member))//simple caching mechanism
            return lastLangs;

        val langs = getLangsList(member);
        if(langs.isEmpty()) {
            lastMember = member;
            lastLangs = null;
            return null;
        }

        val builder = new StringBuilder(langs.get(0));
        for(String lang : langs.subList(1, langs.size())) {
            builder.append(", ");
            builder.append(lang);
        }

        lastMember = member;
        lastLangs = builder.toString();
        return builder.toString();
    }

    private List<String> getLangsList(Member member) {
        if(member==null || member.getKnowledges()==null || member.getKnowledges().isEmpty())
            return Collections.emptyList();

       val langs = new ArrayList<String>();
        for(Knowledge know : member.getKnowledges()) {
            if(know instanceof LanguageKnowledge)
                langs.add(((LanguageKnowledge)know).getLanguage());
        }

        Collections.sort(langs);
        return langs;
    }

}
