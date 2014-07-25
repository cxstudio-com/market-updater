package com.cxstudio.market.updater.dataprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;

public class GoogleFinanceDataRetreiver implements DataProvider {
	static Logger log = Logger.getLogger(GoogleFinanceDataRetreiver.class);

	@Override
	public void connect() {

	}

	@Override
	public List<Trade> retreive(Symbol symbol, DataFilter dataFilter) {
		List<Trade> trades = new ArrayList<Trade>();
		log.trace("Retreiving data for : " + symbol.getTicker());
		URI uri = buildUri(symbol, dataFilter);
		HttpGet httpget = new HttpGet(uri);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				log.trace("Parsing response for: " + symbol.getTicker());
				InputStream in = entity.getContent();
				try {
					LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(in));
					String line = null;
					LineDataParser parser = new GoogleFinanceDataParser(symbol);
					for (line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
						log.trace("Prasing line: " + line);
						Trade trade = parser.parse(line);
						if (trade != null) {
							trades.add(trade);
						}
					}
				} catch (IllegalArgumentException ex) {
					log.error("Failed to parse response data.", ex);
				} finally {
					in.close();
				}
			}
		} catch (ClientProtocolException e) {
			log.error("Protocol error while retreiving " + symbol.getTicker() + " from Google.");
		} catch (IOException e) {
			log.error("IO error while retreiving " + symbol.getTicker() + " from Google.");
		} finally {
			try {
				response.close();
			} catch (IOException ignore) {
			}
		}

		return trades;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	/**
	 * ex: http://www.google.com/finance/getprices?q=RMAX&x=NYSE&ts=-1293726&p=
	 * 43081&i=30&f=d%2Co%2Ch%2Cl%2Cc%2Cv
	 * 
	 * @param symbol
	 * @param filter
	 * @return
	 * @throws IllegalArgumentException
	 */
	private URI buildUri(Symbol symbol, DataFilter filter) throws IllegalArgumentException {
		URI uri = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http").setHost("www.google.com").setPath("/finance/getprices");
			builder.setParameter("q", symbol.getTicker());
			builder.setParameter("x", fixExchangeCode(symbol.getExchange()));
			long startTime = filter.getStartTime() != null ? filter.getStartTime().getTime() : System
					.currentTimeMillis();
			long endTime = filter.getEndTime() != null ? filter.getEndTime().getTime() : System
					.currentTimeMillis();
			int interval = filter.getInterval() > 0 ? filter.getInterval() : 60;
			long periods = (endTime - startTime) / 1000 / 60 / 60 / 24;

			periods = (periods > 1) ? periods : 1;
			builder.addParameter("ts", String.valueOf(endTime));
			builder.addParameter("p", periods + "d");
			builder.addParameter("i", String.valueOf(interval));
			builder.addParameter("f", "d,o,h,l,c,v");
			uri = builder.build();
			log.debug("Built Google API URI: " + uri.toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Unable to create request: illegal arguments.");
		}
		return uri;
	}

	private String fixExchangeCode(String exchange) {
		if ("AMEX".equals(exchange)) {
			return "NYSEMKT";
		} else {
			return exchange;
		}
	}

}
