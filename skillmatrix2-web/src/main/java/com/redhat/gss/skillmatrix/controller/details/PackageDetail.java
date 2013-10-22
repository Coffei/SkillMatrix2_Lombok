package com.redhat.gss.skillmatrix.controller.details;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.model.Package;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * ManagedBean for Package detail view.
 * User: jtrantin
 * Date: 9/24/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "tagDetail")
@ViewScoped()
public class PackageDetail {

    @Inject
    private Logger log;

    @Inject
    private PackageDAO dao;

    @Inject
    private MemberDAO memberDao;

    private Package pkg;

    @PostConstruct
    private void init() {
        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.parseLong(sid);

                List<Package> pkgs = dao.getProducerFactory().filterId(id).getPackages();
                if(pkgs!=null && !pkgs.isEmpty()) {
                    this.pkg = pkgs.get(0);

                    // init other stuff here
                }
            } catch (NumberFormatException e) {} //ignore if happens
        }
    }

    private Integer experts;
    public int getExperts()  {
        if(experts==null) {
            //init experts
            try {
                experts = (int)memberDao.getProducerFactory().filterKnowledgeOfPackage(this.pkg, 2).getCount();
            } catch (PackageInvalidException e) {
                log.warning("error when getting experts count");
                log.warning(e.toString());
            }
        }

        return experts;
    }

    private Integer intermediates;
    public int getIntermediates()  {
        if(intermediates==null) {
            //init experts
            try {
                intermediates = (int)memberDao.getProducerFactory().filterKnowledgeOfPackage(this.pkg, 1).getCount();
            } catch (PackageInvalidException e) {
                log.warning("error when getting intermediates count");
                log.warning(e.toString());
            }
        }

        return intermediates;
    }

    private Integer beginners;
    public int getBeginners()  {
        if(beginners==null) {
            //init experts
            try {
                beginners = (int)memberDao.getProducerFactory().filterKnowledgeOfPackage(this.pkg, 0).getCount();
            } catch (PackageInvalidException e) {
                log.warning("error when getting beginners count");
                log.warning(e.toString());
            }
        }

        return beginners;
    }




    public Package getPkg() {
        return pkg;
    }

    public void setPkg(Package pkg) {
        this.pkg = pkg;
    }
}