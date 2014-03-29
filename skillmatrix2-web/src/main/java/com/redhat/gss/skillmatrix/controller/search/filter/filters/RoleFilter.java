package com.redhat.gss.skillmatrix.controller.search.filter.filters;

import com.redhat.gss.skillmatrix.controller.search.filter.BasicAttributeFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 12/6/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
@MemberFilter(id = "roleFilter",
        name = "role",
        page = "basic.xhtml",
        type = FilterType.BASIC)
@ToString(includeFieldNames=true)
@EqualsAndHashCode(of="value")
public class RoleFilter implements Filter, BasicAttributeFilter {

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
    public boolean apply(@NonNull MemberModelHelper modelHelper) {

        modelHelper.setRoleFilter(this.value);

        return true;
    }

    @Override
    public void applyOnProducer(@NonNull MemberProducer producer) {

        producer.filterRole(this.value);
    }

    @Override
    public String explain() {
        return String.format("role contains '%s'", this.value);
    }
}
