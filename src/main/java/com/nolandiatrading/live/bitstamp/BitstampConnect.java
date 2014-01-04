package com.nolandiatrading.live.bitstamp;

import com.nolandiatrading.live.common.AllData;
import com.nolandiatrading.live.common.ExchangeAccess;
import com.nolandiatrading.live.common.OrderData;
import com.nolandiatrading.live.common.RawEvent;
import com.nolandiatrading.live.common.TradeData;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.pusher.client.Pusher;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

@Service
public class BitstampConnect implements ConnectionEventListener,
        ChannelEventListener, ExchangeAccess, DisposableBean {

    private final long startTime = System.currentTimeMillis();
    private static final Logger logger = LoggerFactory.getLogger(BitstampConnect.class);
    
    private RingBuffer<RawEvent> ringBuffer;
    private ObjectMapper mapper;
    
    //Normally, I would put this in a configuration file, or read from configuration database
    private static final int MAX_REQUEST_SIZE = 1024*2; //Will allow +1 extra
    private static final int RING_BUFFER_SIZE = 1024*4; //Must be a power of 2
    private static final String API_KEY = "de504dc5763aeef9ff52";
    private static final boolean ORDERS_ENABLED = false; //TODO: temporary
    private ExecutorService exec;
    private Disruptor<RawEvent> disruptor;
    private Pusher pusher;
    
    public BitstampConnect() {
		exec = Executors.newCachedThreadPool();
		disruptor = new Disruptor<RawEvent>(RawEvent.BITSTAMP_EVENT_FACTORY, RING_BUFFER_SIZE, exec);
        
        this.ringBuffer = disruptor.start();
        logger.info(String.format("Fired off Disruptor/RingBuffer of size[%d]", RING_BUFFER_SIZE));
        
        this.mapper = new ObjectMapper();
        
        String apiKey = API_KEY;
        pusher = new Pusher(apiKey);
        pusher.connect(this);
        pusher.subscribe("live_trades", this, "trade");
        logger.info(String.format("Fired off Pusher Live Data with apiKey[%s] and subscribed to 'live_trades' channel'", API_KEY));
        
        //TODO: Currently disabling orders data, make it a property
        if(ORDERS_ENABLED)
        	pusher.subscribe("live_orders", this, "order_deleted","order_created","order_changed");
    }
    
    public BitstampConnect(final EventHandler<RawEvent>... handlers) {
		ExecutorService exec = Executors.newCachedThreadPool();
		Disruptor<RawEvent> disruptor = new Disruptor<RawEvent>(RawEvent.BITSTAMP_EVENT_FACTORY, RING_BUFFER_SIZE, exec);
        
        disruptor.handleEventsWith(handlers);
        this.ringBuffer = disruptor.start();
        logger.info(String.format("Fired off Disruptor/RingBuffer of size[%d]", RING_BUFFER_SIZE));
        
        this.mapper = new ObjectMapper();
        
        String apiKey = API_KEY;
        Pusher pusher = new Pusher(apiKey);
        pusher.connect(this);
        pusher.subscribe("live_trades", this, "trade");
        logger.info(String.format("Fired off Pusher Live Data with apiKey[%s] and subscribed to 'live_trades' channel'", API_KEY));
        
        //TODO: Currently disabling orders data
        if(ORDERS_ENABLED)
        	pusher.subscribe("live_orders", this, "order_deleted","order_created","order_changed");
    }

    /* ConnectionEventListener implementation */

    @Override
    public void onConnectionStateChange(ConnectionStateChange change) {

        logger.info(String.format(
                "[%d] Connection state changed from [%s] to [%s]",
                timestamp(), change.getPreviousState(), change.getCurrentState()));
    }

    @Override
    public void onError(String message, String code, Exception e) {

        logger.info(String.format(
                "[%d] An error was received with message [%s], code [%s], exception [%s]",
                timestamp(), message, code, e));
    }

    /* ChannelEventListener implementation */

    @Override
    public void onEvent(String channelName, String eventName, String data) {
    	
    	//TODO: Find out if there's a way to get actual timestamp from Pusher...
    	long now = System.currentTimeMillis();
        logger.info(String.format(
                "[%d] Received event [%s] on channel [%s] with data [%s]",
                timestamp(), eventName, channelName, data));

        long seq = this.ringBuffer.next();
        RawEvent rawMessageEvent = this.ringBuffer.get(seq);
        rawMessageEvent.setValue(now, channelName, eventName, data);
        ringBuffer.publish(seq);
    }

    @Override
    public void onSubscriptionSucceeded(String channelName) {

        logger.info(String.format(
                "[%d] Subscription to channel [%s] succeeded",
                timestamp(), channelName));
    }

    private long timestamp() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
	public TradeData[] TradesData(long offset) {
    	int size = this.ringBuffer.getBufferSize();
    	long end = this.ringBuffer.getCursor();
    	
    	//Bounded by MAX_REQUEST_SIZE, regardless of how much data we have
    	long start = Math.max(offset, Math.max(end-size, end-MAX_REQUEST_SIZE));
    	
    	//Technically, will allow MAX_REQUEST_SIZE+1
    	int responseCapacity = (int) (end-start+1);
    	
    	TradeData[] tradesList;
    	if(responseCapacity > 0)
    		tradesList = new TradeData[responseCapacity];
    	else
    	{
    		logger.debug(String.format("Data requested is beyond what exists. Returning nothing"));
    		return TradeData.NO_TRADES;
    	}
    	
    	logger.info(String.format("TradeDataRequest[offset=%d][start=%d][end=%d][responseCapacity=%d][MAX ALLOWED+1=%d]"
    			, offset
    			, start
    			, end
    			, responseCapacity
    			, MAX_REQUEST_SIZE + 1));
    	
    	int responseCount = 0;
    	for(long seq = start; seq <= end; seq++, responseCount++)
    	{
    		//TODO: Ignore ORDERS data here
    		RawEvent event = this.ringBuffer.get(seq % size);
    		long ts = event.timestamp;
    		String data = event.data;
    		try {
    			JSONBitstampTradeTemplate liveTrade = mapper.readValue(data, JSONBitstampTradeTemplate.class);
    			logger.info(String.format("TradeDataResponse[i=%d] Adding [%d][%f][%f][%d]", 
    					responseCount, ts, liveTrade.price, liveTrade.amount, seq));
    			tradesList[responseCount] = new TradeData(ts, liveTrade.price, liveTrade.amount, seq);
    		} catch (JsonParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (JsonMappingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	logger.info(String.format("TradeDataResponse COMPLETE: returning %d rows", responseCount));
		return tradesList;
	}
	
    @Override
	public OrderData[] OrdersData(long offset) {
		return OrderData.NO_ORDERS;
	}
	
    @Override
	public AllData[] AllData(long offset) {
		return AllData.NO_ALLS;
	}

	@Override
	public void destroy() throws Exception {
		// Destroy ring buffer, disruptor, shutdown pusher
        pusher.unsubscribe("live_trades");
        if(ORDERS_ENABLED)
        	pusher.unsubscribe("live_orders");
        pusher.disconnect();
        
        disruptor.shutdown();
        exec.shutdown();
	}
}