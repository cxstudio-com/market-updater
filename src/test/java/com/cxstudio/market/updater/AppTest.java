package com.cxstudio.market.updater;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cxstudio.market.charter.ChartService;
import com.cxstudio.market.charter.SwingOutput;
import com.cxstudio.market.charter.TradePercentChangeSeries;
import com.cxstudio.market.updater.dataprovider.DataProvider;
import com.cxstudio.market.updater.dataprovider.FlatFileProvider;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

/**
 * Hello world!
 * 
 */
public class AppTest {
	static Logger log = Logger.getLogger(AppTest.class.getName());
	static SymbolDao symbolDao;
	static TradeDao tradeDao;
	static DataProvider dataProvider;

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"persistent-context.xml", "application-context.xml");
		symbolDao = (SymbolDao) ctx.getBean("symbolDao");
		tradeDao = (TradeDao) ctx.getBean("tradeDao");
		dataProvider = (DataProvider) ctx.getBean("dataProvider");
		LogManager.getRootLogger().setLevel(Level.DEBUG);
		LogManager.getLogger("com.cxstudio.market.updater").setLevel(Level.DEBUG);

		Calendar startTime = Calendar.getInstance();
		startTime.set(2014, 6, 21, 6, 0, 0);
		Calendar endTime = Calendar.getInstance();
		// endTime.set(2014, 6, 21, 4, 0, 0);
		DataFilter filter = new DataFilter();
		filter.setInterval(30);
		filter.setStartTime(startTime.getTime());
		filter.setEndTime(endTime.getTime());

		List<Trade> trades = getTodaysGoogleTicker(symbolDao.getSymbol("AAPL"), filter);

		drawChart(trades);
		log.info("Num of trades got : " + trades.size());
		log.info("done");
	}

	static List<Trade> getTrades(String ticker, DataFilter filter) {

		Symbol symbol;
		List<Trade> trades = null;
		try {
			symbol = symbolDao.getSymbol(ticker);
			log.info("Symbol: " + symbol);
			trades = tradeDao.getTrades(symbol, filter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return trades;

	}

	static void updateSymbol(int symbolId) throws Exception {

		Symbol symbol = symbolDao.getSymbol(symbolId);

		List<Trade> trades;
		trades = getTodaysGoogleTicker(symbol, new DataFilter());

		tradeDao.insertTempTrades(trades);

	}

	public static List<Trade> getFileData(int symbolId, String fileName) throws Exception {
		SymbolDao symbolDao = new SymbolDao();
		Symbol symbol = symbolDao.getSymbol(symbolId);
		DataProvider dataProvider = new FlatFileProvider(fileName);
		DataFilter filter = new DataFilter();
		// Symbol symbol = new Symbol("AAPL", "NASDAQ");
		log.info("Start retreiving... : " + symbol.toString());
		List<Trade> trades = dataProvider.retreive(symbol, filter);
		log.info("Done retreiving.");
		return trades;
	}

	public static List<Trade> getTodaysGoogleTicker(Symbol symbol, DataFilter filter) {
		log.info("Start retreiving... : " + symbol.toString());
		List<Trade> trades = dataProvider.retreive(symbol, filter);
		log.info("Done retreiving.");
		return trades;
	}

	static void drawChart(List<Trade> trades) {
		SwingOutput output = new SwingOutput();
		ChartService chart = new ChartService(output);
		TradePercentChangeSeries series = new TradePercentChangeSeries();
		for (Trade trade : trades) {
			log.debug("trade.time: " + trade.getDateTime() + " trade.close: " + trade.getClose());
			series.add(trade);
		}
		chart.addSeries(series);
		chart.drawChart();
	}
}
