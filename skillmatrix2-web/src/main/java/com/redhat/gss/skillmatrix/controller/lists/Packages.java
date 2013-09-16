package com.redhat.gss.skillmatrix.controller.lists;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.PackageModelHelper;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Controller bean for all packages view.
 * User: jtrantin
 * Date: 9/16/13
 * Time: 10:44 AM
 */
@ManagedBean
@ViewScoped
public class Packages  implements Serializable {
    private static final int MAX_RECORDS_ON_PAGE = 20;

    @Inject
    private PackageDAO pkgDao;

    private PackageModelHelper modelHelper;

    @PostConstruct
    private void init() {
        modelHelper = new PackageModelHelper(MAX_RECORDS_ON_PAGE) {
            @Override
            protected PackageProducer getProducer() {
                return pkgDao.getPackageProducer();
            }
        };
    }

    public PackageModelHelper getModelHelper() {
        return modelHelper;
    }

    public int getMaxRecordsOnPage() {
        return MAX_RECORDS_ON_PAGE;
    }
}
