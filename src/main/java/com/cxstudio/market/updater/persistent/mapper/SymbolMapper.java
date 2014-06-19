package com.cxstudio.market.updater.persistent.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.cxstudio.market.updater.model.Symbol;

public interface SymbolMapper {
	@Select("SELECT * FROM symbol")
	@Results({ @Result(property = "symbolId", column = "symbol_id"),
			@Result(property = "lastUpdate", column = "last_update") })
	List<Symbol> selectAllSymbols();

	@Select("SELECT * FROM symbol WHERE collectable=1")
	@Results({ @Result(property = "symbolId", column = "symbol_id"),
			@Result(property = "lastUpdate", column = "last_update") })
	List<Symbol> selectFilteredSymbols();

	@Select("SELECT * FROM symbol WHERE symbol_id = #{symbolId}")
	@Results({ @Result(property = "symbolId", column = "symbol_id"),
			@Result(property = "lastUpdate", column = "last_update") })
	Symbol selectSymbolById(int symbolId);

	@Update("Update symbol Set last_update = #{symbol.lastUpdate} WHERE symbol_id = #{symbol.symbolId}")
	void updateSymbol(@Param("symbol") Symbol symbol);

}
