package com.cxstudio.market.updater.dataprovider;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public class CsvFileLineParser implements LineDataParser {
	private final Symbol symbol;
	static Logger log = Logger.getLogger(CsvFileLineParser.class);

	public CsvFileLineParser(Symbol symbol) {
		this.symbol = symbol;
	}

	/**
	 * "Date","Time","Open","High","Low","Close","Volume"
	 * 12/30/2002,0931,26.55,26.57,26.49,26.55,45924
	 */
	@Override
	public Trade parse(String textline) {
		SimpleDateFormat dateParser = new SimpleDateFormat("MM/dd/yyyy HHmm");
		String[] tokens = textline.split(",");
		String date = tokens[0];
		String time = tokens[1];
		String open = tokens[2];
		String high = tokens[3];
		String low = tokens[4];
		String close = tokens[5];
		String volume = tokens[6];
		Trade trade = null;
		String dateTime = date + " " + time;

		try {
			trade = new Trade();
			trade.setSymbol(symbol);
			trade.setDateTime(dateParser.parse(dateTime));
			trade.setOpen(Float.parseFloat(open));
			trade.setHigh(Float.parseFloat(high));
			trade.setLow(Float.parseFloat(low));
			trade.setClose(Float.parseFloat(close));
			trade.setVolume(Long.parseLong(volume));
		} catch (ParseException | NumberFormatException e) {
			log.error("Fail to parse input line: " + textline);
		}

		log.debug("text line parsed: " + (trade == null ? "null" : trade.toString()));

		return trade;
	}
}
