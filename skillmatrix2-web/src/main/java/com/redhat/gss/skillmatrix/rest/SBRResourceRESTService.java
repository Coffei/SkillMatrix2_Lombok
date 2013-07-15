package com.redhat.gss.skillmatrix.rest;

import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.data.api.SbrApiBuilder;
import com.redhat.gss.skillmatrix.model.api.SbrApi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 6/14/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
@Path("/sbrs")
@RequestScoped
public class SBRResourceRESTService {

    @Inject
    private SbrManager manager;

    @Inject
    private SbrApiBuilder builder;

    //@BadgerFish
    @GET
    @Produces({"text/xml", "application/json"})
    public List<SbrApi> listAllSBRs() {
        return builder.buildSbrs(manager.getAllSbrsSortedByName());
    }

    //@BadgerFish
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces({"text/xml", "application/json"})
    public SbrApi listSBRById(@PathParam("id") long id) {
        return builder.buildSbr(manager.getSbrById(id));
    }




}
