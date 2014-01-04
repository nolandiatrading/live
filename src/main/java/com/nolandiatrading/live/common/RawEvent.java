package com.nolandiatrading.live.common;

import com.lmax.disruptor.EventFactory;

public class RawEvent {
	//Header Info
	public long timestamp;
	public ExchangeID exchangeId;
	public String channelName;
	public String eventName;
	
	//Raw Payload data
	public String data;
	
	public RawEvent(ExchangeID exch)
	{
		timestamp = 0;
		exchangeId = exch;
	}
	
	public void setValue(long ts, String channelName, String eventName, String data)
	{
		this.timestamp = ts;
		this.channelName = channelName;
		this.eventName = eventName;
		this.data = data;
	}
	
	public final static EventFactory<RawEvent> BITSTAMP_EVENT_FACTORY = new EventFactory<RawEvent>() {
        public RawEvent newInstance() {
            return new RawEvent(ExchangeID.BITSTAMP);
        }
    };
    
    public final static EventFactory<RawEvent> MTGOX_EVENT_FACTORY = new EventFactory<RawEvent>() {
        public RawEvent newInstance() {
            return new RawEvent(ExchangeID.MTGOX);
        }
    };
}
