package com.redhat.gss.skillmatrix.util.converter;

import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.model.Package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 10/1/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Named
public class PackageConverter implements Converter {

    @Inject
    private PackageDAO pkgDao;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String sid) {
        if(sid!=null && !sid.trim().isEmpty()) {
            try {
                long id = Long.valueOf(sid.trim());
                List<Package> pkgs = pkgDao.getProducerFactory().filterId(id).getPackages();
                if(!pkgs.isEmpty()) {
                    return pkgs.get(0);
                }
            } catch (NumberFormatException e) {}
        }

        return null;

    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if(o==null || !(o instanceof Package))
            return null;

        return String.valueOf(((Package) o).getId());
    }
}
