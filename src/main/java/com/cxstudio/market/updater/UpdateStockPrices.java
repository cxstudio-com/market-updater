package com.cxstudio.market.updater;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.dataprovider.DataProvider;
import com.cxstudio.market.updater.dataprovider.GoogleFinanceDataRetreiver;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class UpdateStockPrices implements Runnable {
	static final long ALMOST_A_DAY = 23 * 60 * 60 * 1000; // 23 hours
	static final int MARKET_STARTS_HOUR = 6; // 6AM
	static final int MARKET_END_HOUR = 13; // 1PM
	static Logger log = Logger.getLogger(UpdateStockPrices.class.getName());
	private final SymbolDao symbolDao;
	private final TradeDao tradeDao;
	private final DataProvider dataProvider;
	private final UpdateResult updateResult;
	private final Date todayMarketStart;
	private final Date todayMarketEnd;

	public static void main(String[] args) throws Exception {
		SymbolDao symbolDao = new SymbolDao();
		TradeDao tradeDao = new TradeDao();
		DataProvider dataProvider = new GoogleFinanceDataRetreiver();
		UpdateStockPrices updateStockPrices = new UpdateStockPrices(symbolDao, tradeDao, dataProvider);

		log.info("Stock update started.");
		// could be run in multi-thread if need to
		updateStockPrices.run();
		UpdateResult result = updateStockPrices.getUpdateResult();

		log.info("Stock update completed: " + result.toString());

	}

	public UpdateStockPrices(SymbolDao symbolDao, TradeDao tradeDao, DataProvider dataProvider) {
		this.symbolDao = symbolDao;
		this.tradeDao = tradeDao;
		this.dataProvider = dataProvider;
		this.updateResult = new UpdateResult();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, MARKET_STARTS_HOUR);
		this.todayMarketStart = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, MARKET_END_HOUR);
		this.todayMarketEnd = cal.getTime();
	}

	@Override
	public void run() {
		List<Symbol> symbolsToUpdate = null;
		updateResult.startTime = new Date();
		if (!isTimeToRun(updateResult.startTime)) {
			log.warn("Not time to run yet. Current time: " + updateResult.startTime + " Market end time: "
					+ this.todayMarketEnd);
			return;
		}
		try {
			symbolDao.connect();
			tradeDao.connect();
			symbolsToUpdate = symbolDao.getAllSymbols(true);
			if (symbolsToUpdate != null) {
				updateResult.numOfStocksToUpdate = symbolsToUpdate.size();
				dataProvider.connect();
				DataFilter filter = new DataFilter();
				Calendar cal = Calendar.getInstance();
				cal.roll(Calendar.HOUR, -24);
				filter.setStartTime(cal.getTime());
				filter.setInterval(30);
				int counter = 1;
				for (Symbol symbol : symbolsToUpdate) {
					if (needsUpdate(symbol.getLastUpdate())) {
						try {
							log.info("Updating # " + (counter++) + " of " + symbolsToUpdate.size() + ": "
									+ symbol.getTicker());
							List<Trade> trades = dataProvider.retreive(symbol, filter);
							if (trades != null && trades.size() > 0) {
								tradeDao.insertTrades(trades);
								symbol.setLastUpdate(new Date());
								symbolDao.setUpdateDate(symbol);
								updateResult.numOfStocksUpdated++;
								updateResult.numOfTradesUpdated += trades.size();
							}
							log.info(trades.size() + " num of trades retreived for: " + symbol.getTicker());
						} catch (Exception e) {
							log.error("Problem inserting trades for " + symbol.getTicker() + " into DB.");
							updateResult.exception = e;
							updateResult.successful = false;
						}
					} else {
						log.info("Stock " + symbol.getTicker() + " is up to date. Last update: "
								+ symbol.getLastUpdate());
					}
				}
				dataProvider.disconnect();
			}
		} catch (Exception e) {
			updateResult.exception = e;
			updateResult.successful = false;
			log.error("Error when updating stock prices. ", e);
		} finally {
			symbolDao.disconnect();
			tradeDao.disconnect();
		}
		updateResult.endTime = new Date();
	}

	public UpdateResult getUpdateResult() {
		return updateResult;
	}

	private boolean isTimeToRun(Date timeToRun) {
		return timeToRun.after(this.todayMarketEnd);
	}

	private boolean needsUpdate(Date lastUpdate) {
		return lastUpdate.before(this.todayMarketStart);
	}

	private static class UpdateResult {
		Date startTime;
		Date endTime;
		int numOfStocksToUpdate;
		int numOfStocksUpdated;
		long numOfTradesUpdated;
		boolean successful = true;
		Exception exception;

		long getDuration() {
			return endTime.getTime() - startTime.getTime();
		}

		@Override
		public String toString() {
			return "UpdateResult [startTime=" + startTime + ", endTime=" + endTime + ", duration=" + getDuration()
					/ 1000 + ", numOfStocksToUpdate=" + numOfStocksToUpdate + ", numOfStocksUpdated="
					+ numOfStocksUpdated + ", numOfTradesUpdated=" + numOfTradesUpdated + ", successful=" + successful
					+ ", exception=" + exception + "]";
		}

	}

}
