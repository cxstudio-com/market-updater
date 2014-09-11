package com.cxstudio.market.updater.dataprovider;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public class GoogleFinanceDataParser implements LineDataParser {
	static Logger log = Logger.getLogger(GoogleFinanceDataParser.class);
	private final Context context;

	public GoogleFinanceDataParser(Symbol symbol) {
		this.context = new Context(symbol);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cxstudio.market.updater.dataprovider.LineDataParser#parse(java.lang
	 * .String)
	 */
	public Trade parse(String textline) {
		return parse(textline, -1);
	}

	public Trade parse(String textline, int lineNumber) {
		Trade trade = null;
		if (lineNumber >= 0) {
			context.lineNumber = lineNumber;
		} else {
			context.lineNumber++;
		}
		String[] values = textline.split("=");
		String[] dataAry = null;
		switch (context.lineNumber) {
		case 0: // EXCHANGE
		case 1: // MARKET_OPEN_MINUTE
		case 2: // MARKET_CLOSE_MINUTE
			break;
		case 3: // INTERVAL
			context.interval = Integer.parseInt(values[1]);
			break;
		case 4: // COLUMNS
		case 5: // DATA
			break;
		case 6: // TIMEZONE_OFFSET
			context.timezoneOffset = Integer.parseInt(values[1]) * 1000;
			break;
		default: // rest of the lines
			dataAry = textline.split(",");
			if (dataAry.length > 0) {
				int timeOffset = 0;
				if (dataAry[0].startsWith("a")) {
					// first line of the actual data. ex:
					// "a1401975000,646.21,646.31,646.01,646.2,82798"
					String initDateStr = dataAry[0].substring(1);
					context.initialDate = Long.parseLong(initDateStr) * 1000;
				} else {
					timeOffset = context.interval * Integer.parseInt(dataAry[0]) * 1000;
				}
				trade = new Trade(context.symbol);
				trade.setDateTime(new Date(context.initialDate + context.timezoneOffset + timeOffset));
				trade.setClose(Float.parseFloat(dataAry[1]));
				trade.setHigh(Float.parseFloat(dataAry[2]));
				trade.setLow(Float.parseFloat(dataAry[3]));
				trade.setOpen(Float.parseFloat(dataAry[4]));
				trade.setVolume(Long.parseLong(dataAry[5]));
			}
		}

		log.trace("Parsing line [" + context.lineNumber + "]: " + textline);
		log.trace("result: " + (trade == null ? "null" : trade.toString()));
		return trade;
	}

	public static class Context {
		Symbol symbol;
		int lineNumber = -1;
		long initialDate;
		int timezoneOffset;
		int interval = 0;
		int dataSeriesIndex = 0;

		public Context(Symbol symbol) {
			this.symbol = symbol;
		}
	}
}
