package com.cxstudio.market.charter;

import java.awt.geom.Ellipse2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;

public class ChartService {
	private final TimeSeriesCollection dataset;
	private final ChartOutput chartOutput;

	public ChartService(ChartOutput chartOutput) {
		this.dataset = new TimeSeriesCollection();
		this.chartOutput = chartOutput;
	}

	public void addSeries(TradeDataSeries series) {
		dataset.addSeries(series);
	}

	public void drawChart() {
		// Generate the graph
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Time Series Chart", // Title
				"Time", // x-axis Label
				"Price", // y-axis Label
				dataset, // Dataset
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
				);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesShape(0, new Ellipse2D.Double(-0.5, -0.5, 1, 1));
		renderer.setSeriesShapesVisible(0, true);
		chart.getXYPlot().setRenderer(renderer);

		// ChartUtilities.saveChartAsJPEG(new
		// File("D:\\java\\temp\\chart.jpg"), chart, 500, 300);
		chartOutput.outputChart(chart, 800, 500);
	}
}
