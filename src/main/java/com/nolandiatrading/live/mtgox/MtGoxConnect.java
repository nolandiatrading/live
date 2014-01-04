package com.nolandiatrading.live.mtgox;

import org.springframework.stereotype.Service;

import com.nolandiatrading.live.common.ExchangeAccess;
import com.nolandiatrading.live.common.TradeData;
import com.nolandiatrading.live.common.OrderData;
import com.nolandiatrading.live.common.AllData;

@Service
public class MtGoxConnect implements ExchangeAccess {
	@Override
	public TradeData[] TradesData(long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderData[] OrdersData(long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AllData[] AllData(long offset) {
		// TODO Auto-generated method stub
		return null;
	}

}