package com.redhat.gss.skillmatrix.data.api;

import com.redhat.gss.skillmatrix.model.Coach;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.Package;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.model.api.SbrApi;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

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
        //coaches
        if(sbr.getCoaches()!=null && !sbr.getCoaches().isEmpty()) {
            List<SbrApi.CoachHelper> coachesApi = new ArrayList<SbrApi.CoachHelper>(sbr.getCoaches().size());
            for(Coach coach : sbr.getCoaches()) {
                coachesApi.add(buildCoachHelper(coach));
            }

            api.setCoaches(coachesApi);
        }

        //members
        List<SbrApi.MemberHelper> membersApi = new ArrayList<SbrApi.MemberHelper>(sbr.getMembers().size());
        for(Member member : sbr.getMembers()) {
            SbrApi.MemberHelper memberApi = buildMemberHelper(member);

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

    private SbrApi.CoachHelper buildCoachHelper(Coach coach) {
        SbrApi.CoachHelper coachHelper = new SbrApi.CoachHelper();
        coachHelper.setSbrRole(coach.getSbr_role());
        coachHelper.setMember(buildMemberHelper(coach.getMember()));

        return coachHelper;
    }


    private SbrApi.MemberHelper buildMemberHelper(Member member) {
       SbrApi.MemberHelper memberApi = new SbrApi.MemberHelper();
        memberApi.setName(member.getName());
        memberApi.setId(member.getId());
        memberApi.setRole(member.getRole());
        memberApi.setGeo(new MemberApiBuilder().buildGeo(member.getGeo()));
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
