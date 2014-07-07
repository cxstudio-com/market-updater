package com.cxstudio.market.updater.persistent;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.mapper.TradeMapper;

public class TradeDao {
	static Logger log = Logger.getLogger(TradeDao.class.getName());
	@Autowired
	TradeMapper mapper;

	public TradeDao() {
	}

	public List<Trade> getTrades(Symbol symbol, DataFilter filter) {
		return mapper.selectTrades(symbol, filter);
	}

	public void insertTrades(List<Trade> trades) throws Exception {
		if (trades != null && trades.size() > 0)
			mapper.insertTrades(trades);
	}

	public void insertTempTrades(List<Trade> trades) throws Exception {
		if (trades != null && trades.size() > 0)
			mapper.insertTempTrades(trades);
	}
}
