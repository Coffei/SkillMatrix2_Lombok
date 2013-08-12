package com.redhat.gss.skillmatrix.model.api;

import com.redhat.gss.skillmatrix.model.GeoEnum;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 6/14/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "member")
public class MemberApi {
    private Long id;
    private String name;
    private String email;
    private String nick;
    private String extension;
    private String geo;
    private String role;
    private List<SbrHelper> sbrs;
    private List<KnowledgeHelper> knows;
    private List<String> langs;



    @XmlAttribute
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "full-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "kerberos")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    @XmlElementWrapper(name = "sbrs")
    @XmlElement(name = "sbr")
    public List<SbrHelper> getSbrs() {
        return sbrs;
    }

    public void setSbrs(List<SbrHelper> sbrs) {
        this.sbrs = sbrs;
    }

    @XmlElementWrapper(name = "knowledges")
    @XmlElement(name = "knowledge")
    public List<KnowledgeHelper> getKnows() {
        return knows;
    }

    public void setKnows(List<KnowledgeHelper> knows) {
        this.knows = knows;
    }

    @XmlElementWrapper(name = "languages")
    @XmlElement(name = "language")
    public List<String> getLangs() {
        return langs;
    }

    public void setLangs(List<String> langs) {
        this.langs = langs;
    }

    @XmlRootElement(name = "sbr")
    public static class SbrHelper {
        private Long id;
        private String name;

        @XmlAttribute
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @XmlRootElement(name = "knowledge")
    public static class KnowledgeHelper {
        private int level;
        private Long pkgId;
        private String pkgName;

        public KnowledgeHelper() {
        }

        public KnowledgeHelper(int level, Long pkgId, String pkgName) {
            this.level = level;
            this.pkgId = pkgId;
            this.pkgName = pkgName;
        }

        @XmlAttribute
        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        @XmlAttribute(name = "package-id")
        public Long getPkgId() {
            return pkgId;
        }

        public void setPkgId(Long pkgId) {
            this.pkgId = pkgId;
        }

        @XmlElement(name = "package-name")
        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }
    }




}
