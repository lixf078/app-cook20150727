package com.shecook.wenyi.model.personal;

import java.util.ArrayList;


public class PersonalHomeworkItem {
	private String id; // 主Id,删除需传递此参数
	private String recipeid;
	private String uid; // 用户主标识
	private String nickname; // 昵称 
	private String uportrait; // 头像
	private String description; // 作业描述
	private String comments; // 评论数 
	private String timeline; // 时间
	private ArrayList<HomeWorkImage> images; // 图片集合，默认第一张

	public PersonalHomeworkItem() {
		super();
		images = new ArrayList<HomeWorkImage>();
	}

	public PersonalHomeworkItem(String id, String recipeid, String uid,
			String nickname, String uportrait, String description,
			String comments, String timeline, ArrayList<HomeWorkImage> images) {
		super();
		this.id = id;
		this.recipeid = recipeid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.description = description;
		this.comments = comments;
		this.timeline = timeline;
		this.images = images;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecipeid() {
		return recipeid;
	}

	public void setRecipeid(String recipeid) {
		this.recipeid = recipeid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public ArrayList<HomeWorkImage> getImages() {
		return images;
	}

	public void setImages(ArrayList<HomeWorkImage> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "PersonalHomeworkItem [id=" + id + ", recipeid=" + recipeid
				+ ", uid=" + uid + ", nickname=" + nickname + ", uportrait="
				+ uportrait + ", description=" + description + ", comments="
				+ comments + ", timeline=" + timeline + ", images=" + images
				+ "]";
	}
	
}