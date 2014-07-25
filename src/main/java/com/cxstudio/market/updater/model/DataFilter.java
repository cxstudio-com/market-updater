package com.cxstudio.market.updater.model;

import java.util.Calendar;
import java.util.Date;

public class DataFilter {
	private Date startTime;
	private Date endTime;
	private int interval;
	private int limit;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public static DataFilter lastNumOfDays(int numOfDays) {
		DataFilter newFilter = new DataFilter();
		Calendar cal = Calendar.getInstance();
		newFilter.setEndTime(cal.getTime());
		cal.roll(Calendar.DATE, (-1) * numOfDays);
		newFilter.setStartTime(cal.getTime());
		return newFilter;
	}

	@Override
	public String toString() {
		return "DataFilter [startTime=" + startTime + ", endTime=" + endTime + ", interval=" + interval + ", limit="
				+ limit + "]";
	}

}
