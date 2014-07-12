package com.cxstudio.market.pattern;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.PatternDao;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class PatternRunnerHelper {
	static Logger log = Logger.getLogger(PatternRunnerHelper.class.getName());
	@Autowired
	private SymbolDao symbolDao;
	@Autowired
	private TradeDao tradeDao;
	@Autowired
	private PatternDao patternDao;

	public Symbol getSymbol(String ticker) {
		return symbolDao.getSymbol(ticker);
	}

	public List<Trade> getTrades(Symbol symbol, DataFilter filter) {
		return tradeDao.getTrades(symbol, filter);
	}

	public void insertPattern(CandidatePattern pattern) {
		patternDao.insertPattern(pattern);
	}
}
