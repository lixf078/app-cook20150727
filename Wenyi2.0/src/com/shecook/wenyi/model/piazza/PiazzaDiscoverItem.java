package com.shecook.wenyi.model.piazza;

import java.util.Arrays;


public class PiazzaDiscoverItem {

	private String title;
	private String imageurl;
	private String desc;
	private int img_width;
	private int img_height;
	private String[] image_items;

	private String _id;
	private String objid;
	private String template;
	private String event_type;
	private String event_content;
	private boolean istop;
	private String keyword;
	private String timeline;

	public PiazzaDiscoverItem() {
		super();
	}

	public PiazzaDiscoverItem(String title, String imageurl, String desc,
			int img_width, int img_height, String[] image_items, String _id,
			String objid, String template, String event_type, boolean istop,
			String keyword, String timeline, String event_content) {
		super();
		this.title = title;
		this.imageurl = imageurl;
		this.desc = desc;
		this.img_width = img_width;
		this.img_height = img_height;
		this.image_items = image_items;
		this._id = _id;
		this.objid = objid;
		this.template = template;
		this.event_type = event_type;
		this.istop = istop;
		this.keyword = keyword;
		this.timeline = timeline;
		this.event_content = event_content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getImg_width() {
		return img_width;
	}

	public void setImg_width(int img_width) {
		this.img_width = img_width;
	}

	public int getImg_height() {
		return img_height;
	}

	public void setImg_height(int img_height) {
		this.img_height = img_height;
	}

	public String[] getImage_items() {
		return image_items;
	}

	public void setImage_items(String[] image_items) {
		this.image_items = image_items;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getObjid() {
		return objid;
	}

	public void setObjid(String objid) {
		this.objid = objid;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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

	public boolean isIstop() {
		return istop;
	}

	public void setIstop(boolean istop) {
		this.istop = istop;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

	@Override
	public String toString() {
		return "PiazzaDiscoverItem [title=" + title + ", imageurl=" + imageurl
				+ ", desc=" + desc + ", img_width=" + img_width
				+ ", img_height=" + img_height + ", image_items="
				+ Arrays.toString(image_items) + ", _id=" + _id + ", objid="
				+ objid + ", template=" + template + ", event_type="
				+ event_type + ", istop=" + istop + ", keyword=" + keyword
				+ ", timeline=" + timeline + "]";
	}

	
}