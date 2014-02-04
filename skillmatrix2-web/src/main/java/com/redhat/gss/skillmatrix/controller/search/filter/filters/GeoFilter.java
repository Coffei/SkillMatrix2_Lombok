package com.redhat.gss.skillmatrix.controller.search.filter.filters;

import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.model.GeoEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
@MemberFilter(id = "geoFilter",
        name = "GEO",
        page = "geo.xhtml",
        type = FilterType.BASIC)
public class GeoFilter implements Filter {

    private GeoEnum value;

    public GeoEnum getValue() {
        return value;
    }

    public void setValue(GeoEnum value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GeoFilter{" +
                "value=" + value +
                '}';
    }

    @Override
    public String encode() {
       Map<String, String> data  = new HashMap<String, String>(1);
       data.put("geo", value.toString());

        return AttributeEncoder.encodeFromMap("geoFilter", data);
    }

    @Override
    public void decode(String filter) throws TypeMismatchException, IllegalArgumentException {
        Map<String, String> data = AttributeEncoder.decodeToMap(filter, "geoFilter");
        if(data==null || data.get("geo")==null)
            throw new IllegalArgumentException("missing parameter");

        GeoEnum value = GeoEnum.parseGeo(data.get("geo"));
        if(value==null)
            throw new IllegalArgumentException("wrong geo parameter");

        this.value = value;
    }

    @Override
    public boolean apply(MemberModelHelper modelHelper) {
        if(modelHelper==null)
            throw new NullPointerException("modelHelper");

        modelHelper.setGeoFilter(this.value.toString());

        return true;
    }

    @Override
    public void applyOnProducer(MemberProducer producer) {
        if(producer==null)
            throw new NullPointerException("producer");

        producer.filterGeo(this.value);
    }

    @Override
    public String explain() {
        return String.format("GEO is '%s'", this.value);
    }
}
