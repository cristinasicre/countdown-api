package com.emergencycountdown.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The countdown has already started")
public class CountdownInProgressException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -658792525901006688L;

}
