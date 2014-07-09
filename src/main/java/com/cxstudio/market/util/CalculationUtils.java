package com.cxstudio.market.util;

import java.util.List;

import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.updater.model.Trade;

public class CalculationUtils {

	public static float percentChange(float base, float change) {
		if (base == 0) {
			return 0;
		} else {
			return (change - base) / base * 100;
		}
	}

	public static float averageClose(List<Trade> trades) {
		float sum = 0f;
		for (Trade trade : trades) {
			sum += trade.getClose();
		}
		return sum / trades.size();
	}

	public static float averagePerformance(List<Pattern> patterns) {
		float sum = 0f;
		for (Pattern pattern : patterns) {
			sum += pattern.getPerformance();
		}
		return sum / patterns.size();
	}

	public static float confidence(float performance, List<Pattern> patterns) {
		float sum = 0f;
		for (Pattern pattern : patterns) {
			sum += 100 - Math.abs(percentChange(performance, pattern.getPerformance()));
		}
		return sum / patterns.size();
	}

	public static float trend(List<Pattern> patterns) {
		float trendUp = 0f;
		float trendDown = 0f;
		for (Pattern pattern : patterns) {
			if (pattern.getPerformance() > 0)
				trendUp++;
			else if (pattern.getPerformance() < 0)
				trendDown--;
		}
		return (trendUp > Math.abs(trendDown) ? trendUp : trendDown) / patterns.size() * 100;
	}

}
