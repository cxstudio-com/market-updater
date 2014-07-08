package com.cxstudio.market.updater;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.charter.ChartService;
import com.cxstudio.market.charter.SwingOutput;
import com.cxstudio.market.charter.TradePercentChangeSeries;
import com.cxstudio.market.updater.dataprovider.DataProvider;
import com.cxstudio.market.updater.dataprovider.FlatFileProvider;
import com.cxstudio.market.updater.dataprovider.GoogleFinanceDataRetreiver;
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

	public static void main(String[] args) throws Exception {
		// updateSymbol(3117);

		// List<Trade> trades = getFileData(1553,
		// "D:\\MyData\\GoogleDrive\\Project\\GBPUSD1d.txt");
		// drawChart(trades);

		List<Trade> trades = getTrades("AAPL", DataFilter.lastNumOfDays(2));

		drawChart(trades);
		log.info("Num of trades got : " + trades.size());
		log.info("done");
	}

	static List<Trade> getTrades(String ticker, DataFilter filter) {
		SymbolDao symbolDao = new SymbolDao();
		TradeDao tradeDao = new TradeDao();
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
		SymbolDao symbolDao = new SymbolDao();
		TradeDao tradeDao = new TradeDao();

		Symbol symbol = symbolDao.getSymbol(symbolId);

		List<Trade> trades;
		trades = getTodaysGoogleTicker(symbol);

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

	public static List<Trade> getTodaysGoogleTicker(Symbol symbol) {
		DataProvider dataProvider = new GoogleFinanceDataRetreiver();
		DataFilter filter = new DataFilter();
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.HOUR, -24);
		filter.setStartTime(cal.getTime());
		filter.setInterval(30);
		// Symbol symbol = new Symbol("AAPL", "NASDAQ");
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
