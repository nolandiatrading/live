package com.nolandiatrading.live.common;

public interface ExchangeAccess {
	
	public TradeData[] TradesData(long offset);
	public OrderData[] OrdersData(long offset);	
	public AllData[] AllData(long offset);
}
