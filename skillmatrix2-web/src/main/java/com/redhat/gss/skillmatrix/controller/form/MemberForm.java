package com.redhat.gss.skillmatrix.controller.form;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.model.Geo;
import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.model.Member;
import org.joda.time.Duration;
import org.joda.time.Period;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private Member member;

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
                    if(this.member.getGeo()==null) // set geo if there is none
                        this.member.setGeo(new Geo());
                    return;                       // and exit
                }

            } catch(NumberFormatException e) {} //noop, just let it run

        }

        this.member = new Member();
        this.member.setGeo(new Geo());
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
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public static class TimeZone {
        private int offset;
        private String name;

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
