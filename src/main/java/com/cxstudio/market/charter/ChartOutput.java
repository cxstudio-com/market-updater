package com.cxstudio.market.charter;

import org.jfree.chart.JFreeChart;

public interface ChartOutput {
	void outputChart(JFreeChart chart, int width, int height);
}
