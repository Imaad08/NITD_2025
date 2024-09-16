package com.nighthawk.spring_portfolio.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private PersonDetailsService personDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * This method is responsible for building a log message for the incoming HTTP request.
	 * @param request  the incoming HTTP request
	 * @return a string containing the request URI, method, remote address, remote host, and remote port
	 */	
	private String buildRequestLogMessage(HttpServletRequest request) {
    	return request.getRequestURI() + " " + request.getMethod() + " " + request.getRemoteAddr() + " " + request.getRemoteHost() + " " + request.getRemotePort();
	}

	/**
	 * This method works with stateless authentication. 
	 * It validates the JWT token and sets the authentication.
	 * @param request  the incoming HTTP request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	private void handleClientRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		Optional<String> jwtToken = getJwtTokenFromCookies(request.getCookies());
	
		if (!jwtToken.isPresent()) {
			logger.warn("No JWT cookie: " + buildRequestLogMessage(request));
			chain.doFilter(request, response);
			return;
		}

		// If there is a JWT token, extract the username and set the authentication
		try {
			String username = jwtTokenUtil.getUsernameFromToken(jwtToken.get());
	
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.personDetailsService.loadUserByUsername(username);
	
				if (jwtTokenUtil.validateToken(jwtToken.get(), userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					logger.warn("Cookie: " + userDetails.getUsername() + " " + userDetails.getAuthorities());
				}
			}
		} catch (IllegalArgumentException e) {
			logger.error("JWT Token get error", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token get error");
			return;
		} catch (ExpiredJwtException e) {
			logger.error("JWT Token has expired", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
			return;
		} catch (Exception e) {
			logger.error("JWT error occurred", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT error occurred");
			return;
		}
	
		chain.doFilter(request, response);
	
	}

	/**
	 * This method is responsible for filtering incoming HTTP requests. 
	 * API reuests are handled by the handleClientRequest method.
	 * @param request  the incoming HTTP request
	 * @param response the HTTP response
	 * @param chain    the filter chain
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
		String origin = request.getHeader("X-Origin");
		
		// If the request is coming from the client api
    	if (origin != null && origin.equals("client")) {
			logger.warn("Client request: " + buildRequestLogMessage(request));
			handleClientRequest(request, response, chain);
		// Else the request is coming from session
		} else {
			logger.warn("Session request: " + buildRequestLogMessage(request));
			chain.doFilter(request, response);
			return;
		}
	}
	
	/**
	 * This method is responsible for extracting the JWT token from the cookies. It returns an Optional<String> that
	 * contains the JWT token if it exists, or an empty Optional if it doesn't exist.
	 *
	 * @param cookies the array of cookies from the HTTP request
	 * @return an Optional<String> containing the JWT token, or an empty Optional if the token doesn't exist
	 */
	private Optional<String> getJwtTokenFromCookies(Cookie[] cookies) {
		if (cookies == null || cookies.length == 0) {
			//logger.warn("No cookies");
			return Optional.empty();
		}
	
		return Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals("jwt_java_spring"))
			.map(Cookie::getValue)
			.findFirst();
	}
}