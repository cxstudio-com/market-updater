package com.cxstudio.market.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cxstudio.market.updater.dataprovider.CsvFileLineParser;
import com.cxstudio.market.updater.dataprovider.LineDataParser;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;

public class CsvDataImporter {
	private final SymbolDao symbolDao;
	private final TradeDao tradeDao;
	static Logger log = Logger.getLogger(CsvDataImporter.class.getName());
	private final int BATCH_SIZE = 2000;

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"persistent-context.xml", "application-context.xml");
		CsvDataImporter csvDataImporter = (CsvDataImporter) ctx.getBean("csvDataImporter");
		String dirName = args[0];
		log.info("Starting to import stock data from directory " + dirName);
		csvDataImporter.processDir(dirName);
	}

	public CsvDataImporter(SymbolDao symbolDao, TradeDao tradeDao) {
		this.symbolDao = symbolDao;
		this.tradeDao = tradeDao;
	}

	private void processDir(String dirName) {
		File directory = new File(dirName);
		File[] files = getFiles(directory);
		for (File file : files) {
			String fileName = file.getName();
			String tickerName = fileName.substring(0, fileName.lastIndexOf("."));
			log.info("Processing file " + fileName);
			Symbol symbol = symbolDao.getSymbol(tickerName);
			if (symbol != null) {
				List<Trade> trades = parseFile(file, symbol);
				if (trades == null || trades.size() == 0) {
					log.warn("No trades are parsed!");
					continue;
				}
				try {
					log.info("Inserting " + trades.size() + " into database.");
					System.out.print("Inserting to db: ");
					for (int i = 0; i < trades.size(); i += BATCH_SIZE) {
						int tail = (i + BATCH_SIZE) > trades.size() ? trades.size() : i + BATCH_SIZE;
						System.out.print(".");
						tradeDao.insertTrades(trades.subList(i, tail - 1));
					}
					String newFileName = file.getAbsolutePath() + ".done";
					file.renameTo(new File(newFileName));
					log.info("Complete processing file " + file.getName() + ". Renamed to " + newFileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				log.warn("Ticker symbol " + tickerName + " is not found. Skip parsing.");
			}
		}

	}

	private File[] getFiles(File directory) {
		File[] files = directory.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				if (name.toLowerCase().endsWith("txt")) {
					return true;
				} else {
					return false;
				}
			}
		});
		return files;
	}

	private List<Trade> parseFile(File dataFile, Symbol symbol) {
		List<Trade> trades = new ArrayList<Trade>();

		InputStream in = null;
		LineNumberReader lineReader = null;

		try {
			in = new FileInputStream(dataFile);
			lineReader = new LineNumberReader(new InputStreamReader(in));
			String line = lineReader.readLine();// skip first line
			LineDataParser parser = new CsvFileLineParser(symbol);
			for (line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
				Trade trade = parser.parse(line);
				if (trade != null) {
					trades.add(trade);
					log.debug("Trade parsed: " + trade.toString());
				}
			}
			lineReader.close();
		} catch (FileNotFoundException e) {
			log.error("Data file: " + dataFile.getName() + " doesn't exist.");
		} catch (IOException e) {
			log.error("Unable to read data file: " + dataFile.getName(), e);
		} finally {
			try {
				in.close();
			} catch (Exception ignore) {
			}
		}

		return trades;
	}
}
