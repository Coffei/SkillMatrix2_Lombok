package com.redhat.gss.skillmatrix.controller.search.filter.filters.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.val;

import com.redhat.gss.skillmatrix.controller.search.filter.BasicAttributeFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/10/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class AttributeEncoder {

    public static String encodeBasicFilter(BasicAttributeFilter filter) {
        if(filter==null)
            return null;
        MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
        String type = annt.id();
        val data = new HashMap<String, String>(1);
        data.put("value", filter.getValue());

        return encodeFromMap(type, data);
    }

    public static void decodeBasicFilter(String encodedFilter, BasicAttributeFilter filter) throws TypeMismatchException, IllegalArgumentException {
        //get filter type
        if(filter==null)
            return;
        MemberFilter annt = filter.getClass().getAnnotation(MemberFilter.class);
        String type = annt.id();

        //get decoded data
        val data = decodeToMap(encodedFilter, type);

        String value = data.get("value");
        if(value==null)
            throw new IllegalArgumentException("missing value attribute");

        filter.setValue(value);
    }

    public static Map<String, String> decodeToMap(String encodedFilter, String requestedType) throws TypeMismatchException, IllegalArgumentException {
        val result = new HashMap<String, String>();
        Pattern typeCheckPattern = Pattern.compile("^([^:]+):(.*)$");
        Pattern paramsValidity = Pattern.compile("^(([\\w]+)-((([^;])|(\\\\;))+)?[^\\\\;];)*$");
        Pattern params = Pattern.compile("([\\w]+)-(((([^;])|(\\\\;))+)?[^\\\\;]);"); // all ; in values body are escaped as \;
        // group 1 is name of param
        // group 2 is value of param

        //verify compatibility
        Matcher typeMatcher = typeCheckPattern.matcher(encodedFilter);
        if (typeMatcher.matches()) {
            if (typeMatcher.group(1).equals(requestedType)) { //type matches
                if(paramsValidity.matcher(typeMatcher.group(2)).matches()) {
                    Matcher paramMatcher = params.matcher(typeMatcher.group(2));
                    while (paramMatcher.find()) {
                        String name = paramMatcher.group(1);
                        String value = paramMatcher.group(2);
                        //eliminate escape chars
                        value = value.replace("\\\\", "\\");
                        value = value.replace("\\;", ";");
                        result.put(name,value);
                    }
                } else {
                    throw new IllegalArgumentException("parameters in wrong format");
                }
            } else {
                throw new TypeMismatchException(String.format("wrong filter class, expected <emailFilter> got <%s>", typeMatcher.group(1)));
            }
        } else {
            throw new IllegalArgumentException("unexpected filter format");
        }

        return result;
    }

    public static String encodeFromMap(String filterType, Map<String, String> data) {
        if(filterType==null) //maybe other checks?
            return null;


        StringBuilder builder = new StringBuilder(filterType);
        builder.append(":");
        for (val entry : data.entrySet()) {
            builder.append(entry.getKey());
            builder.append("-"); //using '-' instead of '=' because of HTML param encoding
            String value = new String(entry.getValue()); //create new instance of value
            if(value.contains("\\") || value.contains(";")) {
                value = value.replace("\\", "\\\\");
                value = value.replace(";", "\\;");
            }
            builder.append(value);
            builder.append(";");
        }

        return builder.toString();
    }

}
