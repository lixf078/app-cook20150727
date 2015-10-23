package com.shecook.wenyi.model.personal;

import java.util.LinkedList;

import com.shecook.wenyi.model.WenyiImage;

public class PersonalTopicModel {

	private String id;
	private String uid;
	private String ugid; // title,image,paragraph,subhead
	private String nickname;
	private String uportrait;
	private String body;
	private String tags;
	private String comments;
	private String timeline;
	private LinkedList<WenyiImage> images;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUgid() {
		return ugid;
	}

	public void setUgid(String ugid) {
		this.ugid = ugid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUportrait() {
		return uportrait;
	}

	public void setUportrait(String uportrait) {
		this.uportrait = uportrait;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

	public LinkedList<WenyiImage> getImages() {
		return images;
	}

	public void setImages(LinkedList<WenyiImage> images) {
		this.images = images;
	}

	public PersonalTopicModel(String id, String uid, String ugid,
			String nickname, String uportrait, String body, String tags,
			String comments, String timeline, LinkedList<WenyiImage> images) {
		super();
		this.id = id;
		this.uid = uid;
		this.ugid = ugid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.body = body;
		this.tags = tags;
		this.comments = comments;
		this.timeline = timeline;
		this.images = images;
	}

	public PersonalTopicModel() {
		super();
	}
}
