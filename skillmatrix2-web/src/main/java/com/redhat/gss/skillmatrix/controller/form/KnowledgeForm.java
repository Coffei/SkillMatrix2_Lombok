package com.redhat.gss.skillmatrix.controller.form;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.PackageModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageKnowledgeDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 9/26/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class KnowledgeForm implements Serializable {
    private static final int MAX_RECORDS_PER_PAGE = 20;

    @Inject
    private MemberDAO memberDAO;

    @Inject
    private PackageDAO pkgDAO;

    @Inject
    private PackageKnowledgeDAO pkgKnowDAO;

    private Member member;


    private PackageModelHelper pkgModelHelper;

    private Map<Package, Integer> knowledges;


    @PostConstruct
    private void init() {
        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id"); // get id param
        if(sid!=null) { // there is some ID param
            try {
                long id = Long.parseLong(sid); // try parse
                List<Member> members = memberDAO.getProducerFactory().filterId(id).getMembers();
                if(!members.isEmpty()) { // some members were found
                    this.member = members.get(0); //just get the first member
                }

            } catch (NumberFormatException e) {} //if parsing fails, ignore and go on
        }

        //fill in knowledges map
        this.knowledges = new HashMap<Package, Integer>();
        for(Knowledge know : member.getKnowledges()) {
            if(know instanceof PackageKnowledge) {
                knowledges.put(((PackageKnowledge)know).getPackage(), know.getLevel());
            }
        }

        this.pkgModelHelper = new PackageModelHelper(MAX_RECORDS_PER_PAGE) { // we create new package model helper
            @Override
            protected PackageProducer getProducer() {
                return pkgDAO.getPackageProducer();
            }
        };
    }

    public String submit() {
        try {
            pkgKnowDAO.update(knowledges, member);
        } catch (MemberInvalidException e) {
            FacesMessage msg = new FacesMessage("Member is invalid. Root cause: " + e.getMessage());
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            FacesContext.getCurrentInstance().validationFailed();
            return null;
        } catch (UnsupportedOperationException e) {
            FacesMessage msg = new FacesMessage("Operation is not supported right now.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            FacesContext.getCurrentInstance().validationFailed();
            return null;
        }

        FacesMessage msg = new FacesMessage("Knowledges updated!");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        //TODO: redirect to member detail
        return "";

    }


    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }


    public PackageModelHelper getPkgModelHelper() {
        return pkgModelHelper;
    }

    public Map<Package, Integer> getKnowledges() {
        return knowledges;
    }
}
