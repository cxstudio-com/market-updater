package com.cxstudio.market.pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class PatternRunnerHelper {
	static Logger log = Logger.getLogger(PatternRunner.class.getName());
	@Autowired
	private SymbolDao symbolDao;
	@Autowired
	private TradeDao tradeDao;

}
