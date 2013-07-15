package com.redhat.gss.skillmatrix.controller;

import com.redhat.gss.skillmatrix.controller.sorthelpers.MemberSortingFilteringHelper;
import com.redhat.gss.skillmatrix.data.MemberManager;
import com.redhat.gss.skillmatrix.model.Member;
import com.redhat.gss.skillmatrix.model.MemberSbr;
import com.redhat.gss.skillmatrix.model.SBR;
import com.redhat.gss.skillmatrix.util.StatsProvider;
import com.redhat.gss.skillmatrix.util.datamodels.AllMembersModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.awt.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Bean controller for matrix-view.
 * @author jtrantin
 *
 */
@ManagedBean
@ViewScoped
public class MatrixController implements Serializable {
	private static final long serialVersionUID = 7458403089802523130L;

	private static final int[] GOOD_PERC_THRESHOLD = new int[] {70, 40, 20};
	private static final int[] BAD_PERC_THRESHOLD = new int[] {30, 10};
	private static final String DIMMED_COLOR = "#999999";

	@Inject
	private EntityManager em;

	@Inject
	private MemberManager memberManager;

	@Inject
	private Conversation  conversation;

	@Inject
	private StatsProvider statsProvider;

	@Inject
	private List<SBR> sbrs;

	private MemberSortingFilteringHelper memberHelper;

	private Color startGreen = new Color(210, 255, 210);
	private Color endGreen = new Color(0, 210, 0);

	private Color endRed = new Color(255, 10, 10);
	private Color startRed = new Color(255, 230, 230);

	private boolean allowColors = false;

	
	/**
	 * @return the allowColors
	 */
	public boolean isAllowColors() {
		return allowColors;
	}

	/**
	 * Switches colors
	 */
	public void switchAllowColors() {
		this.allowColors = !allowColors;
	}

	/**
	 * Returns member sorting helper with model.
	 * @return
	 */
	public MemberSortingFilteringHelper getMemberHelper() {
		return memberHelper;
	}

	/**
	 * Generates column styles, first style is {@code firstStyle} then several {@code otherStyle} is added.
	 * @param firstStyle CSS style
	 * @param otherStyle CSS style
	 * @param howMany how many other styles to add
	 * @return string ready to insert to columnStyles
	 */
	public String generateColumnStyles(String firstStyle, String otherStyle, int howMany) {
		StringBuilder builder = new StringBuilder(firstStyle);

		for(int i = 0; i < howMany ; i++) {
			builder.append(",");
			builder.append(otherStyle);
		}

		return builder.toString();
	}

	/**
	 * Returns string showing members membership in {@code sbr}. This number is equal to MemberSbr.level where MemberSbr.member == member and MemberSbr.sbr == sbr.
	 * @param member member instance
	 * @param sbr sbr instance
	 * @return formated number- sbr membership level
	 */
	public String getSbrString(Member member, SBR sbr) {
		Integer level = getSbrLevel(member, sbr);
		if(level!=null) {
			return level.toString() + ".&nbsp;/&nbsp;";
		} else {
			return "";
		}

	}

	/**
	 * Returns string representing percentage of members knowledge of specified {@code sbr}.
	 * @param member member instance
	 * @param sbr sbr instance
	 * @return formatted string representing knowledge.
	 */
	public String getPercentageString(Member member, SBR sbr) {
		Integer res = getPercentage(member, sbr);
		if(res!=null) {
			return res.toString() + "%";
		} else {
			return "";
		}
	}

	public String getTextColor(Member member, SBR sbr) {
		if(getSbrLevel(member, sbr)==null && getPercentage(member, sbr) == 0) {
			return DIMMED_COLOR;
		}

		return "";
	}

	/**
	 * Generates string representing background color of a cell.
	 * @param member member instance
	 * @param sbr sbr instance
	 * @return valid HTML color or empty string
	 */
	public String getColor(Member member, SBR sbr) { 
		if(allowColors) {
			Integer level = getSbrLevel(member, sbr);
			if(level!=null) {
				Integer perc = getPercentage(member, sbr);
				if(level <= GOOD_PERC_THRESHOLD.length && perc > GOOD_PERC_THRESHOLD[level-1]) {
					perc -= GOOD_PERC_THRESHOLD[level-1];
					double coef = (double)perc / (double)(100 - GOOD_PERC_THRESHOLD[level-1]);
					Color fin = mix(startGreen, endGreen, coef);

					return toColorString(fin);
				} else if (level <= BAD_PERC_THRESHOLD.length && perc < BAD_PERC_THRESHOLD[level-1]) {
					double coef = 1d - ((double)perc / (double)(BAD_PERC_THRESHOLD[level-1]));
					Color fin = mix(startRed, endRed, coef);

					return toColorString(fin);
				}
			}
		}
		return "";
	}


	/**
	 * Generates string containing members SBRs sorted by level and separated by ','.
	 * @param member member instance
	 * @return human readable string
	 */
	public String getSbrsString(Member member) {
		if(member.getMembersbrs()!=null && !member.getMembersbrs().isEmpty()) {
			Collections.sort(member.getMembersbrs(), new Comparator<MemberSbr>() { //sort sbrs by their level
				@Override
				public int compare(MemberSbr arg0, MemberSbr arg1) {
					return arg0.getLevel() - arg1.getLevel();
				}
			});

			StringBuilder builder = new StringBuilder(member.getMembersbrs().get(0).getSbr().getName());
			member.getMembersbrs().remove(0);
			for (MemberSbr ms : member.getMembersbrs()) {
				builder.append(", ");
				builder.append(ms.getSbr().getName());
			}

			return builder.toString();
		} 

		return "";
	}

	/**
	 * Returns percentage representing how much of members knowledge is in specified SBR.
	 * @param member members instance
	 * @param sbr sbr instance
	 * @return percentage, value between 0 and 100 inclusive
	 */
	public Integer getMemberSbrRatio(Member member, SBR sbr) {
		if(member==null || sbr==null)
			return 0;

		int memberScore = statsProvider.countKnowScore(member);
		int sbrScore = statsProvider.countKnowScore(member, sbr);

		if (memberScore!=0) {
			return (sbrScore * 100) / memberScore;
		} else {
			return 0;
		}
	}

	/**
	 * Converts Color to html string.
	 * @param c color
	 * @return html string
	 */
	private String toColorString(Color c) {
		StringBuilder b = new StringBuilder("#");
		b.append(String.format("%02x", c.getRed()));
		b.append(String.format("%02x", c.getGreen()));
		b.append(String.format("%02x", c.getBlue()));

		return b.toString();
	}

	private Integer getSbrLevel(Member member, SBR sbr) {
		for (MemberSbr ms : member.getMembersbrs()) {
			if(ms.getSbr().equals(sbr)) {
				return ms.getLevel();
			}
		}

		return null;
	}


	private Integer getPercentage(Member member, SBR sbr) {
		//compute result
		int memberScore = statsProvider.countKnowScore(member,sbr);
		int sbrScore = statsProvider.countKnowScore(sbr);

		if(sbrScore == 0) 
			return 0;

		return (memberScore * 100) / sbrScore;
	}

	/**
	 * Mixes c1 and c2 according to coefficient (coef, percentage or ratio).
	 * @param c1 start color
	 * @param c2 end color
	 * @param coef if 0 then c1 is returned, if 1 then c2 is returned, must be within this range
	 * @return mixed color
	 */
	private Color mix(Color c1, Color c2, double coef) {

		Color res = new Color((int)(c1.getRed() + (c2.getRed() - c1.getRed()) * coef),
				(int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * coef),
				(int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * coef));

		return res;
	}


	@PostConstruct
	private void init() {
		memberHelper = new MemberSortingFilteringHelper();
		memberHelper.setModel(new AllMembersModel(em, memberManager));

	}
}
