package com.cxstudio.market.updater.persistent;

import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.mapper.TradeMapper;

public class TradeDao extends AbstractDao {
	static Logger log = Logger.getLogger(TradeDao.class.getName());
	TradeMapper mapper;

	public TradeDao() {
		init();
	}

	@Override
	public void connect() {
		session = sqlSessionFactory.openSession(true);
		mapper = session.getMapper(TradeMapper.class);
	}

	public List<Trade> getTrades(Symbol symbol, DataFilter filter) {
		return mapper.selectTrades(symbol, filter);
	}

	public void insertTrades(List<Trade> trades) throws Exception {
		if (trades != null && trades.size() > 0)
			mapper.insertTrades(trades);
	}
}
