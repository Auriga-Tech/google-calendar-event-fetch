package com.aurigait.googleCalendar.controller;

import com.aurigait.googleCalendar.DTO.CreateEventRequestDTO;
import com.aurigait.googleCalendar.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public ResponseEntity<String> fetchCalendarEvents(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        String response = calendarService.fetchCalendarEvents(startDate, endDate, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET, params = "search")
    public ResponseEntity<String> fetchCalendarEvents(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate, @RequestParam(value = "search") String search) {
        String response = calendarService.fetchCalendarEvents(startDate, endDate, search);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    public ResponseEntity<String> createEvent(@RequestBody CreateEventRequestDTO createEventRequestDTO) {
        String response = calendarService.createGoogleCalendarEvent(createEventRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
