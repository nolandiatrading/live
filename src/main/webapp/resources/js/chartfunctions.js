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
					setInterval(function() {
						$.getJSON(url+'/'+exch+"/"+NEXT_SEQ, function(data) {
							if(data === undefined || data.length == 0)
							{
								return;
							}
							console.log("Requesting data:"+url+'/'+exch+"/NEXT_SEQ="+NEXT_SEQ);
							dataLength = data.length;
							if(CHART.series === undefined)
							{
							    console.log('ERROR: Where are my series. Problem fixed... should never occur');
							    return;
							}
							var ohlcSeries = CHART.series[0];
							var volSeries = CHART.series[1];
							for (i = 0; i < dataLength; i++) {
								ohlcSeries.addPoint([data[i].timestamp, //date
								                    data[i].price, // open
													data[i].price, // high
													data[i].price, // low
													data[i].price // close
													], false, false, true);
								
								volSeries.addPoint([data[i].timestamp, //date
								                    data[i].amount
													], false, false, true);
								console.log("["+i+"][seq="+(i+NEXT_SEQ)+" data:"+data[i].timestamp+", "+data[i].price+", "+data[i].amount);
							}
							console.log("Added " + dataLength + " points");
							
							NEXT_SEQ = data[dataLength-1].seq + 1;
							CHART.redraw();
						});}
					, 1000);
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
