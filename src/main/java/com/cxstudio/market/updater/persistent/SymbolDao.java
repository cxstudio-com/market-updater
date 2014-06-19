package com.cxstudio.market.updater.persistent;

import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.persistent.mapper.SymbolMapper;

public class SymbolDao extends AbstractDao {
	SymbolMapper mapper;
	static Logger log = Logger.getLogger(SymbolDao.class.getName());

	public SymbolDao() {
		init();
	}

	@Override
	public void connect() {
		session = sqlSessionFactory.openSession(true);
		mapper = session.getMapper(SymbolMapper.class);
	}

	public Symbol getSymbol(int symbolId) throws Exception {
		return mapper.selectSymbolById(symbolId);
	}

	public List<Symbol> getAllSymbols() throws Exception {
		return mapper.selectAllSymbols();
	}

	public void setUpdateDate(Symbol symbol) throws Exception {
		mapper.updateSymbol(symbol);
	}
}
