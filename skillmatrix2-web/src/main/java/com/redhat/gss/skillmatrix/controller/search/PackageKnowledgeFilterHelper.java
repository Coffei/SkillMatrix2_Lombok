package com.redhat.gss.skillmatrix.controller.search;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.PackageModelHelper;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 2/4/14
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "pkgKnowHelper")
@ViewScoped
public class PackageKnowledgeFilterHelper implements Serializable {
    private static final int PKG_POPUP_MAX_ROWS = 8;


    @Inject
    private PackageDAO pkgDao;
    @Getter
    private PackageModelHelper pkgModelHelper;


    @PostConstruct
    private void init() {
        pkgModelHelper = new PackageModelHelper(PKG_POPUP_MAX_ROWS) {
            @Override
            protected PackageProducer getProducer() {
                return pkgDao.getProducerFactory();
            }
        };


    }
}
