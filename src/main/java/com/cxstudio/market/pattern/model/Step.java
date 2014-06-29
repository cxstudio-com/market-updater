package com.cxstudio.market.pattern.model;

import java.util.Date;

public class Step {
	private float change;
	private int index;
	private Date time;

	public Step() {

	}

	public Step(float change, int index, Date time) {
		this.change = change;
		this.index = index;
		this.time = time;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
