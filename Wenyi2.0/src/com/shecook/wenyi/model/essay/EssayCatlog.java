package com.shecook.wenyi.model.essay;

public class EssayCatlog {
	public int id;
	public String title;
	public String iconurl;
	public String sort;
	public boolean ispub;
	public int count;
	public int event_type;
	public String event_content;
	public String timeline;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isIspub() {
		return ispub;
	}

	public void setIspub(boolean ispub) {
		this.ispub = ispub;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getEvent_type() {
		return event_type;
	}

	public void setEvent_type(int event_type) {
		this.event_type = event_type;
	}

	public String getEvent_content() {
		return event_content;
	}

	public void setEvent_content(String event_content) {
		this.event_content = event_content;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

}
