package com.cxstudio.market.pattern;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.charter.PatternChartService;
import com.cxstudio.market.charter.SwingOutput;
import com.cxstudio.market.pattern.maker.PercentChangePatternMaker;
import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class PatternMakerTest {

	static Logger log = Logger.getLogger(PatternMakerTest.class.getName());

	public static void main(String[] args) throws Exception {
		int numOfPatterns = 50;
		SymbolDao symbolDao = new SymbolDao();
		TradeDao tradeDao = new TradeDao();
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE) - 7,
				5, 0);
		PatternConfig config = PatternConfig.get30StepConfig();
		DataFilter dataFilter = new DataFilter();
		dataFilter.setStartTime(cal.getTime());
		dataFilter.setLimit(config.getTotalSteps() + numOfPatterns);

		symbolDao.connect();
		tradeDao.connect();
		Symbol symbol = symbolDao.getSymbol("AAPL");
		List<Trade> trades = tradeDao.getTrades(symbol, dataFilter);
		tradeDao.disconnect();
		symbolDao.disconnect();

		SwingOutput output = new SwingOutput();
		PatternChartService chart = new PatternChartService(output);
		List<Trade> patternTrades;
		for (int i = 0; i < numOfPatterns; i++) {
			patternTrades = trades.subList(i, config.getTotalSteps() + i);
			Pattern pattern = makePattern(patternTrades, config);
			chart.addPattern(pattern);
		}
		chart.drawChart();

	}

	static Pattern makePattern(List<Trade> trades, PatternConfig config) {
		PercentChangePatternMaker maker = new PercentChangePatternMaker(config);
		Pattern pattern = maker.make(trades);
		return pattern;
	}

}
