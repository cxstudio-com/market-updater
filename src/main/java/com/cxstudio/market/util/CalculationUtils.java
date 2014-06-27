package com.cxstudio.market.util;

import java.util.List;

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
		float sum = 0;
		for (Trade trade : trades) {
			sum += trade.getClose();
		}
		return sum / trades.size();
	}
}
