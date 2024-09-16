package com.nighthawk.spring_portfolio.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**JwtAuthenticationEntryPoint
 * Implements AuthenticationEntryPoint, an interface in Spring Security. 
 * Defines response when an unauthenticated user tries to access a protected resource.
 * 
 * @Component is a Spring annotation, which means Spring will automatically create an instance of it.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		// HTTP 401 error is sent in the response header
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}