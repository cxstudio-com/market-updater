package com.cxstudio.market.pattern.model;

public class PatternConfig {
	private int interval; // in seconds
	private int length;
	private int stepsToPrediction;
	private int predictionRange;

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

	public int getPredictionRange() {
		return predictionRange;
	}

	public void setPredictionRange(int predictionRange) {
		this.predictionRange = predictionRange;
	}

	public int getTotalSteps() {
		return length + stepsToPrediction + predictionRange;
	}

	public PatternConfig(int interval, int length, int stepsToPrediction, int predictionRange) {
		super();
		this.interval = interval;
		this.length = length;
		this.stepsToPrediction = stepsToPrediction;
		this.predictionRange = predictionRange;
	}

	public static PatternConfig get30StepConfig() {
		return new PatternConfig(60, 30, 5, 3);
	}

}
