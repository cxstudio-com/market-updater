package com.cxstudio.market.pattern.maker;

import java.util.ArrayList;
import java.util.List;

import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.pattern.model.Step;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.util.CalculationUtils;

public class PercentChangePatternMaker {
	private PatternConfig patternConfig;

	public PercentChangePatternMaker(PatternConfig patternConfig) {
		this.patternConfig = patternConfig;
	}

	public Pattern make(List<Trade> trades) {
		int totalLength = patternConfig.getLength() + patternConfig.getStepsToPrediction();
		Pattern pattern = new Pattern(patternConfig);

		if (trades.size() != totalLength) {
			throw new IllegalArgumentException("Expected trade length: " + totalLength + ". Actual length: "
					+ trades.size());
		}
		int baseIdex = patternConfig.getLength() - 1;
		Trade baseTrade = trades.get(baseIdex);
		List<Trade> outcomeTrades = trades.subList(totalLength - patternConfig.getPredictionRange() - 1,
				totalLength - 1);
		List<Step> steps = new ArrayList<Step>(patternConfig.getLength());
		for (int i = 0; i < patternConfig.getLength(); i++) {
			steps.add(new Step(CalculationUtils.percentChange(baseTrade.getClose(), trades.get(i).getClose()), i));
		}
		pattern.setSteps(steps);

		float outcomeSum = 0;
		for (int i = totalLength - patternConfig.getPredictionRange() - 1; i < patternConfig.getPredictionRange(); i++) {
			outcomeSum += CalculationUtils.percentChange(baseTrade.getClose(), trades.get(i).getClose());
		}

		pattern.setPerformance(outcomeSum / patternConfig.getPredictionRange());

		return pattern;
	}

}
