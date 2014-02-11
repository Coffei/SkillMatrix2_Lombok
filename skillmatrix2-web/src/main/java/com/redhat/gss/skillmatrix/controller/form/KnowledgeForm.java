package com.redhat.gss.skillmatrix.controller.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.PackageModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageKnowledgeDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.model.Knowledge;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.PackageKnowledge;

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

    @Getter
    @Setter
    private Member member;

    @Getter
    private PackageModelHelper pkgModelHelper;
    
    @Getter
    private Map<Package, Integer> knowledges;


    @PostConstruct
    private void init() {
        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id"); // get id param
        if(sid!=null) { // there is some ID param
            try {
                long id = Long.parseLong(sid); // try parse
                val members = memberDAO.getProducerFactory().filterId(id).getMembers();
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
                return pkgDAO.getProducerFactory();
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

        val msg = new FacesMessage("Knowledges updated!");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        //TODO: redirect to member detail
        return "";

    }

}
