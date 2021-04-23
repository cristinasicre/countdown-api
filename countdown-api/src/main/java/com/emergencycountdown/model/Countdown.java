package com.emergencycountdown.model;


import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Countdown {

	private static final Log logger = LogFactory.getLog(Countdown.class);
	
	private long endOfCountdown;
	private long countdownTime;
	
	
	public Countdown(Date now , String countdownTime) {
		try {
			
		this.countdownTime = Integer.parseInt(countdownTime )* 1000;
		endOfCountdown = now.getTime() + this.countdownTime ;
		
		}catch(NumberFormatException ex) {
			logger.error(ex.toString());
		}
	}

	public long getEndOfCountdown() {	
		return endOfCountdown;
	}

	public long getCountdownTime() {
		return countdownTime/1000;
	}

	public long getSecondsRemaining() {
		return (endOfCountdown-new Date().getTime())/1000;
	}

	

	

}
