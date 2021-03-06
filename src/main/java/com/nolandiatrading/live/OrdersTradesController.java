package com.nolandiatrading.live;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nolandiatrading.live.bitstamp.BitstampConnect;
import com.nolandiatrading.live.common.AllData;
import com.nolandiatrading.live.common.ExchangeID;
import com.nolandiatrading.live.mtgox.MtGoxConnect;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/all")
public class OrdersTradesController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrdersTradesController.class);
	
	@Autowired
	private BitstampConnect bitstampService;
	
	@Autowired
	private MtGoxConnect mtGoxService;
	
	@RequestMapping(value = "/{exch}/{seq}", method = RequestMethod.GET)
	public @ResponseBody AllData[] AllData(
			@PathVariable("exch") String exch,
			@PathVariable("seq") long seq) 
	{
		switch (ExchangeID.valueOf(exch.toUpperCase()))
		{
		case BITSTAMP:
			return bitstampService.AllData(seq);
		case MTGOX:
			return mtGoxService.AllData(seq);
		default:
			logger.error("Unknown exchange");
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
}
