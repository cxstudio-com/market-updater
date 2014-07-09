package com.cxstudio.market.pattern;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cxstudio.market.charter.PatternChartService;
import com.cxstudio.market.charter.SwingOutput;
import com.cxstudio.market.pattern.maker.PercentChangePatternMaker;
import com.cxstudio.market.pattern.matcher.SimplePercentMatcher;
import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.updater.persistent.SymbolDao;
import com.cxstudio.market.updater.persistent.TradeDao;
import com.cxstudio.market.util.CalculationUtils;

public class PatternRunner implements Runnable {
	static Logger log = Logger.getLogger(PatternRunner.class.getName());
	@Autowired
	private SymbolDao symbolDao;
	@Autowired
	private TradeDao tradeDao;
	private Symbol symbol;
	private List<Trade> tradePool;
	private PatternConfig patternConfig;
	private float threashold;
	private int minHitCount;

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"persistent-context.xml", "application-context.xml");
		PatternRunner runner = (PatternRunner) ctx.getBean("patternRunner");
		SymbolDao symbolDao = (SymbolDao) ctx.getBean("symbolDao");
		TradeDao tradeDao = (TradeDao) ctx.getBean("tradeDao");

		Symbol symbol;
		symbol = symbolDao.getSymbol("YHOO");
		runner.setSymbol(symbol);
		runner.setTradePool(tradeDao.getTrades(symbol, new DataFilter()));
		runner.run();

		ctx.close();
	}

	public PatternRunner(PatternConfig patternConfig, float threashold, int minHitCount) throws Exception {
		this.patternConfig = patternConfig;
		this.threashold = threashold;
		this.minHitCount = minHitCount;
	}

	public void run() {
		tradePool = tradeDao.getTrades(symbol, new DataFilter());
		log.info("Running pattern detector on \"" + symbol.getTicker() + "\" with " + tradePool.size() + " trades.");

		List<Pattern> rawPatterns;
		try {
			rawPatterns = makePatterns(tradePool, patternConfig);
		} catch (Exception e) {
			log.error("Unable to make raw patterns from trades.");
			return;
		}

		if (rawPatterns == null || rawPatterns.size() == 0) {
			log.warn("No raw patterns made from trades. Finish!");
			return;
		}
		log.debug("Start processing " + rawPatterns.size() + " raw patterns...");
		SimplePercentMatcher matcher = new SimplePercentMatcher();
		Hashtable<Pattern, CandidatePattern> scoreMap = new Hashtable<Pattern, CandidatePattern>();

		Pattern patternA, patternB;
		for (int i = 0; i < rawPatterns.size(); i++) {
			for (int j = i + 1; j < rawPatterns.size(); j++) {
				patternA = rawPatterns.get(i);
				patternB = rawPatterns.get(j);
				float result = matcher.match(patternA, patternB);
				if (result > threashold) {
					CandidatePattern candidatePattern = scoreMap.containsKey(patternA) ? scoreMap.get(patternA)
							: new CandidatePattern(patternA);
					candidatePattern.getVotingPatterns().add(patternB);
					scoreMap.put(patternA, candidatePattern);
					candidatePattern = scoreMap.containsKey(patternB) ? scoreMap.get(patternB)
							: new CandidatePattern(patternB);
					candidatePattern.getVotingPatterns().add(patternA);
					scoreMap.put(patternB, candidatePattern);
				}
			}
		}

		SwingOutput output = new SwingOutput();
		PatternChartService chart = new PatternChartService(output);

		for (Pattern basePattern : scoreMap.keySet()) {

			CandidatePattern candidate = scoreMap.get(basePattern);
			float averagePerformance = CalculationUtils.averagePerformance(candidate.getVotingPatterns());
			float confidence = CalculationUtils.confidence(candidate.getPerformance(),
					candidate.getVotingPatterns());
			float trend = CalculationUtils.trend(candidate.getVotingPatterns());
			candidate.setAveragePerformance(averagePerformance);
			candidate.setConfidence(confidence);
			candidate.setTrend(trend);

			if (candidate.getVotingPatterns().size() >= this.minHitCount
					&& candidate.getConfidence() > 41) {
				chart.addPattern(basePattern);

				for (Pattern votingPattern : candidate.getVotingPatterns()) {
					averagePerformance += votingPattern.getPerformance();
					chart.addPattern(votingPattern);
				}
				System.out.println(">>>>>>>>>> Pattern score:" + scoreMap.get(basePattern).getVotingPatterns().size());
				System.out.println(">>>>>>>>>>>>>> confidence: " + candidate.getConfidence()
						+ "% average performance: "
						+ candidate.getAveragePerformance() + " trend: " + candidate.getTrend());
				chart.drawChart();
				break;
			}
		}
	}

	List<Pattern> makePatterns(List<Trade> trades, PatternConfig config)
			throws Exception {
		int numOfPatterns = trades.size() - config.getTotalSteps() + 1;
		List<Trade> patternTrades;
		List<Pattern> patterns = new ArrayList<Pattern>(numOfPatterns);
		PercentChangePatternMaker maker = new PercentChangePatternMaker(config);
		for (int i = 0; i < numOfPatterns; i++) {
			patternTrades = trades.subList(i, config.getTotalSteps() + i);
			patterns.add(maker.make(patternTrades));
		}
		return patterns;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public List<Trade> getTradePool() {
		return tradePool;
	}

	public void setTradePool(List<Trade> tradePool) {
		this.tradePool = tradePool;
	}

	public int getMinHitCount() {
		return minHitCount;
	}

	public void setMinHitCount(int minHitCount) {
		this.minHitCount = minHitCount;
	}

}
