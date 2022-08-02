package com.aurigait.OAuth.Controllers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.aurigait.OAuth.Entity.User;
import com.aurigait.OAuth.Service.UserService;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.oauth2.model.Userinfo;

@RestController
public class OAuthController {
	private static HttpTransport httpTransport;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	
	GoogleClientSecrets clientSecrets;
	GoogleAuthorizationCodeFlow flow;
	Credential credential;
	
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectURI;
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;
	
	@Autowired
	private UserService userService;

	
	@GetMapping(value = "/")
	public String hello() {
		return "Hello World";
	}
	
	@RequestMapping(value = "/oauth", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
		return new RedirectView(authorize());
	}
	@RequestMapping(value = "/oauth", method = RequestMethod.GET, params = "code")
	public String oauth2Callback(@RequestParam(value = "code") String code) {
		try {
			TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
			System.out.println("START" +response.getAccessToken() + "END");
			
			System.out.println("START" +response.getRefreshToken() + "END");
			
			
			credential = flow.createAndStoreCredential(response, "userID");
			Oauth2 oauth2Client =
				      new Oauth2.Builder(httpTransport, JSON_FACTORY, credential)
				          .setApplicationName("Oauth")
				          .build();

			Userinfo userInfo = oauth2Client.userinfo().get().execute();
			userService.saveUserData(response, userInfo);
			
			
		} catch (Exception e) {
			System.out.println(e);
			return "error";
		}
		
		return "TOKEN SAVED";
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public User lastsToken() throws Exception {
		
		return userService.getLastSavedToken();
	}
	
	
	private String authorize() throws Exception {
		AuthorizationCodeRequestUrl authorizationUrl;
		
		Collection<String> SCOPES = new ArrayList<String>();
		SCOPES.add(CalendarScopes.CALENDAR);
		SCOPES.add(Oauth2Scopes.USERINFO_EMAIL);
		SCOPES.add(Oauth2Scopes.USERINFO_PROFILE);
		
		if (flow == null) {
			Details web = new Details();
			web.setClientId(clientId);
			web.setClientSecret(clientSecret);
			clientSecrets = new GoogleClientSecrets().setWeb(web);
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
					SCOPES).setAccessType("offline").setApprovalPrompt("force").build();
		}
		authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
		System.out.println("cal authorizationUrl->" + authorizationUrl);
		return authorizationUrl.build();
	}
	
}
