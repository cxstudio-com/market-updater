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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patternConfig == null) ? 0 : patternConfig.hashCode());
		result = prime * result + Float.floatToIntBits(performance);
		result = prime * result + ((steps == null) ? 0 : steps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pattern other = (Pattern) obj;
		if (patternConfig == null) {
			if (other.patternConfig != null)
				return false;
		} else if (!patternConfig.equals(other.patternConfig))
			return false;
		if (Float.floatToIntBits(performance) != Float.floatToIntBits(other.performance))
			return false;
		if (steps == null) {
			if (other.steps != null)
				return false;
		} else if (!steps.equals(other.steps))
			return false;
		return true;
	}

}
