package com.redhat.gss.skillmatrix.model.api;

import com.redhat.gss.skillmatrix.model.GeoEnum;
import com.redhat.gss.skillmatrix.model.RoleEnum;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

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
    private MemberHelper coach;
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

    public MemberHelper getCoach() {
        return coach;
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

    public void setCoach(MemberHelper coach) {
        this.coach = coach;
    }

    public void setMembers(List<MemberHelper> members) {
        this.members = members;
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
        private GeoEnum geo;
        private RoleEnum role;
        private String email;
        private String extension;
        private int level;

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

        public GeoEnum getGeo() {
            return geo;
        }

        public RoleEnum getRole() {
            return role;
        }

        public String getEmail() {
            return email;
        }

        public String getExtension() {
            return extension;
        }


        @XmlAttribute(name = "sbr-level")
        public int getLevel() {
            return level;
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

        public void setGeo(GeoEnum geo) {
            this.geo = geo;
        }

        public void setRole(RoleEnum role) {
            this.role = role;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }

}
