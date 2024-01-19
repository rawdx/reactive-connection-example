package com.newspaper.services;

import java.text.MessageFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newspaper.models.User;

import reactor.core.publisher.Mono;

public class LogInImpl implements LogInInterface {

	private static final String LOGIN_ENDPOINT = "http://localhost:8080/api/users/{}";

	private final WebClient webClient;

	public LogInImpl() {
		this.webClient = WebClient.create();
	}
	
	@Override
	public Mono<Boolean> loginUser(User user) {
		try {
			String loginEndpoint = MessageFormat.format(LOGIN_ENDPOINT, user.getEmail());
			String userJson = convertUserToJson(user);
			
			return sendRequest(loginEndpoint, userJson).flatMap(this::handleResponse)
					.onErrorResume(WebClientResponseException.class, this::handleWebClientResponseException)
					.doOnError(this::handleUnexpectedError);
		} catch (JsonProcessingException e) {
			return Mono.error(e);
		}
	}

	private Mono<ResponseEntity<Void>> sendRequest(String endpoint, String body) {
		return webClient.post().uri(endpoint).header("Content-Type", "application/json")
				.body(BodyInserters.fromValue(body)).retrieve().toBodilessEntity();
	}

	private Mono<Boolean> handleResponse(ResponseEntity<Void> responseEntity) {
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			System.out.println("User logged in successfully");
			return Mono.just(true);
		} else {
			System.out.println("Server response: " + responseEntity.getStatusCode());
			return Mono.just(false);
		}
	}

	private Mono<Boolean> handleWebClientResponseException(WebClientResponseException e) {
		System.err.println("WebClient error - status code: " + e.getStatusCode());
		return Mono.just(false);
	}

	private void handleUnexpectedError(Throwable e) {
		System.err.println("Unexpected error occurred: " + e.getMessage());
	}

	private String convertUserToJson(User user) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(user);
	}
}
