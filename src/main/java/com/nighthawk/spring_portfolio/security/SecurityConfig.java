package com.nighthawk.spring_portfolio.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
/*
* To enable HTTP Security in Spring
*/
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {	
        
        http
            // JWT related configuration
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST,"/authenticate").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/person/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/person/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/people/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/person/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/person/**").hasAuthority("ROLE_ADMIN")
            )
            .cors(Customizer.withDefaults())
            .headers(headers -> headers
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Credentials", "true"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-ExposedHeaders", "*", "Authorization"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Content-Type", "Authorization", "x-csrf-token"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-MaxAge", "600"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods", "POST", "GET", "DELETE", "OPTIONS", "HEAD"))
            )
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			)
			.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            
			// Session related configuration
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/mvc/person/search/**").authenticated()
                .requestMatchers("/mvc/person/create/**").authenticated()
                .requestMatchers("/mvc/person/read/**").authenticated()
                .requestMatchers("/mvc/person/update/**").authenticated()
                .requestMatchers("/mvc/person/delete/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET,"/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/authenticateForm").permitAll()
                .requestMatchers("/**").permitAll()
            )
            .formLogin(form -> form 
                .loginPage("/login")
                .defaultSuccessUrl("/mvc/person/read")
            )
            .logout(logout -> logout
                .deleteCookies("sess_java_spring")
                .logoutSuccessUrl("/")
            )
			;
        return http.build();
    }
}
