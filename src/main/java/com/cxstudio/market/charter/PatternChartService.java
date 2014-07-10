package com.cxstudio.market.charter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import com.cxstudio.market.pattern.model.Pattern;
import com.cxstudio.market.pattern.model.PatternConfig;
import com.cxstudio.market.pattern.model.Step;
import com.cxstudio.market.updater.model.Trade;
import com.cxstudio.market.util.CalculationUtils;

public class PatternChartService {
	private ChartOutput chartOutput;
	private XYSeriesCollection dataset;
	private XYLineAndShapeRenderer renderer;
	private String title;
	private String subtitle;

	public PatternChartService(ChartOutput chartOutput) {
		this.chartOutput = chartOutput;
		this.dataset = new XYSeriesCollection();
		this.renderer = new XYLineAndShapeRenderer();
	}

	public void addPattern(Pattern pattern) {
		PatternConfig config = pattern.getPatternConfig();
		PatternStepSeries series = new PatternStepSeries();
		for (Step step : pattern.getSteps()) {
			series.add(step);
		}
		dataset.addSeries(series);
		renderer.setSeriesShape(dataset.indexOf(series), new Ellipse2D.Double(-0.5, -0.5, 1, 1));

		PatternStepSeries performancePoint = new PatternStepSeries();
		Step perfmStep = new Step();
		perfmStep.setChange(pattern.getPerformance());
		perfmStep.setIndex(config.getLength() + config.getStepsToPrediction());
		performancePoint.add(perfmStep);
		dataset.addSeries(performancePoint);
		renderer.setSeriesShape(dataset.indexOf(performancePoint), new Ellipse2D.Double(-3, -3, 6, 6));
		renderer.setSeriesPaint(dataset.indexOf(performancePoint), new Color(30, 120, 245, 32));
	}

	public void addActualTrades(List<Trade> trades, PatternConfig config) {
		PatternStepSeries series = new PatternStepSeries();
		Trade baseTrade = trades.get(config.getLength() - 1);
		for (int i = 0; i < trades.size(); i++) {
			Step step = new Step();
			step.setChange(CalculationUtils.percentChange(baseTrade.getClose(), trades.get(i).getClose()));
			step.setIndex(i);
			series.add(step);
		}
		dataset.addSeries(series);
		renderer.setSeriesStroke(dataset.indexOf(series), new BasicStroke(5));
		renderer.setSeriesPaint(dataset.indexOf(series), new Color(230, 20, 70));
	}

	public void addPredicationTrade(Trade baseTrade, Trade trade, PatternConfig config) {
		PatternStepSeries series = new PatternStepSeries();
		Step step = new Step();
		step.setChange(CalculationUtils.percentChange(baseTrade.getClose(), trade.getClose()));
		step.setIndex(config.getLength() + config.getStepsToPrediction());
		series.add(step);

		dataset.addSeries(series);
		renderer.setSeriesStroke(dataset.indexOf(series), new BasicStroke(5));
		renderer.setSeriesPaint(dataset.indexOf(series), new Color(230, 20, 70));
	}

	public void addAveragePerformance(float performance, PatternConfig config) {
		PatternStepSeries series = new PatternStepSeries();
		Step step = new Step();
		step.setChange(performance);
		step.setIndex(config.getLength() + config.getStepsToPrediction());
		series.add(step);

		dataset.addSeries(series);
		renderer.setSeriesStroke(dataset.indexOf(series), new BasicStroke(5));
		renderer.setSeriesPaint(dataset.indexOf(series), new Color(230, 20, 70));
	}

	public void drawChart() {
		final JFreeChart chart = ChartFactory.createXYLineChart(
				this.title == null ? "Pattern Chart" : this.title,
				"Step", // x axis label
				"Percent Change", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL,
				false, // include legend
				true, // tooltips
				false // urls
				);
		if (this.subtitle != null) {
			TextTitle legendText = new TextTitle(this.subtitle);
			legendText.setPosition(RectangleEdge.BOTTOM);
			legendText.setTextAlignment(HorizontalAlignment.RIGHT);
			legendText.setMaximumLinesToDisplay(10);
			chart.addSubtitle(legendText);
		}

		XYPlot xyplot = chart.getXYPlot();
		xyplot.setRenderer(renderer);

		chartOutput.outputChart(chart, 800, 600);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

}
