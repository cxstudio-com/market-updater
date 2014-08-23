package com.cxstudio.market.pattern.model;

public class PatternQualifier {
	float simularityThreashold;
	int minHitCount;
	float confidenceThreashold;
	float trendThreashold;
	float performanceThreashold;

	public void setSimularityThreashold(float simularityThreashold) {
		this.simularityThreashold = simularityThreashold;
	}

	public void setMinHitCount(int minHitCount) {
		this.minHitCount = minHitCount;
	}

	public void setConfidenceThreashold(float confidenceThreashold) {
		this.confidenceThreashold = confidenceThreashold;
	}

	public void setTrendThreashold(float trendThreashold) {
		this.trendThreashold = trendThreashold;
	}

	public void setPerformanceThreashold(float performanceThreashold) {
		this.performanceThreashold = performanceThreashold;
	}

	public float getSimularityThreashold() {
		return simularityThreashold;
	}

	public int getMinHitCount() {
		return minHitCount;
	}

	public float getConfidenceThreashold() {
		return confidenceThreashold;
	}

	public float getTrendThreashold() {
		return trendThreashold;
	}

	public float getPerformanceThreashold() {
		return performanceThreashold;
	}

}
