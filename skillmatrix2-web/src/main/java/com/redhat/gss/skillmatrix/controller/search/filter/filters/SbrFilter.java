package com.redhat.gss.skillmatrix.controller.search.filter.filters;

import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.SbrInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.SBR;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
@MemberFilter(id = "sbrFilter",
        name = "SBR",
        page = "sbr.xhtml",
        type = FilterType.BASIC)
public class SbrFilter implements Filter {
    private final Logger log = Logger.getLogger(getClass().getName());

    private SBR value;

    private SbrDAO sbrDAO;
    private SbrDAO getDao() { //lazy creation of dao
        if (sbrDAO == null) {
            InitialContext initialContext = null;
            try { //acquire BeanManager in order to get SbrDao
                initialContext = new InitialContext();
                BeanManager beanManager =  (BeanManager) initialContext.lookup("java:comp/BeanManager");

                Bean<SbrDAO> bean = (Bean<SbrDAO>)beanManager.getBeans(SbrDAO.class).iterator().next();
                CreationalContext<SbrDAO> context = beanManager.createCreationalContext(bean);
                sbrDAO = (SbrDAO)beanManager.getReference(bean, SbrDAO.class, context);
            } catch (NamingException e) {
                log.severe(String.format("Unable to create/inject SBRDao. %s\n%S", e.toString(), Arrays.toString(e.getStackTrace())));
            }
        }

        return sbrDAO;
    }


    public SBR getValue() {
        return value;
    }

    public void setValue(SBR value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SbrFilter{" +
                "value=" + value +
                '}';
    }

    @Override
    public String encode() {
        Map<String, String> data = new HashMap<String, String>(1);
        data.put("sbr", value.getId().toString());

        return AttributeEncoder.encodeFromMap("sbrFilter", data);
    }

    @Override
    public void decode(String filter) throws TypeMismatchException, IllegalArgumentException {
        Map<String, String> data = AttributeEncoder.decodeToMap(filter, "sbrFilter");
        if(data==null || data.get("sbr")==null)
            throw new IllegalArgumentException("missing parameter");

        String sbrIdStr = data.get("sbr");
        if(sbrIdStr==null || sbrIdStr.trim().isEmpty())
            throw new IllegalArgumentException("wrong sbr parameter");

        Long id = null;
        try {
            id = Long.parseLong(sbrIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("wrong sbr parameter", e);
        }

        List<SBR> sbrs = getDao().getProducerFactory().filterId(id).getSbrs();
        if(sbrs.isEmpty())
            throw new IllegalArgumentException("unexisting sbr");

        this.value = sbrs.get(0);
    }

    @Override
    public boolean apply(MemberModelHelper modelHelper) {
        return false; //this filter cannot be applied on model helper.
    }

    @Override
    public void applyOnProducer(MemberProducer producer) {
        if(producer==null)
            throw new NullPointerException("producer");

        try {
            producer.filterSBRMembership(this.value);
        } catch (SbrInvalidException e) {
            log.warning(String.format("Invalid SBR. %s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
        }
    }

    @Override
    public String explain() {
        return String.format("SBRs contains '%s'", this.value.getName());
    }
}
