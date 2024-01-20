package com.newspaper.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.newspaper.models.User;
import com.newspaper.services.LogInImpl;
import com.newspaper.services.LogInInterface;
import com.newspaper.services.SignUpImpl;
import com.newspaper.services.SignUpInterface;
import com.newspaper.utils.Encryptor;

import jakarta.servlet.http.HttpSession;
import reactor.core.publisher.Mono;

@Controller
public class AuthController {
	
	@GetMapping("login")
	public String login() {
		return "redirect:/";
	}

	@PostMapping("/login")
	public Mono<String> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
	    LogInInterface authImpl = new LogInImpl();

	    return authImpl.loginUser(email, password)
	            .map(success -> {
	                if (success) {
	                    session.setAttribute("loggedIn", true);
	                    System.out.println("Login successful for user: " + email);
	                } else {
	                    session.setAttribute("loggedIn", false);
	                }
	                return "redirect:/";
	            });
	}
	 
    @GetMapping("/logout")
    public String logout(@SessionAttribute(name = "loggedIn", required = false) Boolean loggedIn, HttpSession session) {
        if (loggedIn != null && loggedIn) {
//        	session.setAttribute("loggedIn", false);
            session.invalidate();
        }
        return "redirect:/";
    }

	@GetMapping("signup")
	public String signup() {
		return "redirect:/";
	}

	@PostMapping("/signup")
	public Mono<String> signup(@RequestParam String email, @RequestParam String password,
			@RequestParam String firstName, @RequestParam String lastName, @RequestParam String phoneNumber, HttpSession session) {
		password = Encryptor.encrypt(password);

		SignUpInterface signUpImpl = new SignUpImpl();

		String name = signUpImpl.processFullName(firstName, lastName);
		String phone = signUpImpl.processPhoneNumber(phoneNumber);

		User user = new User(email, password, name, phone);

		return signUpImpl.signUpUser(user).map(success -> {
			if (success) {
				session.setAttribute("loggedIn", true);
				System.out.println("User created successfully: " + email);
				return "redirect:/";
			} else {
				return "redirect:/";
			}
		});
	}
}
