package com.aurigait.googleCalendar.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateEventRequestDTO {
    private String summary;
    private String location;
    private String description;
    private String startDate;//2022-02-10T09:00:00-07:00
    private String endDate;//2022-02-10T17:00:00-07:00
    private String timezone; //America/Los_Angele
}
