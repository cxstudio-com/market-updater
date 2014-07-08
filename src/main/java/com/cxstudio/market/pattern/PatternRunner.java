package com.cxstudio.market.pattern;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class PatternRunner implements Runnable {
	static Logger log = Logger.getLogger(PatternRunner.class.getName());
	@Autowired
	private SymbolDao symbolDao;
	@Autowired
	private TradeDao tradeDao;

	private Symbol symbol;
	private List<Trade> tradePool;

	public PatternRunner(String ticker) throws Exception {
		this.symbol = symbolDao.getSymbol(ticker);
	}

	public void run() {
		tradePool = tradeDao.getTrades(symbol, new DataFilter());
		log.info("Running pattern detector on \"" + symbol.getTicker() + "\" with " + tradePool.size() + " trades.");

		for (int i = 0; i < tradePool.size(); i++) {
			for (int j = i + 1; j < tradePool.size(); j++) {

			}
		}
	}

}
