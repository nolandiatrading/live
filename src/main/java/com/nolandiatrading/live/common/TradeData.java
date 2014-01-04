package com.nolandiatrading.live.common;



public class TradeData {
	public TradeData(long ts, double price, double amount, long seq) {
		// TODO Auto-generated constructor stub
		this.timestamp = ts;
		this.price = price;
		this.amount = amount;
		this.seq = seq;
	}
	public long timestamp;
	public double price;
	public double amount;
	public long seq;
	
	public static final TradeData[] NO_TRADES = {};
}


