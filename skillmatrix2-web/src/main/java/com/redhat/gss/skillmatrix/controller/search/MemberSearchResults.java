package com.redhat.gss.skillmatrix.controller.search;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import lombok.val;

import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.FilterCreator;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 2/3/14
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class MemberSearchResults {
    public static final int MAX_RECORDS_PER_PAGE = 20;

    @Getter
    private List<Filter> filters;

    @Inject
    private FilterCreator filterCreator;

    @Inject
    private MemberDAO memberDAO;

    @Inject
    private Logger log;

    @Getter
    private MemberModelHelper modelHelper;
    @Getter
    private int failedFilters = 0;

    @PostConstruct
    private void init() {

        filters = new ArrayList<Filter>();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        for (String paramKey : context.getRequestParameterMap().keySet()) {
            if(paramKey.startsWith("filter")) {
                String encodedFilter = context.getRequestParameterMap().get(paramKey);

                try {
                    Filter filter = filterCreator.createFilter(encodedFilter);

                    if (filter != null)
                        filters.add(filter);
                } catch (Exception e) { //not kocher, but no exception should get to the user
                    log.warning("Filter failed" + e.toString()); //warn dev, otherwise ignore
                    failedFilters++;
                }
            }
        }


        val modelFilters = new ArrayList<Filter>(); //filters to be applied on the model itself
        modelHelper = new MemberModelHelper(MAX_RECORDS_PER_PAGE) {
            @Override
            protected MemberProducer getProducerFactory() {
                val producer =memberDAO.getProducerFactory();
                for (Filter filter : modelFilters) {
                    filter.applyOnProducer(producer);
                }

                return producer;
            }
        };
        //sort the filters
        for (Filter filter : filters) {
            if (!filter.apply(modelHelper)) { //apply filter on modelHelper, if not enough stash the filter to be later applied on model
                modelFilters.add(filter);
            }
        }

        log.info(String.format("There are %d filters- %d are applied to model.", filters.size(), modelFilters.size()));
    }
}
