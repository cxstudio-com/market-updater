package com.cxstudio.market.pattern.matcher;

import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.pattern.model.Step;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.util.CalculationUtils;

public class SimplePercentMatcher {
	static Logger log = Logger.getLogger(SimplePercentMatcher.class.getName());

	/**
	 * 
	 * @param sample
	 * @param pattern
	 * @return how much sample and pattern is a like in percentage
	 */
	public float match(List<Trade> sample, Pattern pattern) {
		List<Step> steps = pattern.getSteps();
		PatternConfig config = pattern.getPatternConfig();
		// sanity check
		if (sample.size() < config.getLength()) {
			throw new IllegalArgumentException("Sample size and pattern size mismatch. Sample size: " + sample.size()
					+ ". Pattern size: " + config.getLength());
		}

		Trade baseTrade = sample.get(config.getLength() - 1);
		float totalALike = 0f;
		for (int i = 0; i < config.getLength(); i++) {
			Trade trade = sample.get(i);
			Step step = steps.get(i);
			float sampleChange = CalculationUtils.percentChange(baseTrade.getClose(), trade.getClose());
			// log.debug("Percent difference in step: "
			// + (100 - Math.abs(CalculationUtils.percentChange(sampleChange,
			// step.getChange()))));
			totalALike += 100 - Math.abs(CalculationUtils.percentChange(sampleChange, step.getChange()));
		}
		return totalALike / sample.size();
	}

	public float match(Pattern patternA, Pattern patternB) {
		PatternConfig configA = patternA.getPatternConfig();
		PatternConfig configB = patternB.getPatternConfig();
		if (!configA.equals(configB)) {
			throw new IllegalArgumentException("Incompatible patterns. Unable to match.");
		}

		float totalALike = 0f;
		List<Step> stepsA = patternA.getSteps();
		List<Step> stepsB = patternB.getSteps();
		for (int i = 0; i < stepsA.size(); i++) {
			totalALike += 100 - Math.abs(CalculationUtils.percentChange(stepsA.get(i).getChange(), stepsB.get(i)
					.getChange()));
		}
		return totalALike / stepsA.size();
	}
}
