package com.emergencycountdown.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.emergencycountdown.exception.CountdownInProgressException;
import com.emergencycountdown.model.Countdown;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

@RestController
@RequestMapping("/")
@CrossOrigin
public class CountdownController {

	private static final Log logger = LogFactory.getLog(CountdownController.class);

	@Value("${countdown.time:-1}")
	private String countdownTime;

	private Countdown countdown;

	@RequestMapping(value = "/time-remaining", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<String> getTimeRemaining() {
		logger.info("calling getTimeRemaining");
		JSONObject response = new JSONObject();

		try {
			if (countdown.getSecondsRemaining() < 0) {
				logger.warn("The rocket has already been launched");
				response.put("seconds", -1);
				response.put("message", "The rocket has already been launched");
				return ResponseEntity.status(HttpStatus.OK).body(response.toString());
			}

			logger.debug(((countdown.getSecondsRemaining() >= 0) ? countdown.getSecondsRemaining() : 0)
					+ " seconds left before rocket launch");
			response.put("seconds", ((countdown.getSecondsRemaining() >= 0) ? countdown.getSecondsRemaining() : 0));

		} catch (NullPointerException e) {
			logger.error(e.toString());
			response.put("message", "Error, countdown has not started");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
		} catch (Exception e) {
			logger.error(e.toString());
			response.put("message", "Error, there has been a server error");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
		}

		logger.info("returning value from getTimeRemaining");
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());

	}

	@RequestMapping(value = { "/start",
			"/start/{seconds}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> startCountdown(@PathVariable(required = false, name = "seconds") final String seconds)
			throws CountdownInProgressException {
		logger.info("calling startCountdown");
		JSONObject response = new JSONObject();

		if (countdown == null || countdown.getSecondsRemaining() < 0) {
			if (seconds != null) {
				countdownTime = seconds;
			}
			try {

				if (countdownTime.equals("-1")) {

					logger.error("CountdownTime is not set in the properties file.");
					response.put("message", "CountdownTime is null");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
				}
				countdown = new Countdown(new Date(), countdownTime);
				logger.debug(
						"The countdown has started, the rocket will be launched in " + countdown.getCountdownTime());
				response.put("seconds", countdown.getCountdownTime());

			} catch (NullPointerException e) {
				logger.error(e.toString());
				response.put("message", "Error, countdown could not be started");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
			} catch (Exception e) {
				logger.error(e.toString());
				response.put("message", "Error, there has been a server error");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
			}
		} else {

			logger.error("The countdown has already started.");
			throw new CountdownInProgressException();
		}
		logger.info("returning value from startCountdown");
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());

	}

}
