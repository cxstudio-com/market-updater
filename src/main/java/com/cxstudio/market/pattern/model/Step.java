package com.cxstudio.market.pattern.model;

public class Step {
	private float change;
	private int index;

	public Step(float change, int index) {
		this.change = change;
		this.index = index;
	}

	public float getChange() {
		return change;
	}

	public void setChange(float change) {
		this.change = change;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
