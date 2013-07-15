package com.redhat.gss.skillmatrix.controller;

import com.google.common.base.Predicate;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.data.PackageKnowledgeManager;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static com.google.common.collect.Collections2.filter;

@ViewScoped
@ManagedBean(name = "knowledgeController")
public class EnterKnowledgeController implements Serializable {
	private static final long serialVersionUID = -4624780602040051087L;

	@Inject
	private FacesContext facesContext;

	@Inject
	private MemberManager memberManager;

	@Inject
	private Logger log;

	@Inject
	private List<Package> packages;

	@Inject
	private List<SBR> sbrs;

	@Inject
	private PackageKnowledgeManager pkgKnowManager;




	private Member member;
	private Map<Package, Integer> knowledge;


	public Map<Package, Integer> getKnowledge() {
		return knowledge;
	}

	public Member getMember() {
		return member;
	}

	public List<SBR> getSbrs() {
		return new ArrayList<SBR>(filter(sbrs, new Predicate<SBR>() { // filter out empty sbrs
			@Override
			public boolean apply(SBR input) {
				return !input.getPackages().isEmpty();
			}
		}));
	}

	public List<Map.Entry<Package, Integer>> getPackagesBySbr(final SBR sbr) {
		return new ArrayList<Map.Entry<Package,Integer>>(filter(knowledge.entrySet(), new Predicate<Map.Entry<Package, Integer>>() {
			@Override
			public boolean apply(Entry<Package, Integer> input) {
				return input.getKey().getSbr().equals(sbr);
			}
		}));

	}

	public String submit() {
		for (Map.Entry<Package, Integer> entry : knowledge.entrySet()) {
			if(entry.getValue() >= 0) { // is valid

				//try to fetch existing knowledge
				List<PackageKnowledge> knows = pkgKnowManager.getAllByPackageAndMember(entry.getKey(), member);
				if(!knows.isEmpty()) { // can be updated
					PackageKnowledge know = knows.get(0);
					if(!know.getLevel().equals(entry.getValue())) { // don't update unless it is needed
						know.setLevel(entry.getValue());

						pkgKnowManager.update(know);
					} 
				} else { // create new
					PackageKnowledge know = new PackageKnowledge();
					know.setLevel(entry.getValue());
					know.setMember(member);
					know.setPackage(entry.getKey());
					
					pkgKnowManager.create(know);
				}
			}
		}
		
		pkgKnowManager.deleteAllWithMemberNotInPackages(member, filter(knowledge.keySet(), new Predicate<Package>() {
			@Override
			public boolean apply(Package input) {
				Integer result = knowledge.get(input);
				return result != null && result >= 0;
			}
		}));
		
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Information saved.", null));

		return "";
	}

	@PostConstruct
	private void init() {
		//get member
		String sid = facesContext.getExternalContext().getRequestParameterMap().get("id");
		if(sid != null) {
			try {
				long id = Long.parseLong(sid);

				this.member = memberManager.getMemberById(id);

			} catch (NumberFormatException e) { }
		}

		if(this.member!=null) {// member loaded
			loadKnowledge();
		} else {// some error in request param
			try {
				facesContext.getExternalContext().redirect("members.jsf");
			} catch (IOException e) {
				log.warning("redirect to members wasn't ok");
			}
		}
	}

	private void loadKnowledge() {
		knowledge = new HashMap<Package, Integer>();

		for (Package pkg : packages) {
			if(pkg.getSbr()!=null) { // put in just the ones with some sbr
				knowledge.put(pkg, -1);
			}
		}

		for (Knowledge know : member.getKnowledges()) {
			if(know instanceof PackageKnowledge) {
				knowledge.put(((PackageKnowledge)know).getPackage(), know.getLevel());
			}
		}
	}



}
