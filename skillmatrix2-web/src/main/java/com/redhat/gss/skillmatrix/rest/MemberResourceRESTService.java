package com.redhat.gss.skillmatrix.rest;

import com.redhat.gss.skillmatrix.data.api.MemberApiBuilder;
import com.redhat.gss.skillmatrix.data.dao.interfaces.MemberDAO;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.api.MemberApi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * JAX-RS Example
 *
 * This class produces a RESTful service to read the contents of the members table.
 */
@Path("/members")
@RequestScoped
public class MemberResourceRESTService {

    @Inject
    private MemberDAO manager;

    @Inject
    private MemberApiBuilder builder;

    @GET
    @Produces({"text/xml", "application/json"})
    public List<MemberApi> listAllMembers() {

        final List<Member> results = manager.getProducerFactory().getMembers();


        return builder.buildMembers(results);
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces({"text/xml", "application/json" })
    public MemberApi lookupMemberById(@PathParam("id") long id) {
        List<Member> members = manager.getProducerFactory().filterId(id).getMembers();
        //TODO: whatif not found!
        return builder.buildMember(members.get(0));
    }


}
