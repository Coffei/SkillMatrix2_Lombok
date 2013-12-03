package com.redhat.gss.skillmatrix.controller.details;

import com.redhat.gss.skillmatrix.controllers.sorthelpers.PackageModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.MemberInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.PackageProducer;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/22/13
 * Time: 5:08 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MemberDetail {
    private static final int PACKAGE_RECORDS = 3;

    @Inject
    private MemberDAO memberDAO;

    @Inject
    private Logger log;

    @Inject
    private PackageDAO pkgDao;

    private Member member;

    private PackageModelHelper expertModel;
    private PackageModelHelper intermediateModel;
    private PackageModelHelper beginnerModel;

    private boolean expertsAvailable;
    private boolean intermediatesAvailable;
    private boolean beginnersAvailable;


    @PostConstruct
    private void init() {
        String sid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.parseLong(sid);

                List<Member> members = memberDAO.getProducerFactory().filterId(id).getMembers();
                if(members!=null && !members.isEmpty()) {
                    this.member = members.get(0);
                    Collections.sort(this.member.getSbrs(), new Comparator<SBR>() {
                        @Override
                        public int compare(SBR sbr, SBR sbr2) {
                            return sbr.getName().compareTo(sbr2.getName());
                        }
                    });

                    this.expertModel = new PackageModelHelper(PACKAGE_RECORDS) {
                        @Override
                        protected PackageProducer getProducer() {
                            try {
                                return pkgDao.getProducerFactory().filterKnowledgeByPerson(member, 2);
                            } catch (MemberInvalidException e) {
                                log.severe(e.getStackTrace().toString());
                                return null; //should not happen
                            }
                        }
                    };

                    this.intermediateModel = new PackageModelHelper(PACKAGE_RECORDS) {
                        @Override
                        protected PackageProducer getProducer() {
                            try {
                                return pkgDao.getProducerFactory().filterKnowledgeByPerson(member, 1);
                            } catch (MemberInvalidException e) {
                                log.severe(e.getStackTrace().toString());
                                return null;
                            }
                        }
                    };

                    this.beginnerModel = new PackageModelHelper(PACKAGE_RECORDS) {
                        @Override
                        protected PackageProducer getProducer() {
                            try {
                                return pkgDao.getProducerFactory().filterKnowledgeByPerson(member, 0);
                            } catch (MemberInvalidException e) {
                                log.severe(e.getStackTrace().toString());
                                return null;
                            }
                        }
                    };

                    //init this stuff here so it is not influenced by filters that can be added later
                    expertsAvailable = expertModel.getModel().getRowCount() > 0;
                    intermediatesAvailable = intermediateModel.getModel().getRowCount() > 0;
                    beginnersAvailable = beginnerModel.getModel().getRowCount() > 0;

                    // init other stuff here

                }
            } catch (NumberFormatException e) {} //ignore if happens
        }
    }

    public List<SBR> getRestSBRS() { //quite a cheap trick, can do better
        if(member.getSbrs()!=null && member.getSbrs().size() > 1)
            return member.getSbrs().subList(1, member.getSbrs().size());
        else
            return new ArrayList<SBR>(0); //cannot use Collections.emptyList(); due to some bug
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public PackageModelHelper getExpertModel() {
        return expertModel;
    }

    public void setExpertModel(PackageModelHelper expertModel) {
        this.expertModel = expertModel;
    }

    public PackageModelHelper getIntermediateModel() {
        return intermediateModel;
    }

    public void setIntermediateModel(PackageModelHelper intermediateModel) {
        this.intermediateModel = intermediateModel;
    }

    public PackageModelHelper getBeginnerModel() {
        return beginnerModel;
    }

    public void setBeginnerModel(PackageModelHelper beginnerModel) {
        this.beginnerModel = beginnerModel;
    }

    public boolean isExpertsAvailable() {
        return expertsAvailable;
    }

    public boolean isIntermediatesAvailable() {
        return intermediatesAvailable;
    }

    public boolean isBeginnersAvailable() {
        return beginnersAvailable;
    }
}
