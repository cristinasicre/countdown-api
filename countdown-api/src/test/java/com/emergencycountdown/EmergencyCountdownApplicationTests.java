package com.emergencycountdown;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.emergencycountdown.controller.CountdownController;
import com.emergencycountdown.model.Countdown;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class EmergencyCountdownApplicationTests {
	
	private MockMvc mvc;

	@Autowired
	private CountdownController countdownController;

	@BeforeEach
	public void setup() {

		JacksonTester.initFields(this, new ObjectMapper());
		mvc = MockMvcBuilders.standaloneSetup(countdownController).build();
	}

	@Test
	public void settingCountdownTest() throws Exception {
		String countdownTime = "60";
		String nowString = "2021-02-18 18:00:00";
		String launchString = "2021-02-18 18:01:00";

		DateFormat format = new SimpleDateFormat("yyyy-mm-dd H:m:s");

		Countdown countdown = new Countdown(format.parse(nowString), countdownTime);
		assertEquals(format.parse(launchString).getTime(), countdown.getEndOfCountdown());

	}

	@Test
	public void startCountTest() throws Exception {
		MockHttpServletResponse response = mvc.perform(get("/start/10").accept(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo("{\"seconds\":10}");
	}

		
	@Test
	public void CountInProgress() throws Exception {
		
		 mvc.perform(get("/start"));

		MockHttpServletResponse response = mvc.perform(get("/start").accept(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	
	}
	
		
	@Test
	public void timeRemainingbeforeBeforeStartTest() throws Exception {

		MockHttpServletResponse response = mvc.perform(get("/time-remaining").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

	}

	@Test
	public void timeRemainingBeforeTest() throws Exception {

		 mvc.perform(get("/start"));

		MockHttpServletResponse responseTimeRemaining = mvc
				.perform(get("/time-remaining").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		assertThat(responseTimeRemaining.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseTimeRemaining.getContentAsString()).contains("{\"seconds\":");
	}
	
	
	
	
}
