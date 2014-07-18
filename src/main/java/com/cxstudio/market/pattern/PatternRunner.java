package com.cxstudio.market.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cxstudio.market.charter.PatternChartService;
import com.cxstudio.market.charter.PngOutput;
import com.cxstudio.market.pattern.maker.PercentChangePatternMaker;
import com.cxstudio.market.pattern.matcher.SimplePercentMatcher;
import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.updater.model.DataFilter;
import com.cxstudio.market.updater.model.Symbol;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.util.CalculationUtils;
import com.cxstudio.market.util.FormatUtils;

public class PatternRunner implements Runnable {
	static Logger log = Logger.getLogger(PatternRunner.class.getName());
	@Autowired
	private PatternRunnerHelper helper;
	private Symbol symbol;
	private PatternConfig patternConfig;
	private PatternQualifier qualifier;
	private String imageOutputFolder;
	private String runnerId;
	int patternGenerated;

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"persistent-context.xml", "application-context.xml");

		PatternRunnerHelper helper = (PatternRunnerHelper) ctx.getBean("runnerHelper");
		List<Symbol> symbols = helper.getRunnableSymbols();
		// Running pattern runner in a thread pool
		@SuppressWarnings("unchecked")
		Map<String, String> prop = (Map<String, String>) ctx.getBean("marketUpdateProperties");
		int threadPoolSize = Integer.parseInt(prop.get("PatternRunner.threadPoolSize"));
		String runnerId = PatternRunnerHelper.getRunnerId();

		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
		int counter = 0;
		long startTime = System.currentTimeMillis();
		for (Symbol symbol : symbols) {
			log.info("Starting new thread for: " + symbol.getTicker() + " " + counter + " of " + symbols.size());
			PatternRunner runner = (PatternRunner) ctx.getBean("patternRunner");
			runner.setRunnerId(runnerId);
			runner.setSymbol(symbol);
			executor.execute(runner);
			counter++;
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		long secondsUsed = (System.currentTimeMillis() - startTime) / 1000;
		log.info("Finished all threads. " + secondsUsed + " seconds.");
		// ctx.close();
	}

	public PatternRunner(PatternConfig patternConfig, PatternQualifier qualifier, String imageOutputFolder)
			throws Exception {
		this.patternConfig = patternConfig;
		this.qualifier = qualifier;
		this.imageOutputFolder = imageOutputFolder;
	}

	public void run() {
		log.info("Thread [" + Thread.currentThread().getId() + "] is running pattern detector on \""
				+ this.symbol.getTicker() + "\".");
		long startTime = System.currentTimeMillis();
		List<Trade> tradePool = this.helper.getTrades(symbol, new DataFilter());
		// make raw patterns
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
		// Match each raw pattern against every other raw pattern
		Pattern patternA, patternB;
		for (int i = 0; i < rawPatterns.size(); i++) {
			for (int j = i + 1; j < rawPatterns.size(); j++) {
				patternA = rawPatterns.get(i);
				patternB = rawPatterns.get(j);
				float result = matcher.match(patternA, patternB);
				if (result > this.qualifier.simularityThreashold) {
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
		// Qualifying pattern matching results. Only good patterns get saved
		// into DB
		for (Pattern basePattern : scoreMap.keySet()) {
			CandidatePattern candidate = scoreMap.get(basePattern);
			float averagePerformance = CalculationUtils.averagePerformance(candidate.getVotingPatterns());
			float confidence = CalculationUtils.confidence(candidate.getPerformance(),
					candidate.getVotingPatterns());
			float trend = CalculationUtils.trend(candidate.getVotingPatterns());
			candidate.setAveragePerformance(averagePerformance);
			candidate.setConfidence(confidence);
			candidate.setTrend(trend);

			// Qualifying candidate patterns
			if (candidate.getVotingPatterns().size() >= this.qualifier.minHitCount
					&& candidate.getConfidence() > this.qualifier.confidenceThreashold
					&& candidate.getAveragePerformance() > this.qualifier.performanceThreashold
					&& candidate.getTrend() > this.qualifier.trendThreashold) {
				// Qualified candidate pattern will be inserted to DB, and ID
				// will be set on the object
				this.helper.insertPattern(candidate);

				// Also output a graph for each qualified candidate pattern.
				PngOutput output = new PngOutput(getPngFile(candidate));
				PatternChartService chart = new PatternChartService(output);
				chart.setTitle("Pattern " + candidate.getPatternId());
				chart.addPattern(basePattern);
				chart.addAveragePerformance(candidate.getAveragePerformance(), candidate.getPatternConfig());

				StringBuilder sb = new StringBuilder();
				sb.append(FormatUtils.formatPatternSpec(candidate));
				for (Pattern votingPattern : candidate.getVotingPatterns()) {
					averagePerformance += votingPattern.getPerformance();
					chart.addPattern(votingPattern);
					sb.append("\n").append("Voting pattern from: ");
					sb.append(votingPattern.getBaseTrade().getDateTime());
				}
				chart.setSubtitle(sb.toString());
				log.debug("Outputing pattern [" + candidate.getPatternId() + "]:"
						+ FormatUtils.formatPatternSpec(candidate));
				chart.drawChart();
				patternGenerated++;
			}
		}
		long secondsUsed = (System.currentTimeMillis() - startTime) / 1000;
		log.info("Pattern detector completed on " + symbol.getTicker() + ". " + patternGenerated
				+ " patterns generated." + secondsUsed + " seconds.");
	}

	private File getPngFile(CandidatePattern candidate) {
		File folder = new File(this.imageOutputFolder + File.separator + this.runnerId);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = folder.getAbsolutePath() + File.separator + candidate.getPatternId() + ".png";
		return new File(fileName);
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

	public String getRunnerId() {
		return runnerId;
	}

	public void setRunnerId(String runnerId) {
		this.runnerId = runnerId;
	}

	public static class PatternQualifier {
		float simularityThreashold;
		int minHitCount;
		float confidenceThreashold;
		float trendThreashold;
		float performanceThreashold;

		public void setSimularityThreashold(float simularityThreashold) {
			this.simularityThreashold = simularityThreashold;
		}

		public void setMinHitCount(int minHitCount) {
			this.minHitCount = minHitCount;
		}

		public void setConfidenceThreashold(float confidenceThreashold) {
			this.confidenceThreashold = confidenceThreashold;
		}

		public void setTrendThreashold(float trendThreashold) {
			this.trendThreashold = trendThreashold;
		}

		public void setPerformanceThreashold(float performanceThreashold) {
			this.performanceThreashold = performanceThreashold;
		}

	}

}
