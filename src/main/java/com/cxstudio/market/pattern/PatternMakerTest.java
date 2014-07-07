package com.cxstudio.market.pattern;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cxstudio.market.charter.PatternChartService;
import com.cxstudio.market.charter.SwingOutput;
import com.cxstudio.market.pattern.maker.PercentChangePatternMaker;
import com.cxstudio.market.pattern.matcher.SimplePercentMatcher;
import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class PatternMakerTest implements Runnable {

	static Logger log = Logger.getLogger(PatternMakerTest.class.getName());
	SymbolDao symbolDao;
	TradeDao tradeDao;

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"persistent-context.xml", "application-context.xml");
		PatternMakerTest patternMaker = (PatternMakerTest) ctx.getBean("patternMakerTest");
		patternMaker.run();
		ctx.close();
	}

	public PatternMakerTest() {

	}

	public void run() {
		float threadshold = 20f;
		// PatternConfig config = PatternConfig.get30StepConfig();
		PatternConfig config = new PatternConfig(60, 30, 5, 3);
		int numOfPatterns = 300;
		String ticker = "YHOO";
		Calendar baseDate = Calendar.getInstance();
		baseDate.set(baseDate.get(Calendar.YEAR),
				baseDate.get(Calendar.MONTH),
				baseDate.get(Calendar.DATE) - 4,
				9, 0);
		SwingOutput output = new SwingOutput();
		PatternChartService chart = new PatternChartService(output);

		try {
			List<Pattern> patterns = getPatterns(ticker, (Calendar) baseDate.clone(), config, numOfPatterns);
			List<Trade> trades = getTrades(ticker, (Calendar) baseDate.clone(), config.getLength());

			Trade predicationTrade = getPredicationTrade(ticker, (Calendar) baseDate.clone(), config);
			chart.addPredicationTrade(trades.get(trades.size() - 1), predicationTrade, config);

			SimplePercentMatcher matcher = new SimplePercentMatcher();
			for (int i = 0; i < numOfPatterns; i++) {
				float alike = matcher.match(trades, patterns.get(i));

				if (alike > threadshold) {
					log.debug("Percent alike: " + alike);
					chart.addPattern(patterns.get(i));
				}
			}
			chart.addActualTrades(trades, config);

			chart.drawChart();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Trade getPredicationTrade(String ticker, Calendar baseDate, PatternConfig config) throws Exception {
		// roll base time forward to the predication time
		baseDate.add(Calendar.SECOND, (config.getInterval() * (config.getLength() + config.getStepsToPrediction())));
		// symbolDao.connect();
		// tradeDao.connect();
		Symbol symbol = symbolDao.getSymbol(ticker);
		DataFilter dataFilter = new DataFilter();
		dataFilter.setStartTime(baseDate.getTime());
		dataFilter.setLimit(1);
		List<Trade> trades = tradeDao.getTrades(symbol, dataFilter);
		// tradeDao.disconnect();
		// symbolDao.disconnect();
		return trades.get(0);
	}

	List<Trade> getTrades(String ticker, Calendar baseDate, int numOfTrades) throws Exception {

		// symbolDao.connect();
		// tradeDao.connect();
		Symbol symbol = symbolDao.getSymbol(ticker);
		DataFilter dataFilter = new DataFilter();
		dataFilter.setStartTime(baseDate.getTime());
		dataFilter.setLimit(numOfTrades);
		List<Trade> trades = tradeDao.getTrades(symbol, dataFilter);
		// tradeDao.disconnect();
		// symbolDao.disconnect();
		return trades;

	}

	List<Pattern> getPatterns(String ticker, Calendar baseDate, PatternConfig config, int numOfPatterns)
			throws Exception {
		baseDate.set(2014, 5, 18, 5, 0);

		DataFilter dataFilter = new DataFilter();
		dataFilter.setStartTime(baseDate.getTime());
		dataFilter.setLimit(config.getTotalSteps() + numOfPatterns);

		log.debug("Start time is: " + dataFilter.getStartTime());
		// symbolDao.connect();
		// tradeDao.connect();
		Symbol symbol = symbolDao.getSymbol(ticker);
		List<Trade> trades = tradeDao.getTrades(symbol, dataFilter);
		// tradeDao.disconnect();
		// symbolDao.disconnect();

		List<Trade> patternTrades;
		List<Pattern> patterns = new ArrayList<Pattern>(numOfPatterns);
		for (int i = 0; i < numOfPatterns; i++) {
			patternTrades = trades.subList(i, config.getTotalSteps() + i);
			Pattern pattern = makePattern(patternTrades, config);
			patterns.add(pattern);
		}
		return patterns;
	}

	Pattern makePattern(List<Trade> trades, PatternConfig config) {
		PercentChangePatternMaker maker = new PercentChangePatternMaker(config);
		Pattern pattern = maker.make(trades);
		return pattern;
	}

	public void setSymbolDao(SymbolDao symbolDao) {
		this.symbolDao = symbolDao;
	}

	public void setTradeDao(TradeDao tradeDao) {
		this.tradeDao = tradeDao;
	}

}
