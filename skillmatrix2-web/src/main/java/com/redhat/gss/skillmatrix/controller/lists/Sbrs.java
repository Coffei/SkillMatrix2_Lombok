package com.redhat.gss.skillmatrix.controller.lists;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.SbrModelHelper;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.SbrProducer;

/**
 * Controller bean for all SBRs view.
 * User: jtrantin
 * Date: 9/13/13
 * Time: 3:12 PM
 */
@ManagedBean
@ViewScoped
public class Sbrs implements Serializable {
    private static final int MAX_RECORDS_ON_PAGE = 20;

    @Inject
    private SbrDAO sbrDAO;

    @Getter
    private SbrModelHelper modelHelper;


    @PostConstruct
    private void init() {
        this.modelHelper = new SbrModelHelper(MAX_RECORDS_ON_PAGE) {
            @Override
            public SbrProducer getProducerFactory() {
                return sbrDAO.getProducerFactory();
            }
        };
    }

    public int getMaxRecordsOnPage() {
        return MAX_RECORDS_ON_PAGE;
    }
}
