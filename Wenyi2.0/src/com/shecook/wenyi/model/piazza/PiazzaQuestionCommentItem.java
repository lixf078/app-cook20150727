package com.shecook.wenyi.model.piazza;

import java.util.LinkedList;

public class PiazzaQuestionCommentItem extends PiazzaQuestionItem{
	private String id;
	private String topicid;
	private String uid;
	private String nickname;
	private String uportrait;
	private String comment;
	private String floor;
	private String comments;
	private String timeline;
	private LinkedList<PiazzaQuestionCommentItem> comment_items;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTopicid() {
		return topicid;
	}

	public void setTopicid(String topicid) {
		this.topicid = topicid;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
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

	public LinkedList<PiazzaQuestionCommentItem> getComment_items() {
		if(comment_items == null){
			comment_items = new LinkedList<PiazzaQuestionCommentItem>();
		}
		return comment_items;
	}

	public void setComment_items(LinkedList<PiazzaQuestionCommentItem> comment_items) {
		this.comment_items = comment_items;
	}

	public PiazzaQuestionCommentItem(String id, String topicid, String uid,
			String nickname, String uportrait, String comment, String floor,
			String comments, String timeline, LinkedList<PiazzaQuestionCommentItem> comment_items) {
		super();
		this.id = id;
		this.topicid = topicid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.comment = comment;
		this.floor = floor;
		this.comments = comments;
		this.timeline = timeline;
		this.comment_items = comment_items;
	}

	public PiazzaQuestionCommentItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PiazzaQuestionCommentItem [id=" + id + ", topicid=" + topicid
				+ ", uid=" + uid + ", nickname=" + nickname + ", uportrait="
				+ uportrait + ", comment=" + comment + ", floor=" + floor
				+ ", comments=" + comments + ", timeline=" + timeline
				+ ", comment_items=" + comment_items + "]";
	}

}