package com.cxstudio.market.charter;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.cxstudio.market.updater.model.Trade;

public class TradePercentChangeSeries extends TimeSeries {
	Trade baseTrade;

	private static final long serialVersionUID = 8412983236464148290L;

	public TradePercentChangeSeries() {
		super(new Comparable() {

			public int compareTo(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

		});
	}

	public void add(Trade trade) {
		add(new Minute(trade.getDateTime()), percentChange(trade));
		if (baseTrade == null) {
			baseTrade = trade;
		}
	}

	private float percentChange(Trade trade) {
		if (baseTrade == null) {
			return 0;
		} else {
			float previous = baseTrade.getClose();
			float current = trade.getClose();
			return (current - previous) / previous * 100;
		}
	}

}
