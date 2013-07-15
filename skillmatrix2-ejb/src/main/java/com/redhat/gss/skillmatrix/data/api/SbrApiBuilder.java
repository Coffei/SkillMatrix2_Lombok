package com.redhat.gss.skillmatrix.data.api;

import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.api.SbrApi;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 6/14/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SbrApiBuilder {

    public SbrApi buildSbr(SBR sbr) {
        SbrApi api = new SbrApi();
        api.setId(sbr.getId());
        api.setName(sbr.getName());
        //coach
        if(sbr.getCoach()!=null) {
            SbrApi.MemberHelper coachApi = buildMemberHelper(sbr.getCoach());
            api.setCoach(coachApi);
        }
        //members
        List<SbrApi.MemberHelper> membersApi = new ArrayList<SbrApi.MemberHelper>(sbr.getMembersbrs().size());
        for(MemberSbr member : sbr.getMembersbrs()) {
            SbrApi.MemberHelper memberApi = buildMemberHelper(member.getMember());
            memberApi.setLevel(member.getLevel());
            membersApi.add(memberApi);
        }

        api.setMembers(membersApi);

        // tags
        List<SbrApi.PackageHelper> pkgsApi = new ArrayList<SbrApi.PackageHelper>(sbr.getPackages().size());
        for(Package pkg : sbr.getPackages()) {
            pkgsApi.add(buildPackageHelper(pkg));
        }
        api.setTags(pkgsApi);


        return api;
    }

    public List<SbrApi> buildSbrs(List<SBR> sbrs) {
        List<SbrApi> apis = new ArrayList<SbrApi>(sbrs.size());
        for (SBR sbr : sbrs) {
            apis.add(buildSbr(sbr));
        }

        return apis;
    }

    private SbrApi.MemberHelper buildMemberHelper(Member member) {
        SbrApi.MemberHelper memberApi = new SbrApi.MemberHelper();
        memberApi.setName(member.getName());
        memberApi.setId(member.getId());
        memberApi.setRole(member.getRole());
        memberApi.setGeo(member.getGeo());
        memberApi.setEmail(member.getEmail());
        memberApi.setNick(member.getNick());
        memberApi.setExtension(member.getExtension());

        return memberApi;
    }

    private  SbrApi.PackageHelper buildPackageHelper(Package pkg) {
        SbrApi.PackageHelper pkgApi = new SbrApi.PackageHelper();
        pkgApi.setId(pkg.getId());
        pkgApi.setName(pkg.getName());
        pkgApi.setDeprecated(pkg.isDeprecated());

        return pkgApi;
    }
}
