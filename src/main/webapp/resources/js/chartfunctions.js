$.windowActive = true;

$.isWindowActive = function () {
    return $.windowActive;
};

$(window).focus(function() {
    $.windowActive = true;
});

$(window).blur(function() {
    $.windowActive = false;
});

function createCandleStickWithVolumeChart(url, exch, next_seq, divId) {
	// set the allowed units for data grouping
	var initialGroupingUnits = [[
		'minute',
		[1, 2, 5, 10, 15, 30]
	], [
		'hour',
		[1, 2, 3, 4, 6, 8, 12]
	]];
	
	$('#'+divId).highcharts('StockChart', {
		chart: {
			renderTo: divId,
			events : {
				load : function() {
					CHART=this;
					var dataFetchInterval=100;
					if($.windowActive)
						dataFetchInterval = 100;
					else
						dataFetchInterval = 1000;
					dataFetchInterval = setInterval(function() {
						$.getJSON(url+"/"+exch+"/"+SEQ[exch], function(data) {
							if(data === undefined || data.length == 0)
							{
								return;
							}
							console.log("Requesting data:"+url+'/'+exch+"/SEQ="+SEQ[exch]);
							dataLength = data.length;
							if(CHART.series === undefined)
							{
							    console.log('ERROR: Where are my series. Problem fixed... should never occur');
							    return;
							}
							var ohlcSeries = CHART.series[0];
							var volSeries = CHART.series[1];
							for (i = 0; i < dataLength; i++) {
								if (!(exch in TS))
								{
									TS[exch] = data[i].timestamp-1;
									console.log('setting init time');
								}
								
								if(data[i].timestamp >= TS[exch])
								{
									TS[exch] = data[i].timestamp;
									ohlcSeries.addPoint([data[i].timestamp, //date
										                    data[i].price, // open
															data[i].price, // high
															data[i].price, // low
															data[i].price // close
															], false, false, true);
									
									volSeries.addPoint([data[i].timestamp, //date
								                    data[i].amount
													], false, false, true);
									console.log("["+i+"][seq="+(i+SEQ[exch])+" data:"+data[i].timestamp+", "+data[i].price+", "+data[i].amount);
								}
								else
								{
									console.log("warning timestamp issue");
								}
							}
							console.log("Added " + dataLength + " points");
							document.title = "($"+data[dataLength-1].price+") Bitstamp";
							SEQ[exch] = data[dataLength-1].seq + 1;
							CHART.redraw();
						});}
					, dataFetchInterval );
				}
			}
		},
	    
	    rangeSelector : {
			buttons : [{
				type : 'second', 
				count : 15,
				text : '15s'
			}, {
				type : 'second',
				count : 30,
				text : '30s'
			}, {
				type : 'minute',
				count : 1,
				text : '1m'
			}, {
				type : 'minute',
				count : 15,
				text : '15m'
			}, {
				type : 'minute',
				count : 30,
				text : '30m'
			}, {
				type : 'hour',
				count : 1,
				text : '1h'
			}, {
				type : 'hour',
				count : 3,
				text : '3h'
			}, {
				type : 'hour',
				count : 12,
				text : '12h'
			}, {
				type : 'day',
				count : 1,
				text : '1D'
			}],
			selected : 2,
			inputEnabled : false
		},
	
	    title: {
	        text: 'USD/BTC'
	    },
	    xAxis: [{
	    	ordinal:false
	    } ,{
	    	ordinal:false
	    }],
	    
	    yAxis: [{
	        title: {
	            text: 'OHLC'
	        },
	        ordinal: false,
	        height: 200,
	        lineWidth: 2
	    }, {
	        title: {
	            text: 'Volume'
	        },
	        top: 300,
	        height: 100,
	        offset: 0,
	        lineWidth: 2,
	        ordinal: false
	    }],
	    series: [{ 
	        type: 'candlestick',
	        name: 'USD/BTC',
	        data: [],
	        dataGrouping:{
	        	units: initialGroupingUnits
	        }
	        
	    }, {
	        type: 'column',
	        name: 'Volume',
	        data: [],
	        dataGrouping:{
	        	units: initialGroupingUnits
	        },
	        forced: true,
	        yAxis: 1
	    }]
	});
}
