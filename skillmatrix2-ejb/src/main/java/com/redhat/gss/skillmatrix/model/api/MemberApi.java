package com.redhat.gss.skillmatrix.model.api;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 6/14/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "member")
public class MemberApi {
	
	@Getter(onMethod=@_({@XmlAttribute}))
	@Setter
    private Long id;
	
	@Getter(onMethod=@_({@XmlElement(name="full-name")}))
	@Setter
    private String name;
	
	@Getter	@Setter
    private String email;
	
	@Getter(onMethod=@_({@XmlElement(name="kerberos")}))
	@Setter
    private String nick;

	@Getter @Setter
    private String extension;
	
	@Getter @Setter
    private String geo;
	
	@Getter @Setter
    private String role;
	
	@Getter(onMethod=@_({@XmlElementWrapper(name="sbrs"), @XmlElement(name="sbr")}))
	@Setter
    private List<SbrHelper> sbrs;
	
	@Getter(onMethod=@_({@XmlElementWrapper(name = "knowledges"), @XmlElement(name = "knowledge")}))
	@Setter
    private List<KnowledgeHelper> knows;
	
	@Getter(onMethod=@_({@XmlElementWrapper(name = "languages"), @XmlElement(name = "language")}))
	@Setter
    private List<String> langs;

    @XmlRootElement(name = "sbr")
    public static class SbrHelper {
    	@Getter(onMethod=@_({@XmlAttribute}))
    	@Setter
        private Long id;
    	
    	@Getter @Setter
        private String name;
    }

    @XmlRootElement(name = "knowledge")
    public static class KnowledgeHelper {
    	@Getter(onMethod=@_({@XmlAttribute}))
    	@Setter
        private int level;
    	
    	@Getter(onMethod=@_({@XmlAttribute(name="package-id")}))
    	@Setter
        private Long pkgId;
    	
    	@Getter(onMethod=@_({@XmlElement(name = "package-name")}))
    	@Setter
        private String pkgName;

        public KnowledgeHelper() {
        }

        public KnowledgeHelper(int level, Long pkgId, String pkgName) {
            this.level = level;
            this.pkgId = pkgId;
            this.pkgName = pkgName;
        }
    }




}
