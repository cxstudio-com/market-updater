package com.cxstudio.market.charter;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class SwingOutput implements ChartOutput {
	private final JFrame frame;

	public SwingOutput() {
		frame = new JFrame();
	}

	public void outputChart(JFreeChart chart, int width, int height) {
		frame.setTitle(chart.getTitle().getText());
		ChartPanel chartPanel = new ChartPanel(chart);
		// default size
		chartPanel.setPreferredSize(new Dimension(width, height));
		// add it to our application
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}

}
