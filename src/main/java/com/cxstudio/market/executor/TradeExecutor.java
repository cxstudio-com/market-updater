package com.cxstudio.market.executor;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cxstudio.market.pattern.PatternRunnerHelper;
import com.cxstudio.market.pattern.matcher.SimplePercentMatcher;
import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.pattern.model.PatternQualifier;
import com.cxstudio.market.updater.dataprovider.DataProvider;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.util.FixedQueue;

public class TradeExecutor extends TimerTask {
	static Logger log = Logger.getLogger(TradeExecutor.class.getName());
	private Symbol symbol;
	private PatternConfig patternConfig;
	private FixedQueue<Trade> runningTrades;
	private DataProvider dataProvider;
	private SimplePercentMatcher patternMatcher;
	private List<CandidatePattern> modelPatterns;
	private static Timer mainTimer;

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"persistent-context.xml", "application-context.xml");
		PatternRunnerHelper helper = (PatternRunnerHelper) ctx.getBean("runnerHelper");
		Symbol symbol = helper.getSymbol("YHOO");
		PatternConfig config = (PatternConfig) ctx.getBean("patternConfig30Step");
		DataProvider dataProvider = (DataProvider) ctx.getBean("mockDataProvider");
		SimplePercentMatcher patternMatcher = new SimplePercentMatcher();
		PatternQualifier qualifier = new PatternQualifier();
		qualifier.setPerformanceThreashold(0.01F);
		qualifier.setConfidenceThreashold(0.8F);
		qualifier.setSimularityThreashold(0.75F);
		List<CandidatePattern> modelPatterns = helper.getModelPattners(qualifier);

		mainTimer = new Timer();
		TradeExecutor executor = new TradeExecutor(symbol, config, dataProvider, patternMatcher, modelPatterns);
		mainTimer.scheduleAtFixedRate(executor, 0, 60 * 2);
	}

	public TradeExecutor(Symbol symbol, PatternConfig patternConfig, DataProvider dataProvider,
			SimplePercentMatcher patternMatcher, List<CandidatePattern> modelPatterns) {
		this.symbol = symbol;
		this.patternConfig = patternConfig;
		this.runningTrades = new FixedQueue<Trade>(patternConfig.getLength());
		this.dataProvider = dataProvider;
		this.patternMatcher = patternMatcher;
		this.modelPatterns = modelPatterns;
	}

	@Override
	public void run() {
		DataFilter filter = new DataFilter();
		Calendar cal = Calendar.getInstance();
		filter.setStartTime(cal.getTime());
		filter.setInterval(60);
		filter.setLimit(1);

		log.info("TradeExecutor started to run.");
		// retrieve latest trade from online
		List<Trade> trades = dataProvider.retreive(symbol, filter);
		log.trace("Number of trades retreived from online: " + (trades != null ? trades.size() : -1));
		Trade currentTrade;
		if (trades.size() > 0) {
			currentTrade = trades.get(trades.size() - 1);
			runningTrades.add(currentTrade);
			log.trace("New trade added to running trades. Total running trade: " + runningTrades.size());
		} else {
			log.info("No more trade received. Trade execution complete. Symbol: " + symbol.getTicker());
			return;
		}

		float likelihood = 0F;
		if (runningTrades.size() == patternConfig.getLength()) {
			log.debug("Accumalated enough trades to run pattern matching");
			for (Pattern pattern : this.modelPatterns) {
				pattern.setPatternConfig(this.patternConfig);
				likelihood = patternMatcher.match(runningTrades, pattern);
				if (likelihood > 0.7) {
					log.info("Running trades are " + (likelihood * 100) + "% like.");
					log.info(">>>>>>>>>> Current trade : " + currentTrade);
					log.info(">>>>>>>>>> Prediction: " + currentTrade.getClose() * (1 + pattern.getPerformance()));
					mainTimer.cancel();
					return;
				}
			}

		}

	}
}
