package com.shecook.wenyi.model.cookbook;

public class CookbookComment extends CookBookModel{
	private String uid;
	private String nickname;
	private String uportrait;
	private String comment;
	private String floor;
	private String comments;
	private String timeline;

	public CookbookComment() {
		super();
	}

	public CookbookComment(String id, String recipeid, String uid,
			String nickname, String uportrait, String comment, String floor,
			String comments, String timeline) {
		super();
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.comment = comment;
		this.floor = floor;
		this.comments = comments;
		this.timeline = timeline;
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

	@Override
	public String toString() {
		return "CookbookComment [uid=" + uid + ", nickname=" + nickname
				+ ", uportrait=" + uportrait + ", comment=" + comment
				+ ", floor=" + floor + ", comments=" + comments + ", timeline="
				+ timeline + ", getId()=" + getId() + ", getRecipeid()="
				+ getRecipeid() + ", getRowType()=" + getRowType()
				+ ", getRowContent()=" + getRowContent() + "]";
	}

}
