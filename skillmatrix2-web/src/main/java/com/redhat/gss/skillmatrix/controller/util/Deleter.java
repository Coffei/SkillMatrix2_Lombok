package com.redhat.gss.skillmatrix.controller.util;

import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 9/24/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class Deleter {

    @Inject
    private MemberDAO memberDAO;

    @Inject
    private SbrDAO sbrDAO;

    @Inject
    private PackageDAO packageDAO;


    public String deleteMember(Member member) {
        if(member==null) //ignore wrong inputs
            return null;

        if(memberDAO.canModify()) {
            try {
                memberDAO.delete(member);
                handleMessage(FacesMessage.SEVERITY_INFO, "Member deleted!");
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true); //keep messages over redirect

            } catch (MemberInvalidException e) {
                handleMessage(FacesMessage.SEVERITY_ERROR, "Member is invalid, try again. Root cause: " + e.getMessage());
                return null;
            } catch (UnsupportedOperationException  e) {
                handleMessage(FacesMessage.SEVERITY_ERROR, "Operation is currently not supported.");
                return null;
            }
        } else {
            handleMessage(FacesMessage.SEVERITY_ERROR, "Operation is currently not supported.");
            return null;
        }

        //nav rule
        return "member?faces-redirect=true";
    }

    public String deletePackage(Package pkg) {
        if(pkg==null) //ignore wrong inputs
            return null;

        if(packageDAO.canModify()) {
            try {
                packageDAO.delete(pkg);
                handleMessage(FacesMessage.SEVERITY_INFO, "Tag deleted!");
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true); //keep messages over redirect

            } catch (PackageInvalidException e) {
                handleMessage(FacesMessage.SEVERITY_ERROR, "Tag is invalid, try again. Root cause: " + e.getMessage());
                return null;
            } catch (UnsupportedOperationException e) {
                handleMessage(FacesMessage.SEVERITY_ERROR, "Operation is currently not supported.");
                return null;
            }
        } else {
            handleMessage(FacesMessage.SEVERITY_ERROR, "Operation is currently not supported.");
            return null;
        }

        //nav rule
        return "tags?faces-redirect=true";
    }

    public String deleteSbr(SBR sbr) {
        if(sbr==null) //ignore wrong inputs
            return null;

        if(sbrDAO.canModify()) {
            try {
                sbrDAO.delete(sbr);
                handleMessage(FacesMessage.SEVERITY_INFO, "SBR deleted!");
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true); //keep messages over redirect

            } catch (SbrInvalidException e) {
                handleMessage(FacesMessage.SEVERITY_ERROR, "SBR is invalid, try again. Root cause: " + e.getMessage());
                return null;
            } catch (UnsupportedOperationException e) {
                handleMessage(FacesMessage.SEVERITY_ERROR, "Operation is currently not supported.");
                return null;
            }
        } else {
            handleMessage(FacesMessage.SEVERITY_ERROR, "Operation is currently not supported.");
            return null;
        }

        //nav rule
        return "sbrs?faces-redirect=true";
    }



    private void handleMessage(FacesMessage.Severity severity, String messageText) {
        FacesMessage msg = new FacesMessage(messageText);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
