package com.aurigait.OAuth.Controllers;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurigait.OAuth.DTO.EventDTO;
import com.aurigait.OAuth.Entity.User;
import com.aurigait.OAuth.Service.UserService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.oauth2.Oauth2Scopes;

@RestController
public class UserController {
	private static final String APPLICATION_NAME = "oAuth";
	private static HttpTransport httpTransport;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static com.google.api.services.calendar.Calendar client;
	
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;
	
	@Autowired
	private UserService userService;

	
	
//	final DateTime date1 = new DateTime("2018-09-30T16:30:00.000+05:30");
//	final DateTime date2 = new DateTime("2018-10-04T16:30:00.000+05:30");
	
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public ResponseEntity<String> getEvents(@RequestParam(value = "sdate") String sdate, @RequestParam(value = "edate") String edate) {
		com.google.api.services.calendar.model.Events eventList;
		String message;
		try {
			String token = userService.getLastSavedToken().getAccessToken();
			GoogleCredential credential = new GoogleCredential().setAccessToken(token);
			
			final DateTime date1 = new DateTime(sdate + "T00:00:00");
			final DateTime date2 = new DateTime(edate + "T23:59:59");
			
			System.out.println("Credentials generated");
			
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
			Events events = client.events();
			eventList = events.list("primary").setTimeZone("Asia/Kolkata").setTimeMin(date1).setTimeMax(date2).execute();
			message = eventList.getItems().toString();
			System.out.println("My:" + eventList.getItems());
		} catch (Exception e) {
			
			message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.";
		}

		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/events", method = RequestMethod.GET, params = "q")
	public ResponseEntity<String> getQueryEvents(@RequestParam(value = "sdate") String sdate, @RequestParam(value = "edate") String edate, @RequestParam(value = "q") String q) {
		com.google.api.services.calendar.model.Events eventList;
		String message;
		try {
			String token = userService.getLastSavedToken().getAccessToken();
			GoogleCredential credential = new GoogleCredential().setAccessToken(token);
			
			final DateTime date1 = new DateTime(sdate + "T00:00:00");
			final DateTime date2 = new DateTime(edate + "T23:59:59");
			
			System.out.println("Credentials generated");
			
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
			Events events = client.events();
			eventList = events.list("primary").setTimeZone("Asia/Kolkata").setTimeMin(date1).setTimeMax(date2).setQ(q).execute();
			message = eventList.getItems().toString();
			System.out.println("My:" + eventList.getItems());
		} catch (Exception e) {
			
			message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.";
		}

		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/eventsFormatted", method = RequestMethod.GET, params = "q")
	public ResponseEntity<?> getQueryEventsFormatted(@RequestParam(value = "sdate") String sdate, @RequestParam(value = "edate") String edate, @RequestParam(value = "q") String q) {
		com.google.api.services.calendar.model.Events eventList;
		String message;
		ArrayList<EventDTO> eventDTO = new ArrayList<>();
		try {
			String token = userService.getLastSavedToken().getAccessToken();
			GoogleCredential credential = new GoogleCredential().setAccessToken(token);
			
			final DateTime date1 = new DateTime(sdate + "T00:00:00");
			final DateTime date2 = new DateTime(edate + "T23:59:59");
			
			System.out.println("Credentials generated");
			
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
			Events events = client.events();
			eventList = events.list("primary").setTimeZone("Asia/Kolkata").setTimeMin(date1).setTimeMax(date2).setQ(q).execute();
			
			
			
//			message = eventList.getItems().toString();
			for(Event e: eventList.getItems()) {
				long duration = e.getEnd().getDateTime().getValue() - e.getStart().getDateTime().getValue();
//				duration /= 1000;
				EventDTO newEvent = new EventDTO();
//				newEvent.setAttendies(e.getAttendees());
				newEvent.setLocation(e.getLocation());
				newEvent.setStartDate(e.getStart().getDateTime());
				newEvent.setEndDate(e.getEnd().getDateTime());
				System.out.println(e.getEnd().getDateTime().toString());
				System.out.println(e.getStart().getDateTime().toString());
				
				
				
				newEvent.setDuration(duration);
				newEvent.setDurationTime(convertSecondToHourMinutesAndSeconds(duration));
//				newEvent.setEmail(token);
//				newEvent.setName("M");
				eventDTO.add(newEvent);
				
			}
			System.out.println("My:" + eventList.getItems());
			
		} catch (Exception e) {
			
			message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.";
		}
//		User user = userService.getLastSavedToken();
		return new ResponseEntity<>(eventDTO, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
	public ResponseEntity<String> refreshToken() {
		try {
			List<User> users = userService.getAllUser();
			for(User user: users) {				
				String newToken = getNewToken(user.getRefreshToken(),clientId, clientSecret);
				user.setAccessToken(newToken);
				userService.saveUpdatedData(user);
			}
		} catch(Exception e) {
			System.out.println(e);
			return new ResponseEntity<>("Error", HttpStatus.OK);
		}
		return new ResponseEntity<>("Refreshed", HttpStatus.OK);		 
	}
	
	
	public Credential getCredentials() throws GeneralSecurityException, IOException, FileNotFoundException {
		
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();


        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build();

        String refreshToken = "<REFRESH-TOKEN>"; //Find a secure way to store and load refresh token
        credential.setAccessToken(getNewToken(refreshToken, clientId, clientSecret));
        credential.setRefreshToken(refreshToken);

        return credential;
    }
	
	 public String getNewToken(String refreshToken, String clientId, String clientSecret) throws IOException {
        ArrayList<String> SCOPES = new ArrayList<>();
 
        SCOPES.add(CalendarScopes.CALENDAR);
 		SCOPES.add(Oauth2Scopes.USERINFO_EMAIL);
 		SCOPES.add(Oauth2Scopes.USERINFO_PROFILE);
 
         TokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                 refreshToken, clientId, clientSecret).setScopes(SCOPES).setGrantType("refresh_token").execute();
 
         return tokenResponse.getAccessToken();
	 }
	
	
	@RequestMapping(value = "/test", method = RequestMethod.GET, params = "token2")
	public String createEvent(@RequestParam(value = "token2") String code) {
		com.google.api.services.calendar.model.Events eventList;
		String message;
		try {
			
			GoogleCredential credential = new GoogleCredential().setAccessToken(code);
		client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
		
		Event event = new Event()
		    .setSummary("Google I/O 2015")
		    .setLocation("800 Howard St., San Francisco, CA 94103")
		    .setDescription("A chance to hear more about Google's developer products.");
		
//		event.set
		
		DateTime startDateTime = new DateTime("2022-02-10T09:00:00-07:00");
		EventDateTime start = new EventDateTime()
		    .setDateTime(startDateTime)
		    .setTimeZone("America/Los_Angeles");
		event.setStart(start);
		
		DateTime endDateTime = new DateTime("2022-02-10T17:00:00-07:00");
		EventDateTime end = new EventDateTime()
		    .setDateTime(endDateTime)
		    .setTimeZone("America/Los_Angeles");
		event.setEnd(end);
		
		event.setLocked(true);
//		event.set
		
		String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
		event.setRecurrence(Arrays.asList(recurrence));
		
		EventAttendee[] attendees = new EventAttendee[] {
		    new EventAttendee().setEmail("lpage@example.com"),
		    new EventAttendee().setEmail("sbrin@example.com"),
		};
		event.setAttendees(Arrays.asList(attendees));
		
		EventReminder[] reminderOverrides = new EventReminder[] {
		    new EventReminder().setMethod("email").setMinutes(24 * 60),
		    new EventReminder().setMethod("popup").setMinutes(10),
		};
		Event.Reminders reminders = new Event.Reminders()
		    .setUseDefault(false)
		    .setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);
		
		event.setVisibility("onlyme");
		
		String calendarId = "primary";
		event = client.events().insert(calendarId, event).execute();
		System.out.printf("Event created: %s\n", event.getHtmlLink());
		return event.getHtmlLink();
		
		} catch (Exception e) {
					
					return "error";
				}
			}
	
	private String convertSecondToHourMinutesAndSeconds(long millis) {
		
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
	            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
	            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		
		return hms;
		
//		int sec = (int)s % 60;
//	    int min = (int)(s / 60)%60;
//	    int hours = (int)(s/60)/60;
//	    
//	    
//        
//	    String strSec=(sec<10)?"0"+Integer.toString(sec):Integer.toString(sec);
//	    String strmin=(min<10)?"0"+Integer.toString(min):Integer.toString(min);
//	    String strHours=(hours<10)?"0"+Integer.toString(hours):Integer.toString(hours);
	    
//	    return (strHours + ":" + strmin + ":" + strSec);
	}
}
