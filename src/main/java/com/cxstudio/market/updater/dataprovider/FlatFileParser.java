package com.cxstudio.market.updater.dataprovider;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public class FlatFileParser implements LineDataParser {
	private final Symbol symbol;
	static Logger log = Logger.getLogger(FlatFileParser.class);

	public FlatFileParser(Symbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public Trade parse(String textline) {
		SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMddHHmmss");
		String[] tokens = textline.split(",");
		String date = tokens[0];
		String bid = tokens[1];
		String ask = tokens[2];

		Trade trade = null;

		try {
			trade = new Trade();
			trade.setSymbol(symbol);
			trade.setDateTime(dateParser.parse(date));
			trade.setBid(Float.parseFloat(bid));
			trade.setAsk(Float.parseFloat(ask));
			trade.setClose((trade.getAsk() + trade.getBid()) / 2);
		} catch (ParseException | NumberFormatException e) {
			log.error("Fail to parse input line: " + textline);
		}

		log.debug("text line parsed: " + (trade == null ? "null" : trade.toString()));

		return trade;
	}
}
