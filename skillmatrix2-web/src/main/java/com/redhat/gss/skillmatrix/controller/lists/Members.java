package com.redhat.gss.skillmatrix.controller.lists;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import lombok.Getter;
import lombok.experimental.Accessors;

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

    @Getter
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


 
    public int getMaxRecordsPerPage() {
        return MAX_RECORDS_PER_PAGE;

    }
}
