package com.redhat.gss.skillmatrix.controller.search.filter.filters;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.redhat.gss.skillmatrix.controller.search.filter.BasicAttributeFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
@MemberFilter(id = "nameFilter",
              name = "name",
              page = "basic.xhtml",
              type = FilterType.BASIC)
@ToString(includeFieldNames=true)
@EqualsAndHashCode(of="value")
public class NameFilter implements Filter, BasicAttributeFilter {

	@Getter @Setter
    private String value;

    @Override
    public String encode() {
        return AttributeEncoder.encodeBasicFilter(this);
    }

    @Override
    public void decode(String filter) throws TypeMismatchException, IllegalArgumentException {
        AttributeEncoder.decodeBasicFilter(filter, this);
    }

    @Override
    public boolean apply(MemberModelHelper modelHelper) {
        if(modelHelper==null)
            throw new NullPointerException("modelHelper");

        modelHelper.setNameFilter(this.value);

        return true;
    }

    @Override
    public void applyOnProducer(MemberProducer producer) {
        if(producer==null)
            throw new NullPointerException("producer");

        producer.filterName(this.value);
    }

    @Override
    public String explain() {
        return String.format("name contains '%s'", this.value);
    }
}
