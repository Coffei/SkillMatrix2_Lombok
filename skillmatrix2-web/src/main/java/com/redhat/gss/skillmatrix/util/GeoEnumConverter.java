package com.redhat.gss.skillmatrix.util;

import com.redhat.gss.skillmatrix.model.GeoEnum;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter for GeoEnum.
 * @author jtrantin
 *
 */
@FacesConverter("geoconverter")
public class GeoEnumConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if(value==null)
			return null;
		if(value.trim().length() < 1)
			return null;
		
		return GeoEnum.parseEnum(value.trim());
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		
		return value.toString();
	}

}
