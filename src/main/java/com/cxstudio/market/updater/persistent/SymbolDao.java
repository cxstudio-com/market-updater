package com.cxstudio.market.updater.persistent;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.persistent.mapper.SymbolMapper;

public class SymbolDao {
	@Autowired
	SymbolMapper mapper;
	static Logger log = Logger.getLogger(SymbolDao.class.getName());

	public SymbolDao() {
	}

	public Symbol getSymbol(String ticker) throws Exception {
		return mapper.selectSymbolByTicker(ticker);
	}

	public Symbol getSymbol(int symbolId) throws Exception {
		return mapper.selectSymbolById(symbolId);
	}

	public List<Symbol> getAllSymbols(boolean filtered) throws Exception {
		return mapper.selectFilteredSymbols();
	}

	public void setUpdateDate(Symbol symbol) throws Exception {
		mapper.updateSymbol(symbol);
	}

}
