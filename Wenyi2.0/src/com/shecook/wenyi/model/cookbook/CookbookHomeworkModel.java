package com.shecook.wenyi.model.cookbook;

import java.util.LinkedList;

import com.shecook.wenyi.model.WenyiGallery;

public class CookbookHomeworkModel {
	
	/**
	 * "detail": {
            "userlvl": "厨房小毛猴",//等级名称
            "recipename": "五彩肉皮冻",//关联菜谱名称
            "id": 27645,
            "recipeid": 3133,//菜谱主id，点击跳转回菜谱  当为0时不显示原菜谱信息
            "uid": 0,
            "nickname": "ahuier",//用户昵称
            "uportrait": "http://qzapp.qlogo.cn/qzapp/100487086/2F566DA04A7783BCF7D9E7F76A8129C1/100",//用户头像
            "description": "灰常不错哦！",//描述
            "comments": 0,//评论数
            "timeline": "2013-10-23 01:41:20",//时间
            "images": [ //图片集合 多张图采用滑动切换效果
            {
            "id": 27644,
            "followid": 27645,
            "imageurl": "http://static.wenyijcc.com/submit/1aed1256696b4c9ca7663fdd23639018.jpg"//图片地址
            }
	 */
	
	private String userlvl;
	private String recipename;
	private String id;
	private String recipeid;
	private String uid;
	private String nickname;
	private String uportrait;
	private String description;
	private String comments;
	private String timeline;
	private LinkedList<WenyiGallery> images;
	private boolean isComment;

	public CookbookHomeworkModel() {
		super();
	}

	public CookbookHomeworkModel(String userlvl, String recipename, String id,
			String recipeid, String uid, String nickname, String uportrait,
			String description, String comments, String timeline, LinkedList<WenyiGallery> images, boolean isComment) {
		super();
		this.userlvl = userlvl;
		this.recipename = recipename;
		this.id = id;
		this.recipeid = recipeid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.description = description;
		this.comments = comments;
		this.timeline = timeline;
		this.images = images;
		this.isComment = isComment;
	}

	public String getUserlvl() {
		return userlvl;
	}

	public void setUserlvl(String userlvl) {
		this.userlvl = userlvl;
	}

	public String getRecipename() {
		return recipename;
	}

	public void setRecipename(String recipename) {
		this.recipename = recipename;
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

	public LinkedList<WenyiGallery> getImages() {
		if(null == images){
			images = new LinkedList<WenyiGallery>();
		}
		return images;
	}

	public void setImages(LinkedList<WenyiGallery> images) {
		this.images = images;
	}
	
	public boolean isComment() {
		return isComment;
	}

	public void setComment(boolean isComment) {
		this.isComment = isComment;
	}

	@Override
	public String toString() {
		return "CookbookHomework [userlvl=" + userlvl + ", recipename="
				+ recipename + ", id=" + id + ", recipeid=" + recipeid
				+ ", uid=" + uid + ", nickname=" + nickname + ", uportrait="
				+ uportrait + ", description=" + description + ", comments="
				+ comments + ", images=" + images + "]";
	}
	
}
