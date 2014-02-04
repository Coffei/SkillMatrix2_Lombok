package com.redhat.gss.skillmatrix.util.converter;

import com.redhat.gss.skillmatrix.data.dao.interfaces.SbrDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.SBR;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Faces converter for OperatorEnum. It is implemented as CDI bean due to a bug in JSF.
 * User: jtrantin
 * Date: 9/16/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */

@Named
public class OperatorEnumConverter implements Converter {

   @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
      if(s==null || s.isEmpty())
          return null;

       return OperatorEnum.fromReadableText(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if(o==null || !(o instanceof OperatorEnum))
            return "";

        return ((OperatorEnum)o).toReadableText();
    }
}
