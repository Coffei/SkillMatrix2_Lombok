package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.enterprise.inject.Produces;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 9/17/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Named("dao")
@RequestScoped
public class DaoHelper {

    @Inject
    private MemberDAO memberDAO;

    @Inject
    private SbrDAO sbrDAO;

    @Inject
    private PackageDAO packageDAO;

    public boolean canMemberModify() {
        return memberDAO.canModify();
    }

    public boolean canSbrModify() {
        return sbrDAO.canModify();
    }

    public boolean canPkgModify() {
        return packageDAO.canModify();
    }

    @Produces
    @Named("allSBRs")
    public List<SBR> getAllSbrs() {
        return sbrDAO.getProducerFactory().sortName(true).getSbrs();
    }


    @Produces
    @Named("allMembers")
    public List<Member> getAllMembers() {
        return memberDAO.getProducerFactory().sortName(true).getMembers();
    }


    @Produces
    @Named("allPackages")
    public List<Package> getAllPackages() {
        return packageDAO.getProducerFactory().sortName(true).getPackages();
    }

    @Produces
    @Named("allGeos")
    public List<GeoEnum> getAllGeos() {
        List<GeoEnum> geos = Arrays.asList(GeoEnum.values());
        Collections.sort(geos);
        return geos;
    }
}
