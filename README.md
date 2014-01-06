It uses live trades data via the Bitstamp Pusher/Web Sockets API and also the LMAX RingBuffer/Disruptor to store the ongoing data. I wrote the code to be extendable to other exchanges.

skipping the max-one-request-per-second data retrieval, and going straight for the real-time web sockets data.

data processed at the server, then sent over to the browser. Low-latency data processing at the server was a priority. 

re-using as much HighCharts functionality as possible without re-inventing the wheel, and even modified some of the HighCharts source to allow for dynamic data interval grouping via user selection.

The data retrieval is done asynchronously, and is geared to be extendable to a number of uses via EventHandlers/EventListeners/Consumers as provided by the Disruptor API. For example, I have plans to set up an event handler to archive the data (i.e., to HDF5). Or set up another event handler to run against a trading strategy, or to do analytics.

TODOs:
-PropertyHolderConfigurator/JMX
-Trade data rows real-time display
-MtGox
