package com.redhat.gss.skillmatrix.controller.lists;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.SBR;
import org.joda.time.Duration;
import org.joda.time.Period;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Controller bean for all members view.
 * User: jtrantin
 * Date: 8/19/13
 * Time: 2:11 PM
 */
@ViewScoped
@ManagedBean
public class Members implements Serializable {
    public static final int MAX_RECORDS_PER_PAGE = 20;

    private MemberModelHelper modelHelper;

    @Inject
    private MemberDAO memberDAO;


    @PostConstruct
    private void init() {
        modelHelper = new MemberModelHelper(MAX_RECORDS_PER_PAGE) {
            @Override
            protected MemberProducer getProducerFactory() {
                return memberDAO.getProducerFactory();
            }
        };

    }


    public MemberModelHelper getModelHelper() {
        return modelHelper;
    }



    public int getMaxRecordsPerPage() {
        return MAX_RECORDS_PER_PAGE;
    }
}
