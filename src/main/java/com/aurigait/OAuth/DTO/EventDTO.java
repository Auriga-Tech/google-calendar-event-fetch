package com.aurigait.OAuth.DTO;

import com.google.api.client.util.DateTime;

public class EventDTO {
	private String location;
	private DateTime startDate;
	private DateTime endDate;
	private long duration;
	private String durationTime;
	
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public DateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}
	public DateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	

	
	public String getDurationTime() {
		return durationTime;
	}
	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}
	@Override
	public String toString() {
		return "EventDTO [location=" + location + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", duration=" + duration + "]";
//		return super.toString();
	}
	
	
	
	
	
}
