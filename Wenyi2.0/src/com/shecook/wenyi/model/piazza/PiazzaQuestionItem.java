package com.shecook.wenyi.model.piazza;

import java.util.ArrayList;

import com.shecook.wenyi.model.WenyiImage;

public class PiazzaQuestionItem {
	private String id; // 主Id,删除需传递此参数
	private String uid; // 用户主标识
	private String ugid;
	private String tags;
	private String nickname; // 昵称
	private String uportrait; // 头像
	private String body; // 内容
	private String comments; // 评论数
	private String timeline; // 时间
	private String lvlname; // 等级
	private ArrayList<WenyiImage> images; // 图片集合，默认第一张

	public boolean isComment;
	
	public boolean isComment() {
		return isComment;
	}

	public void setComment(boolean isComment) {
		this.isComment = isComment;
	}

	public PiazzaQuestionItem() {
		super();
		images = new ArrayList<WenyiImage>();
	}

	public PiazzaQuestionItem(String id, String uid, String ugid,
			String tags, String nickname, String uportrait, String body,
			String comments, String timeline, String lvlname, ArrayList<WenyiImage> images, boolean isComment) {
		super();
		this.id = id;
		this.uid = uid;
		this.ugid = ugid;
		this.tags = tags;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.body = body;
		this.comments = comments;
		this.timeline = timeline;
		this.lvlname = lvlname;
		this.images = images;
		this.isComment = isComment;
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

	public String getUgid() {
		return ugid;
	}

	public void setUgid(String ugid) {
		this.ugid = ugid;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
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
	
	public String getLvlname() {
		return lvlname;
	}

	public void setLvlname(String lvlname) {
		this.lvlname = lvlname;
	}

	public ArrayList<WenyiImage> getImages() {
		return images;
	}

	public void setImages(ArrayList<WenyiImage> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "PersonalHomeworkItem [id=" + id + ", uid=" + uid + ", ugid="
				+ ugid + ", tags=" + tags + ", nickname=" + nickname
				+ ", uportrait=" + uportrait + ", body=" + body + ", comments="
				+ comments + ", timeline=" + timeline + ", lvlname " + lvlname + ", images=" + images
				+ "]";
	}

}