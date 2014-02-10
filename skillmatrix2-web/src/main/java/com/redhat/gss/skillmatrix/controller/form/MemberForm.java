package com.redhat.gss.skillmatrix.controller.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.joda.time.Duration;
import org.joda.time.Period;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.model.Geo;
import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.LanguageKnowledge;
import com.redhat.gss.skillmatrix.model.Member;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 9/17/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MemberForm implements Serializable {

	@Getter
	@Setter
    private Member member;

	@Getter
	@Setter
    private List<LanguageKnowledge> langs;

    @Inject
    private MemberDAO memberDAO;

    @PostConstruct
    private void init() {
        if(!memberDAO.canModify())
            return; // we cannot modify data, keep null member

        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.parseLong(sid.trim());
                List<Member> members = memberDAO.getProducerFactory().filterId(id).getMembers();
                if(!members.isEmpty()) {
                    this.member = members.get(0); // get the first (usually the only one)

                    postInitProcess();
                    return;
                    // and exit
                }

            } catch(NumberFormatException e) {} //noop, just let it run

        }

        this.member = new Member();

        postInitProcess();
    }

    private void postInitProcess() {
        if(this.member.getGeo()==null) {
            this.member.setGeo(new Geo());;
        }
        langs = new ArrayList<LanguageKnowledge>();
        langs.addAll(filterValidLangs(this.member.getKnowledges()));
        addLanguage();
    }

    public void addLanguage() {
        LanguageKnowledge lang = new LanguageKnowledge();
        lang.setMember(this.member);
        langs.add(lang);
    }

    private List<LanguageKnowledge> filterValidLangs(List<? extends Knowledge> knows) {
        if(knows==null || knows.isEmpty())
            return new ArrayList<LanguageKnowledge>(0); //better not use Collections.emptyList() due to EL bug https://bugzilla.redhat.com/show_bug.cgi?id=1029387
        List<LanguageKnowledge> validLangs = new ArrayList<LanguageKnowledge>(this.langs.size());

        for (Knowledge know : knows) {
            if (know instanceof LanguageKnowledge) {
                LanguageKnowledge lang = (LanguageKnowledge)know;

                if (lang.getLanguage() != null && !lang.getLanguage().trim().isEmpty() && lang.getLanguage().trim().length() <= 5) {
                    lang.setLanguage(lang.getLanguage().trim().toUpperCase(Locale.ENGLISH)); // trim and upper the language
                    validLangs.add(lang);
                }
            }
        }

        return validLangs;
    }

    public String submit() {
        FacesMessage msg = new FacesMessage();
        preprocessMember(this.member);
        try {
            if(this.member.getId()==null) { // we are creating new member
                memberDAO.create(member);
                msg.setSummary("Member created!");
            } else { // updating member
                memberDAO.update(member);
                msg.setSummary("Member updated!");
            }
        } catch (MemberInvalidException miex) {
            FacesMessage msg_err = new FacesMessage("Member is invalid, try again. Root cause: " + miex.getMessage());
            msg_err.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg_err);
            FacesContext.getCurrentInstance().validationFailed();
            return "";
        } catch (UnsupportedOperationException unopex) {
            FacesMessage msg_err = new FacesMessage("Operation is currently not supported.");
            msg_err.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg_err);
            FacesContext.getCurrentInstance().validationFailed();
            return "";
        }

        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //TODO: go to member detail
        return "";

    }

    public String submitKnowledges() {
        submit();

        return "knowledges?faces-redirect=true&id=" + this.member.getId();
    }

    public GeoEnum[] getAllGeocodes() {
        return GeoEnum.values();
    }

    public List<TimeZone> getAllTimezones() { // kinda misuse of Map.Entry, but seems convenient
        List<TimeZone> entries = new LinkedList<TimeZone>();
        for(int i = -690; i<=720; i+=30) { // generate all timezones by 30 minutes
            TimeZone timezone = new TimeZone();
            timezone.setOffset(i);

            Period period = new Duration(Math.abs(i * 60 * 1000)).toPeriod();
            timezone.setName((i < 0 ? "-" : "+") + String.format("%02d:%02d", period.getHours(), period.getMinutes()));
            entries.add(timezone);
        }

        return entries;
    }

    // pre-processing before saved
    private void preprocessMember(Member member) {
        if(member==null) //ignore null members
            return;
        member.setRole(member.getRole().toUpperCase(Locale.ENGLISH));
        addAllLangs(member, filterValidLangs(this.langs));
    }

    private void addAllLangs(Member member, List<LanguageKnowledge> langs) {
        if(member.getKnowledges()==null)
            member.setKnowledges(new ArrayList<Knowledge>());
        for (Iterator<Knowledge> it = member.getKnowledges().iterator(); it.hasNext(); ) {
            Knowledge know = it.next();
            if(know instanceof LanguageKnowledge) //remove all Langs
                it.remove();
        }

        member.getKnowledges().addAll(langs); //add all langs
    }

    @EqualsAndHashCode
    public static class TimeZone {
    	@Getter
    	@Setter
        private int offset;
    	
    	@Getter
    	@Setter
        private String name;
    	
    	
    }

}
