package com.redhat.gss.skillmatrix.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.val;

import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.model.SBR;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 9/16/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
//FacesConverter(forClass = SBR.class)
@Named
public class SbrConverter implements Converter {

    @Inject
    private SbrDAO sbrDao;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            long id = Long.parseLong(s);
            val sbrs = sbrDao.getProducerFactory().filterId(id).getSbrs();
            if(!sbrs.isEmpty())
                return sbrs.get(0);

        } catch (NumberFormatException ex) {}

        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if(o==null || !(o instanceof SBR))
            return "";

        return String.valueOf(((SBR) o).getId());
    }
}
