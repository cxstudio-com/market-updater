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
	static Logger log = Logger.getLogger(UpdateStockPrices.class.getName());
	private final SymbolDao symbolDao;
	private final TradeDao tradeDao;
	private final DataProvider dataProvider;
	private final UpdateResult updateResult;

	public static void main(String[] args) throws Exception {
		SymbolDao symbolDao = new SymbolDao();
		TradeDao tradeDao = new TradeDao();
		DataProvider dataProvider = new GoogleFinanceDataRetreiver();

		UpdateStockPrices updateStockPrices = new UpdateStockPrices(symbolDao,
				tradeDao, dataProvider);

		log.info("Stock update started.");
		// could be run in multi-thread if need to
		updateStockPrices.run();
		UpdateResult result = updateStockPrices.getUpdateResult();

		log.info("Stock update completed: " + result.toString());

	}

	public UpdateStockPrices(SymbolDao symbolDao, TradeDao tradeDao,
			DataProvider dataProvider) {
		this.symbolDao = symbolDao;
		this.tradeDao = tradeDao;
		this.dataProvider = dataProvider;
		this.updateResult = new UpdateResult();
	}

	@Override
	public void run() {
		List<Symbol> symbolsToUpdate = null;
		updateResult.startTime = System.currentTimeMillis();
		try {
			symbolDao.connect();
			tradeDao.connect();
			symbolsToUpdate = symbolDao.getAllSymbols();
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
					try {
						log.info("Updating # " + (counter++) + " of "
								+ symbolsToUpdate.size() + ": "
								+ symbol.getTicker());
						if (System.currentTimeMillis()
								- symbol.getLastUpdate().getTime() > ALMOST_A_DAY) {
							// last update is more than a day, then update
							List<Trade> trades = dataProvider.retreive(symbol,
									filter);
							tradeDao.insertTrades(trades);
							symbol.setLastUpdate(new Date());
							symbolDao.setUpdateDate(symbol);
							updateResult.numOfStocksUpdated++;
							updateResult.numOfTradesUpdated += trades.size();
							log.info(trades.size()
									+ " num of trades retreived for: "
									+ symbol.getTicker());
						} else { // last update is less than a day, skip
							log.info("Skipping " + symbol.getTicker()
									+ ". Last updated: "
									+ symbol.getLastUpdate());
						}
					} catch (Exception e) {
						log.error("Problem inserting trades for "
								+ symbol.getTicker() + " into DB.");
						updateResult.exception = e;
						updateResult.successful = false;
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
		updateResult.endTime = System.currentTimeMillis();
	}

	public UpdateResult getUpdateResult() {
		return updateResult;
	}

	private static class UpdateResult {
		long startTime;
		long endTime;
		int numOfStocksToUpdate;
		int numOfStocksUpdated;
		long numOfTradesUpdated;
		boolean successful = true;
		Exception exception;

		long getDuration() {
			return endTime - startTime;
		}

		@Override
		public String toString() {
			return "UpdateResult [startTime=" + new Date(startTime)
					+ ", endTime=" + new Date(endTime) + ", duration="
					+ getDuration() / 1000 + ", numOfStocksToUpdate="
					+ numOfStocksToUpdate + ", numOfStocksUpdated="
					+ numOfStocksUpdated + ", numOfTradesUpdated="
					+ numOfTradesUpdated + ", successful=" + successful
					+ ", exception=" + exception + "]";
		}

	}

}
