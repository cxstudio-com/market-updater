package com.cxstudio.market.pattern.model;

public class PatternConfig {
	private int interval;
	private int length;
	private int stepsToPrediction;
	private int predictionRange;
	private float performance;

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStepsToPrediction() {
		return stepsToPrediction;
	}

	public void setStepsToPrediction(int stepsToPrediction) {
		this.stepsToPrediction = stepsToPrediction;
	}

	public float getPerformance() {
		return performance;
	}

	public void setPerformance(float performance) {
		this.performance = performance;
	}

	public int getPredictionRange() {
		return predictionRange;
	}

	public void setPredictionRange(int predictionRange) {
		this.predictionRange = predictionRange;
	}

}
