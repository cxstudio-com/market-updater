package com.cxstudio.market.util;

import java.text.NumberFormat;

import com.cxstudio.market.pattern.model.CandidatePattern;

public class FormatUtils {
	static NumberFormat percentFormat = NumberFormat.getPercentInstance();
	static NumberFormat intFormat = NumberFormat.getIntegerInstance();
	static NumberFormat twoDigFloat = NumberFormat.getInstance();
	static {
		twoDigFloat.setMaximumFractionDigits(2);
	}

	public static String formatPatternSpec(CandidatePattern pattern) {
		StringBuffer sb = new StringBuffer();
		sb.append("Confidence: ").append(percentFormat.format(pattern.getConfidence() / 100)).append("\t ");
		sb.append("Trend: ").append(percentFormat.format(pattern.getTrend() / 100)).append("\t ");
		sb.append("Average Performance: ").append(twoDigFloat.format(pattern.getAveragePerformance())).append("\t ");
		sb.append("Voter Count: ").append(pattern.getVotingPatterns().size());
		return sb.toString();
	}
}
