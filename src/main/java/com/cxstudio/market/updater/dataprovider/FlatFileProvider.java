package com.cxstudio.market.updater.dataprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public class FlatFileProvider implements DataProvider {

	static Logger log = Logger.getLogger(FlatFileProvider.class);
	private File dataFile = null;

	public FlatFileProvider(String fileName) {
		dataFile = new File(fileName);
	}

	@Override
	public void connect() {
	}

	@Override
	public List<Trade> retreive(Symbol symbol, DataFilter dataFilter) {
		InputStream in = null;
		LineNumberReader lineReader = null;
		List<Trade> trades = new ArrayList<Trade>();

		try {
			in = new FileInputStream(dataFile);
			lineReader = new LineNumberReader(new InputStreamReader(in));
			String line = null;
			LineDataParser parser = new FlatFileParser(symbol);
			for (line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
				Trade trade = parser.parse(line);
				if (trade != null) {
					trades.add(trade);
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

	@Override
	public void disconnect() {

	}
}
