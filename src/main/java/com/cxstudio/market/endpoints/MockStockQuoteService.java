package com.cxstudio.market.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.persistent.SymbolDao;

@Controller
@RequestMapping("/quote")
public class MockStockQuoteService {
	@Autowired
	SymbolDao symbolDao;

	@RequestMapping(value = "{symbolId}", method = RequestMethod.GET)
	public @ResponseBody
	Symbol getSymbol(@PathVariable int symbolId) {
		return symbolDao.getSymbol(symbolId);
	}

}
