package com.cxstudio.market.pattern.maker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.pattern.model.Step;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.util.CalculationUtils;

public class PercentChangePatternMaker {

	static Logger log = Logger.getLogger(PercentChangePatternMaker.class.getName());
	private PatternConfig patternConfig;

	public PercentChangePatternMaker(PatternConfig patternConfig) {
		this.patternConfig = patternConfig;
	}

	public Pattern make(List<Trade> trades) {
		int totalLength = patternConfig.getTotalSteps();
		Pattern pattern = new Pattern(patternConfig);
		// sanity check, if number of trades = patternConfig.getLength(): trades
		// only have base trades, without performance data
		if (trades.size() != totalLength && trades.size() != patternConfig.getLength()) {
			throw new IllegalArgumentException("Expected # of trades: " + totalLength + ". Actual trades: "
					+ trades.size());
		}
		int baseIdex = patternConfig.getLength() - 1;
		Trade baseTrade = trades.get(baseIdex);
		pattern.setBaseTrade(baseTrade);
		// calculate and set pattern steps
		List<Step> steps = new ArrayList<Step>(patternConfig.getLength());
		for (int i = 0; i < patternConfig.getLength(); i++) {
			log.trace("Calculating percent change on base " + baseTrade.getClose() + " trade# " + i + ":"
					+ trades.get(i).getClose()
					+ " change: " + CalculationUtils.percentChange(baseTrade.getClose(), trades.get(i).getClose()));
			steps.add(new Step(CalculationUtils.percentChange(baseTrade.getClose(),
					trades.get(i).getClose()), i, trades.get(i).getDateTime()));
		}
		pattern.setSteps(steps);

		if (trades.size() == totalLength) {
			// calculate outcome from outcome range trade
			float outcomeSum = 0f;
			for (int i = totalLength - patternConfig.getPredictionRange() - 1; i < totalLength; i++) {
				outcomeSum += CalculationUtils.percentChange(baseTrade.getClose(), trades.get(i).getClose());
			}
			pattern.setPerformance(outcomeSum / (float) patternConfig.getPredictionRange());
		}

		return pattern;
	}

}
