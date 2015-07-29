package com.shecook.wenyi.model;


public class MyAdvView extends BaseModel {
	public String advImageUrl;
	public String advTitle;
	public String eventUrl;
	
	public MyAdvView() {
		super();
	}
	public MyAdvView(String advImageUrl, String advTitle, String eventUrl) {
		super();
		this.advImageUrl = advImageUrl;
		this.advTitle = advTitle;
		this.eventUrl = eventUrl;
	}
	public String getAdvImageUrl() {
		return advImageUrl;
	}
	public void setAdvImageUrl(String advImageUrl) {
		this.advImageUrl = advImageUrl;
	}
	public String getAdvTitle() {
		return advTitle;
	}
	public void setAdvTitle(String advTitle) {
		this.advTitle = advTitle;
	}
	public String getEventUrl() {
		return eventUrl;
	}
	public void setEventUrl(String eventUrl) {
		this.eventUrl = eventUrl;
	}
	
	
	
}
