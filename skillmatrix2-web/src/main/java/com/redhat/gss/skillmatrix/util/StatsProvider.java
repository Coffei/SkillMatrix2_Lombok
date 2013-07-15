package com.redhat.gss.skillmatrix.util;

import com.google.common.collect.Sets;
import com.redhat.gss.skillmatrix.data.SbrManager;
import com.redhat.gss.skillmatrix.model.*;
import com.redhat.gss.skillmatrix.model.Package;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.Map.Entry;

/**
 * Statistics provider. Wraps all relevant methods. Mostly, there is one-level caching, so retrieving the same result multiple times should be fast.
 * <p><b>KnowScore</b>- is knowledge score, a way how to compare level of knowledge. A value is computed as 2^(level of knowledge) or 0 if no knowledge is defined, thus <br/>
 * <ul>
 * <li>no knowledge = 0</li>
 * <li>beginner = 1</li>
 * <li>intermediate = 2</li>
 * <li>expert = 4</li></ul>
 * @author jtrantin
 *
 */
@Named
@RequestScoped
public class StatsProvider  {
	@Inject
	private SbrManager sbrManager;

	@Inject
	private List<SBR> sbrs;

	//in,out stat
	private int lastLevel; // for one-level-caching
	private Member lastMember;
	private Integer in;
	private Integer out;
	/**
	 * How many % of knowledge of particular level lies inside members SBRs
	 * @param member member for which to evaluate statistics, cannot be null
	 * @param level level of knowledge, between 0 and 2 inclusive
	 * @return percentage
	 */
	public Integer getPackagesInSbrsPercentage(Member member, int level) {
		if(member==null)
			throw new NullPointerException("member");
		
		if(level!=lastLevel || !member.equals(lastMember) || in==null) {
			countPackagesInOutSbrsPercentage(member, level);
			lastLevel = level;
			lastMember = member;
		}
		return in;
	}
	/**
	 * How many % of knowledge of particular level lies outside members SBRs
	 * @param member member for which to evaluate statistics, cannot be null
	 * @param level level of knowledge, between 0 and 2 inclusive
	 * @return percentage
	 */
	public Integer getPackagesOutSbrsPercentage(Member member, int level) {
		if(member==null)
			throw new NullPointerException("member");
		
		if(level!=lastLevel || !member.equals(lastMember) || out==null) {
			countPackagesInOutSbrsPercentage(member, level);
			lastLevel = level;
			lastMember = member;
		}
		return out;
	}
	private void countPackagesInOutSbrsPercentage(Member member, int level) {
		Set<Package> allpackages = Sets.newHashSet();
		for (Knowledge know : member.getKnowledges()) {
			if(know instanceof PackageKnowledge) { // add all packages, which knowledge level is equal to param
				if(know.getLevel().intValue() == level) { 
					allpackages.add(((PackageKnowledge) know).getPackage());
				}
			}
		}

		Set<Package> sbrpackages = Sets.newHashSet();
		for (MemberSbr ms : member.getMembersbrs()) {
			SBR sbr = sbrManager.getSbrById(ms.getSbr().getId());
			sbrpackages.addAll(sbr.getPackages());
		}

		int innum = Sets.intersection(allpackages, sbrpackages).size();
		int outnum = allpackages.size() - innum;

		if(innum+outnum!=0){
			in = (innum * 100) / (innum +outnum);
			out = 100 - in;
		} else {
			in = 0;
			out = 0;
		}
	}

	//how much of knowledge is in particular SBR, sort by highest
	private Member lastMember2; // one-level-cache
	private List<Map.Entry<SBR, Integer>> perOfKnowInSbrs;
	
	/**
	 * Returns map, showing how much knowledge (using KnowScore) lies in particular SBR, sorted by the percentage
	 * @see com.redhat.gss.skillmatrix.util.StatsProvider StatsProvider.KnowScore
	 * @param member member for which to evaluate statistics, cannot be null
	 * @return
	 */
	public List<Map.Entry<SBR, Integer>> getPercentageOfKnowledgeInSbrs(Member member) {
		if(member==null)
			throw new NullPointerException("member");
		
		if(!member.equals(lastMember2) || perOfKnowInSbrs == null) {
			countPercentageOfKnowledgeInSbrs(member);
			lastMember2 = member;
		}

		return perOfKnowInSbrs;	
	}

	private void countPercentageOfKnowledgeInSbrs(Member member) {
		
		Map<SBR, Integer> result = new HashMap<SBR, Integer>();


		int totalScore = countKnowScore(member);

		for(SBR sbr : sbrs) {
			int sbrScore = countKnowScore(member, sbr);
			if(totalScore!=0) {
				result.put(sbr, (sbrScore * 100) / totalScore);
			} else {
				result.put(sbr, 0);
			}

		}

		List<Map.Entry<SBR, Integer>> sorted = new ArrayList<Map.Entry<SBR,Integer>>();
		sorted.addAll(result.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<SBR,Integer>>() {
			public int compare(Entry<SBR, Integer> o1, Entry<SBR, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});

		perOfKnowInSbrs = sorted;

	}

	public int countKnowScore(Member member) {
		if(member==null)
			return 0;
		
		int result = 0;
		for (Knowledge know : member.getKnowledges()) {
			if(know instanceof PackageKnowledge) {
				result += (int)Math.pow(2, know.getLevel());
			}
		}

		return result;
	}

	public int countKnowScore(Member member, SBR sbr) {
		if(member==null || sbr==null)
			return 0;
		
		int result = 0;
		for (Knowledge know : member.getKnowledges()) {
			if(know instanceof PackageKnowledge) {
				if(sbr.getPackages().contains(((PackageKnowledge)know).getPackage())) {
					result += (int)Math.pow(2, know.getLevel());
				}
			}
		}
		return result;
	}
	
	public int countKnowScore(SBR sbr) {
		if(sbr==null)
			return 0;
		
		return sbr.getPackages().size() * 4;
		
	}



}