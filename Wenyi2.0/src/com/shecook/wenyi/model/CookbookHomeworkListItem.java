package com.shecook.wenyi.model;

import java.util.ArrayList;

import com.shecook.wenyi.model.cookbook.CookBookModel;

public class CookbookHomeworkListItem extends CookBookModel{

	private String id;// 菜谱主id
	private String recipeid;
	private String uid; // 用户主标识
	private String nickname; // 昵称
	private String uportrait;// 头像
	private String description; // 作业描述
	private String comments; // 评论数
	private String timeline;
	private ArrayList<WenyiImage> imageList;

	public CookbookHomeworkListItem() {
		super();
	}

	public CookbookHomeworkListItem(String id, String recipeid, String uid,
			String nickname, String uportrait, String description,
			String comments, String timeline, ArrayList<WenyiImage> imageList) {
		super();
		this.id = id;
		this.recipeid = recipeid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.description = description;
		this.comments = comments;
		this.timeline = timeline;
		this.imageList = imageList;
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

	public ArrayList<WenyiImage> getImageList() {
		if(imageList == null){
			imageList = new ArrayList<WenyiImage>();
		}
		return imageList;
	}

	public void setImageList(ArrayList<WenyiImage> imageList) {
		this.imageList = imageList;
	}

	@Override
	public String toString() {
		return "CookbookHomeworkListItem [id=" + id + ", recipeid=" + recipeid
				+ ", uid=" + uid + ", nickname=" + nickname + ", uportrait="
				+ uportrait + ", description=" + description + ", comments="
				+ comments + ", timeline=" + timeline + ", imageList="
				+ imageList + "]";
	}
}
