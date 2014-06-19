package com.cxstudio.market.charter;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.cxstudio.market.updater.model.Trade;

public class TradeDataSeries extends TimeSeries {
	private static final long serialVersionUID = 8412983236464148290L;

	public TradeDataSeries() {
		super(new Comparable() {

			public int compareTo(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

		});
	}

	public void add(Trade trade) {
		add(new Minute(trade.getDateTime()), trade.getClose());
	}
}
