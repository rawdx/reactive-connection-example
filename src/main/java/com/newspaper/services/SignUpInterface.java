package com.newspaper.services;

import com.newspaper.models.User;

import reactor.core.publisher.Mono;

public interface SignUpInterface {
	
	Mono<Boolean> signUpUser(User user);

	String processPhoneNumber(String phoneNumber);

	String processFullName(String firstName, String lastName);
}
