package com.cxstudio.market.charter;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class PngOutput implements ChartOutput {
	static Logger log = Logger.getLogger(PngOutput.class.getName());
	private File outputFile;

	public PngOutput(File outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public void outputChart(JFreeChart chart, int width, int height) {
		try {
			ChartUtilities.saveChartAsPNG(this.outputFile, chart, width, height);
		} catch (IOException e) {
			log.error("Unable to save file: " + outputFile);
		}

	}

}
