package com.shecook.wenyi.model.essay;

public class EssayGallery {
	public int id;
	public String title;
	public String imgUrl;
	public int event_type;
	public String event_content;
	public String timeline;

	public EssayGallery(int id, String title, String imgUrl, int event_type,
			String event_content, String timeline) {
		super();
		this.id = id;
		this.title = title;
		this.imgUrl = imgUrl;
		this.event_type = event_type;
		this.event_content = event_content;
		this.timeline = timeline;
	}

	public EssayGallery() {
		super();
	}

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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
