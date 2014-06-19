package com.cxstudio.market.updater.model;

import java.util.Date;

public class Trade {
	private long tradeId;
	private Symbol symbol;
	private Date dateTime;
	private float open;
	private float close;
	private float high;
	private float low;
	private long volume;
	private float percentChange;
	private float bid;
	private float ask;

	public Trade() {

	}

	public Trade(Symbol symbol) {
		this.symbol = symbol;
	}

	public long getTradeId() {
		return tradeId;
	}

	public void setTradeId(long tradeId) {
		this.tradeId = tradeId;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public float getPercentChange() {
		return percentChange;
	}

	public void setPercentChange(float percentChange) {
		this.percentChange = percentChange;
	}

	public float getBid() {
		return bid;
	}

	public void setBid(float bid) {
		this.bid = bid;
	}

	public float getAsk() {
		return ask;
	}

	public void setAsk(float ask) {
		this.ask = ask;
	}

	@Override
	public String toString() {
		return "Trade [symbol=" + symbol.getTicker() + ", dateTime=" + dateTime + ", open=" + open + ", close=" + close
				+ ", high=" + high + ", low=" + low + ", volume=" + volume + ", percentChange=" + percentChange
				+ ", bid=" + bid + ", ask=" + ask + "]";
	}

}
