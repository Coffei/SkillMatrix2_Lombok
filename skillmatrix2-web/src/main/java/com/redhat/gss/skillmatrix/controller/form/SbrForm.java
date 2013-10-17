package com.redhat.gss.skillmatrix.controller.form;

import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.model.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/1/13
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SbrForm implements Serializable {

    @Inject
    private SbrDAO sbrDAO;

    @Inject
    private MemberDAO memberDAO;

    @Inject
    private PackageDAO packageDAO;

    private SBR sbr;

    @PostConstruct
    private void init() {
        if(!sbrDAO.canModify()) {
            return; // leave pkg null to show we can not modify data
        }

        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.valueOf(sid);
                List<SBR> sbrs = sbrDAO.getProducerFactory().filterId(id).getSbrs();

                if(!sbrs.isEmpty()) { // just take the first one
                    this.sbr = sbrs.get(0); // edit existing package mode
                    return;
                }
            } catch (NumberFormatException ex) {} //Exs should not be used for flow control
        }

        // when something fails, create new Package - add new package mode
        this.sbr = new SBR();
    }

    public String submit() {
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        try{
            if(sbr.getId()==null) {
                sbrDAO.create(sbr);
                msg.setSummary("New SBR created!");
            } else {
                sbrDAO.update(sbr);
                msg.setSummary("SBR updated!");
            }
        } catch (SbrInvalidException e) {
            FacesMessage errMsg = new FacesMessage("SBR is invalid, try again. Root cause: " + e.getMessage());
            errMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, errMsg);
            FacesContext.getCurrentInstance().validationFailed();
            return "";
        } catch(UnsupportedOperationException e) {
            FacesMessage errMsg = new FacesMessage("Operation is currently not supported.");
            errMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, errMsg);
            FacesContext.getCurrentInstance().validationFailed();
            return "";
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //TODO: redirect to some other page
        return "";
    }


    public SBR getSbr() {
        return sbr;
    }

    public void setSbr(SBR sbr) {
        this.sbr = sbr;
    }
}
