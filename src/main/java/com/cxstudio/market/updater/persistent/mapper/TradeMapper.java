package com.cxstudio.market.updater.persistent.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public interface TradeMapper {
	List<Trade> selectTrades(@Param("symbol") Symbol symbol, @Param("filter") DataFilter filter);

	void insertTrades(@Param("trades") List<Trade> trades);

	void insertTempTrades(@Param("trades") List<Trade> trades);
}
