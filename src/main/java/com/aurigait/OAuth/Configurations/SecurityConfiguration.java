package com.aurigait.OAuth.Configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests()
		.antMatchers("/").permitAll()	
		.antMatchers("/oauth", "/events", "/create", "/get").permitAll()	
		.anyRequest().permitAll()
		.and()
		.oauth2Login();
	}
	

}
