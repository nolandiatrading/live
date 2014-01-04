package com.nolandiatrading.live;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nolandiatrading.live.bitstamp.BitstampConnect;
import com.lmax.disruptor.EventHandler;
import com.nolandiatrading.live.common.AllData;
import com.nolandiatrading.live.common.ExchangeAccess;
import com.nolandiatrading.live.common.ExchangeID;
import com.nolandiatrading.live.common.OrderData;
import com.nolandiatrading.live.common.RawEvent;
import com.nolandiatrading.live.common.TradeData;
import com.nolandiatrading.live.mtgox.MtGoxConnect;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private Map<ExchangeID, ExchangeAccess> exchanges;
	
	@SuppressWarnings("unchecked")
	public HomeController()
	{
        final EventHandler<RawEvent> HandlerForDataArchive = new EventHandler<RawEvent>() {
            public void onEvent(final RawEvent event, final long sequence, final boolean endOfBatch) throws Exception {
                logger.info(String.format("Seq[%d]: HandlerForDataArchive ValueEvent: time[%d], channel[%s], event[%s], data[%s]", sequence, event.timestamp, event.channelName, event.eventName, event.data));
            }
        };
        
        exchanges = new HashMap<ExchangeID, ExchangeAccess>();
		exchanges.put(ExchangeID.BITSTAMP, new BitstampConnect(HandlerForDataArchive));
		exchanges.put(ExchangeID.MTGOX, new MtGoxConnect());
	}
	
	//Views
	@RequestMapping({"/charts"})
	public String showCharts() {
		//model.
	    return "charts";
	}
	
	@RequestMapping(value = "/trades/{exch}/{seq}", method = RequestMethod.GET)
	public @ResponseBody TradeData[] TradesData(
			@PathVariable("exch") String exch,
			@PathVariable("seq") long seq) 
	{
		switch (ExchangeID.valueOf(exch.toUpperCase()))
		{
		case BITSTAMP:
			return exchanges.get(ExchangeID.BITSTAMP).TradesData(seq);
		case MTGOX:
			return exchanges.get(ExchangeID.MTGOX).TradesData(seq);
		default:
			System.out.println("Unknown exchange");
			return TradeData.NO_TRADES;
		}
	}
	
	@RequestMapping(value = "/orders/{exch}/{seq}", method = RequestMethod.GET)
	public @ResponseBody OrderData[] OrdersData(
			@PathVariable("exch") String exch,
			@PathVariable("seq") long seq) 
	{
		switch (ExchangeID.valueOf(exch.toUpperCase()))
		{
		case BITSTAMP:
			return exchanges.get(ExchangeID.BITSTAMP).OrdersData(seq);
		case MTGOX:
			return exchanges.get(ExchangeID.MTGOX).OrdersData(seq);
		default:
			System.out.println("Unknown exchange");
			return OrderData.NO_ORDERS;
		}
	}
	
	@RequestMapping(value = "/all/{exch}/{seq}", method = RequestMethod.GET)
	public @ResponseBody AllData[] AllData(
			@PathVariable("exch") String exch,
			@PathVariable("seq") long seq) 
	{
		switch (ExchangeID.valueOf(exch.toUpperCase()))
		{
		case BITSTAMP:
			return exchanges.get(ExchangeID.BITSTAMP).AllData(seq);
		case MTGOX:
			return exchanges.get(ExchangeID.MTGOX).AllData(seq);
		default:
			System.out.println("Unknown exchange");
			return AllData.NO_ALLS;
		}
	}
	
	@ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatchException(TypeMismatchException ex, HttpServletRequest req, HttpServletResponse resp) {
        logger.info("Parameter failure: {}", ex.getRootCause().getLocalizedMessage());
        logger.info("Invalid value is: {}", ex.getValue());
        logger.info("Required type is: {}", ex.getRequiredType().getSimpleName());
        // TODO: Return something reasonable to the end user.
        return null;
    }
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "charts";
	}
}
