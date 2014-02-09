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
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "sbr")
public class SbrApi {
	@Getter(onMethod=@_({@XmlAttribute}))
	@Setter
    private Long id;
	
	@Getter
	@Setter
    private String name;
	
	@Getter(onMethod=@_({@XmlElementWrapper, @XmlElement(name = "tag")}))
	@Setter
    private List<PackageHelper> tags;
	
	@Getter(onMethod=@_({@XmlElementWrapper, @XmlElement(name = "coach")}))
	@Setter
    private List<CoachHelper> coaches;
	
	@Getter(onMethod=@_({@XmlElementWrapper, @XmlElement(name = "member")}))
	@Setter
    private List<MemberHelper> members;


    public static class CoachHelper {
    	@Getter(onMethod=@_({@XmlElement(name="sbr-role")}))
    	@Setter
        private String sbrRole;
    	
    	@Getter
    	@Setter
        private MemberHelper member;
    }

    public static class PackageHelper {
    	@Getter(onMethod=@_({@XmlAttribute}))
    	@Setter
        private Long id;
    	
    	@Getter
    	@Setter
        private String name;
    	
    	@Getter
    	@Setter
        private boolean deprecated;
    }

    public static class MemberHelper {
    	
    	@Getter(onMethod=@_({@XmlAttribute}))
    	@Setter
        private Long id;
    	
    	@Getter @Setter
        private String name;
    	
    	@Getter(onMethod=@_({@XmlElement(name="kerberos")}))
    	@Setter
        private String nick;
    	
    	@Getter @Setter
        private String geo;
    	
    	@Getter @Setter
        private String role;
    	
    	@Getter @Setter
        private String email;
    	
    	@Getter @Setter
        private String extension;

    }

}
