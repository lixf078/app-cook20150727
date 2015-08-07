package com.shecook.wenyi.model;

import com.shecook.wenyi.R;

public class EssayListItem {
	private String id;
	private String cataid;
	private String title; // title,image,paragraph,subhead
	private String summ;
	private String event_type;
	private String event_content;
	private String iconurl;
	private String qkey;
	private boolean ontop;
	private String timeline;

	public EssayListItem(String id, String cataid, String title, String summ,
			String event_type, String event_content, String iconurl,
			String qkey, boolean ontop, String timeline) {
		super();
		this.id = id;
		this.cataid = cataid;
		this.title = title;
		this.summ = summ;
		this.event_type = event_type;
		this.event_content = event_content;
		this.iconurl = iconurl;
		this.qkey = qkey;
		this.ontop = ontop;
		this.timeline = timeline;
	}

	public EssayListItem() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCataid() {
		return cataid;
	}

	public void setCataid(String cataid) {
		this.cataid = cataid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSumm() {
		return summ;
	}

	public void setSumm(String summ) {
		this.summ = summ;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getEvent_content() {
		return event_content;
	}

	public void setEvent_content(String event_content) {
		this.event_content = event_content;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getQkey() {
		return qkey;
	}

	public void setQkey(String qkey) {
		this.qkey = qkey;
	}

	public boolean isOntop() {
		return ontop;
	}

	public void setOntop(boolean ontop) {
		this.ontop = ontop;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

	@Override
	public String toString() {
		return "EssayListItem [title=" + title + ", iconurl=" + iconurl + "]";
	}

}
