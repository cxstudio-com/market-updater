package com.cxstudio.market.pattern.model;

import java.util.List;

public class Pattern {
	private PatternConfig patternConfig;
	private float performance;
	private List<Step> steps;

	public Pattern(PatternConfig patternConfig) {
		this.patternConfig = patternConfig;
	}

	public PatternConfig getPatternConfig() {
		return patternConfig;
	}

	public void setPatternConfig(PatternConfig patternConfig) {
		this.patternConfig = patternConfig;
	}

	public float getPerformance() {
		return performance;
	}

	public void setPerformance(float performance) {
		this.performance = performance;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

}
