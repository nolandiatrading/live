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
}
