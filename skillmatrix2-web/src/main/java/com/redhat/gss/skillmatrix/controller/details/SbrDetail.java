package com.redhat.gss.skillmatrix.controller.details;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.PackageModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/22/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SbrDetail {
    private static final int RECORDS_PER_PAGE = 20;

    @Inject
    private SbrDAO dao;

    @Inject
    private MemberDAO memberDao;

    @Inject
    private PackageDAO pkgDao;

    private SBR sbr;

    private PackageModelHelper packageModel;

    @PostConstruct
    private void init() {
        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.parseLong(sid);

                List<SBR> sbrs = dao.getProducerFactory().filterId(id).getSbrs();
                if(sbrs!=null && !sbrs.isEmpty()) {
                    this.sbr = sbrs.get(0);
                    Collections.sort(this.sbr.getCoaches(), new Comparator<Coach>() { //pre sort coaches
                        @Override
                        public int compare(Coach coach, Coach coach2) {
                            return coach.getGeocode().compareTo(coach2.getGeocode());
                        }
                    });

                    packageModel = new PackageModelHelper(RECORDS_PER_PAGE) {
                        @Override
                        protected PackageProducer getProducer() {
                            try {
                                return pkgDao.getProducerFactory().filterSBR(sbr);
                            } catch (SbrInvalidException e) {
                                return null; //should never happen
                            }
                        }
                    };
                }
            } catch (NumberFormatException e) {} //ignore if happens
        }
    }



    public SBR getSbr() {
        return sbr;
    }

    public void setSbr(SBR sbr) {
        this.sbr = sbr;
    }

    public PackageModelHelper getPackageModel() {
        return packageModel;
    }

    public void setPackageModel(PackageModelHelper packageModel) {
        this.packageModel = packageModel;
    }
}
