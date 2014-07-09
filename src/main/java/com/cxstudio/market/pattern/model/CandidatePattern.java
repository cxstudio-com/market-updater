package com.cxstudio.market.pattern.model;

import java.util.ArrayList;
import java.util.List;

import com.cxstudio.market.updater.model.Symbol;

public class CandidatePattern extends Pattern {
	private long patternId;
	private Symbol symbol;
	private float confidence;
	private float averagePerformance;
	private List<Pattern> votingPatterns;
	private float trend;

	public CandidatePattern(Pattern basePattern) {
		super(basePattern.getPatternConfig());
		this.setPerformance(basePattern.getPerformance());
		this.setSteps(basePattern.getSteps());
		this.votingPatterns = new ArrayList<Pattern>();
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	public long getPatternId() {
		return patternId;
	}

	public void setPatternId(long patternId) {
		this.patternId = patternId;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public float getAveragePerformance() {
		return averagePerformance;
	}

	public void setAveragePerformance(float averagePerformance) {
		this.averagePerformance = averagePerformance;
	}

	public List<Pattern> getVotingPatterns() {
		return votingPatterns;
	}

	public void setVotingPatterns(List<Pattern> votingPatterns) {
		this.votingPatterns = votingPatterns;
	}

	public float getTrend() {
		return trend;
	}

	public void setTrend(float trend) {
		this.trend = trend;
	}

}
