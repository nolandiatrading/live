package com.nolandiatrading.live.bitstamp;

import org.codehaus.jackson.annotate.JsonProperty;


public final class JSONBitstampTradeTemplate {
    @JsonProperty("timestamp")
    public long timestamp;
    @JsonProperty("price")
    public double price;
    @JsonProperty("amount")
    public double amount;
    @JsonProperty("id")
    public long id;

//    public JSONBitstampTradeTemplate() {
//	    this.timestamp = 0;
//	    this.price = 0.0;
//	    this.amount = 0.0;
//	    this.id = 0;
//    }
    
//    public void setValue(JSONBitstampTradeTemplate value) {
//    	this.timestamp = value.timestamp;
//	    this.price = value.price;
//	    this.amount = value.amount;
//	    this.id = value.id;
//    }
}
