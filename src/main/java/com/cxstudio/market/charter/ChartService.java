package com.cxstudio.market.charter;

import java.awt.geom.Ellipse2D;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;

public class ChartService {
	static Logger log = Logger.getLogger(ChartService.class.getName());
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
		XYPlot xyplot = chart.getXYPlot();
		xyplot.setRenderer(renderer);
		DateAxis xAxis = (DateAxis) xyplot.getDomainAxis();
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 1, 1, 5, 0);
		SegmentedTimeline segments = new SegmentedTimeline(SegmentedTimeline.HOUR_SEGMENT_SIZE, 7, 17);
		segments.setStartTime(cal.getTimeInMillis());
		xAxis.setTimeline(segments);

		chartOutput.outputChart(chart, 800, 500);
	}

}
