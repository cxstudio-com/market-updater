package com.cxstudio.market.charter;

import org.jfree.data.xy.XYSeries;

import com.cxstudio.market.pattern.model.Step;

public class PatternStepSeries extends XYSeries {
	private static final long serialVersionUID = 6328795152526110648L;

	public PatternStepSeries() {
		super(new Comparable() {

			public int compareTo(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

		});
	}

	public void add(Step step) {
		add(step.getIndex(), step.getChange());
	}
}
