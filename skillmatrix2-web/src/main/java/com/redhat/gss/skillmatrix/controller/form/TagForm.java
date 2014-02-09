package com.redhat.gss.skillmatrix.controller.form;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.model.Package;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 9/16/13
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class TagForm  implements Serializable {

	@Getter
	@Setter
    private Package pkg;

    @Inject
    private PackageDAO pkgDao;

    @PostConstruct
    private void init() {
       if(!pkgDao.canModify()) {
            return; // leave pkg null to show we can not modify data
        }

        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");

        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.valueOf(sid);
                List<Package> pkgs = pkgDao.getProducerFactory().filterId(id).getPackages();

                if(!pkgs.isEmpty()) { // just take the first one
                    this.pkg = pkgs.get(0); // edit existing package mode
                    return;
                }
            } catch (NumberFormatException ex) {} //Exs should not be used for flow control
        }

        // when something fails, create new Package - add new package mode
        this.pkg = new Package();
    }

    public String submit(){
        FacesMessage msg = new FacesMessage();
        try {
            if(pkg.getId() == null) {// we are creating new package
                pkgDao.create(pkg);
                msg.setSummary("Tag created!");
            } else {
                pkgDao.update(pkg);
                msg.setSummary("Tag updated!");
            }
        } catch(PackageInvalidException piex)  {

            FacesMessage msg_err = new FacesMessage("Package is invalid, try again. Root cause: " + piex.getMessage());
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

        //TODO: go to tag detail
        return "";
    }
}
