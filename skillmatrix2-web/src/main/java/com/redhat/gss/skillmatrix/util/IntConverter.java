package com.redhat.gss.skillmatrix.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Custom Integer converter. Behaves as expected, empty string or "null" string (string containing value "null") or string that is not valid int number is evaluated as null Integer.
 * Null Integer is evaluated as "null".
 * @author jtrantin
 *
 */
@FacesConverter("intconverter")
public class IntConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if(arg2==null)
			return null;
		arg2 = arg2.trim();
		if(arg2.isEmpty() || arg2.equals("null"))
			return null;
		
		try {
			return Integer.valueOf(arg2);
		} catch (NumberFormatException e) {
			return null;
		}
		
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if(arg2==null)
			return "null";
		return arg2.toString();
	}

}
