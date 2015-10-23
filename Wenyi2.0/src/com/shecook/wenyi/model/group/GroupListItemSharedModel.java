package com.shecook.wenyi.model.group;

import java.util.LinkedList;

import com.shecook.wenyi.model.WenyiImage;

public class GroupListItemSharedModel {
	private String id;
	private String uid;
	private String circleid;
	private String nickname;
	private String uportrait;
	private String body;
	private String comments;
	private String timeline;
	private int del;
	private LinkedList<WenyiImage> images;
	
	private int status; // used for group mem

	public GroupListItemSharedModel() {
		super();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

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

	public String getCircleid() {
		return circleid;
	}

	public void setCircleid(String circleid) {
		this.circleid = circleid;
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

	public int getDel() {
		return del;
	}

	public void setDel(int del) {
		this.del = del;
	}

	public LinkedList<WenyiImage> getImages() {
		if(images == null){
			images = new LinkedList<WenyiImage>();
		}
		return images;
	}

	public void setImages(LinkedList<WenyiImage> images) {
		this.images = images;
	}

	public GroupListItemSharedModel(String id, String uid, String circleid,
			String nickname, String uportrait, String body, String comments,
			String timeline, int del, LinkedList<WenyiImage> images, int status) {
		super();
		this.id = id;
		this.uid = uid;
		this.circleid = circleid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.body = body;
		this.comments = comments;
		this.timeline = timeline;
		this.del = del;
		this.images = images;
		this.status = status;
	}

	@Override
	public String toString() {
		return "GroupListItemSharedModel [id=" + id + ", uid=" + uid
				+ ", circleid=" + circleid + ", nickname=" + nickname
				+ ", uportrait=" + uportrait + ", body=" + body + ", comments="
				+ comments + ", timeline=" + timeline + ", del=" + del
				+ ", images=" + images + ",status " + status + "]";
	}
}
