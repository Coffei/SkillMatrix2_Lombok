package com.redhat.gss.skillmatrix.model.api;

import com.redhat.gss.skillmatrix.model.GeoEnum;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 6/14/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "sbr")
public class SbrApi {
    private Long id;
    private String name;
    private List<PackageHelper> tags;
    private List<CoachHelper> coaches;
    private List<MemberHelper> members;

    @XmlAttribute
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @XmlElementWrapper
    @XmlElement(name = "tag")
    public List<PackageHelper> getTags() {
        return tags;
    }

    @XmlElementWrapper
    @XmlElement(name = "coach")
    public List<CoachHelper> getCoaches() {
        return coaches;
    }

    @XmlElementWrapper
    @XmlElement(name = "member")
    public List<MemberHelper> getMembers() {
        return members;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(List<PackageHelper> tags) {
        this.tags = tags;
    }

    public void setCoaches(List<CoachHelper> coaches) {
        this.coaches = coaches;
    }

    public void setMembers(List<MemberHelper> members) {
        this.members = members;
    }


    public static class CoachHelper {
        private String sbrRole;
        private MemberHelper member;

        @XmlElement(name = "sbr-role")
        public String getSbrRole() {
            return sbrRole;
        }

        public void setSbrRole(String sbrRole) {
            this.sbrRole = sbrRole;
        }

        @XmlElement
        public MemberHelper getMember() {
            return member;
        }

        public void setMember(MemberHelper member) {
            this.member = member;
        }
    }

    public static class PackageHelper {
        private Long id;
        private String name;
        private boolean deprecated;

        @XmlAttribute
        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isDeprecated() {
            return deprecated;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
        }
    }

    public static class MemberHelper {
        private Long id;
        private String name;
        private String nick;
        private String geo;
        private String role;
        private String email;
        private String extension;

        @XmlAttribute
        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @XmlElement(name = "kerberos")
        public String getNick() {
            return nick;
        }

        public String getGeo() {
            return geo;
        }

        public String getRole() {
            return role;
        }

        public String getEmail() {
            return email;
        }

        public String getExtension() {
            return extension;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public void setGeo(String geo) {
            this.geo = geo;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }
    }

}
