package com.aurigait.googleCalendar.service;

import com.aurigait.googleCalendar.DTO.CreateEventRequestDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CalendarService {
    @Autowired
    private UserService userService;

    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "GoogleCalendar";
    private static com.google.api.services.calendar.Calendar client;

    /**
     * Fetch google calendar events from caledar API
     * @param startDate
     * @param endDate
     * @param search
     * @return
     */
    public String fetchCalendarEvents(String startDate, String endDate, String search) {
        Events eventList;
        String response;
        try {
            String token = userService.getCurrentUser().getAccessToken();
            GoogleCredential credential = new GoogleCredential().setAccessToken(token);

            final DateTime startDateTime = new DateTime(startDate + "T00:00:00");
            final DateTime endDateTime = new DateTime(endDate + "T23:59:59");

            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            Calendar.Events events = client.events();
            if(search == null || search.trim().equals("")) {
                eventList = events.list("primary").setTimeZone("Asia/Kolkata").setTimeMin(startDateTime).setTimeMax(endDateTime).execute();
            } else {
                eventList = events.list("primary").setTimeZone("Asia/Kolkata").setTimeMin(startDateTime).setTimeMax(endDateTime).setQ(search).execute();
            }

            response = eventList.getItems().toString();
        } catch (Exception e) {
            response = "Error in fetching google calendar data, please try again!";
        }
        return response;
    }

    /**
     * Create a Google Calendar event using calendar API
     * @param createEventRequestDTO
     * @return
     */
    public String createGoogleCalendarEvent(CreateEventRequestDTO createEventRequestDTO) {
        try {
            String token = userService.getCurrentUser().getAccessToken();
            System.out.println(token);
            GoogleCredential credential = new GoogleCredential().setAccessToken(token);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            Event event = new Event()
                    .setSummary(createEventRequestDTO.getSummary())
                    .setLocation(createEventRequestDTO.getLocation())
                    .setDescription(createEventRequestDTO.getDescription());

            DateTime startDateTime = new DateTime(createEventRequestDTO.getStartDate());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(createEventRequestDTO.getTimezone());
            event.setStart(start);

            DateTime endDateTime = new DateTime(createEventRequestDTO.getEndDate());
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(createEventRequestDTO.getTimezone());
            event.setEnd(end);

            event.setLocked(true);

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

            event.setVisibility("default");

            String calendarId = "primary";

            event = client.events().insert(calendarId, event).execute();
            return event.getHtmlLink();
        } catch (Exception e) {
            return "Error encountered while crating event: " + e.getMessage();
        }
    }
}
