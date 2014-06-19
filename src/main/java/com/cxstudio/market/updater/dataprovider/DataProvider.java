package com.cxstudio.market.updater.dataprovider;

import java.util.List;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public interface DataProvider {
	void connect();
	List<Trade> retreive(Symbol symbol, DataFilter dataFilter);
	void disconnect();
}
