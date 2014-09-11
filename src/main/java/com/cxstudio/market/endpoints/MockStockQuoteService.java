package com.cxstudio.market.endpoints;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
	static int mockProceeder = 0;

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

	// http://www.google.com/finance/getprices?q=RMAX&x=NYSE&ts=-1293726&p=43081&i=30&f=d%2Co%2Ch%2Cl%2Cc%2Cv
	@RequestMapping(value = "getprices", method = RequestMethod.GET)
	public ModelAndView getSymbol(@RequestParam("q") String ticker, @RequestParam("x") String market,
			@RequestParam("i") int interval,
			@RequestParam("ts") long ts) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("trades");
		DataFilter filter = new DataFilter();
		filter.setEndTime(new Date(ts * 1000));
		filter.setLimit(1000);
		filter.setOrder("desc");
		Symbol symbol = symbolDao.getSymbol(ticker);
		List<Trade> trades = tradeDao.getTrades(symbol, filter);
		if (trades != null) {
			Trade trade = trades.get(mockProceeder++);
			mv.addObject("time", trade.getDateTime().getTime() / 1000);
			mv.addObject("close", trade.getClose());
			mv.addObject("high", trade.getHigh());
			mv.addObject("low", trade.getLow());
			mv.addObject("open", trade.getOpen());
			mv.addObject("volume", trade.getVolume());
		}
		return mv;

	}

}
