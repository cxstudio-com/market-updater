package com.cxstudio.market.endpoints;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

@Controller
@RequestMapping("/quote")
public class MockStockQuoteService {
	@Autowired
	SymbolDao symbolDao;
	@Autowired
	TradeDao tradeDao;

	@RequestMapping(value = "{symbolId}", method = RequestMethod.GET)
	public @ResponseBody
	Symbol getSymbol(@PathVariable int symbolId) {
		return symbolDao.getSymbol(symbolId);
	}

	@RequestMapping(value = "{symbolId}/trades", method = RequestMethod.GET)
	public @ResponseBody
	List<Trade> getTodaysTrades(@PathVariable int symbolId) {
		Symbol symbol = symbolDao.getSymbol(symbolId);
		DataFilter filter = new DataFilter();
		Calendar now = Calendar.getInstance();
		Calendar yesterday = (Calendar) now.clone();
		yesterday.roll(Calendar.DAY_OF_YEAR, -1);

		// filter.setStartTime(now.getTime());
		filter.setEndTime(yesterday.getTime());
		return tradeDao.getTrades(symbol, filter);
	}
}
