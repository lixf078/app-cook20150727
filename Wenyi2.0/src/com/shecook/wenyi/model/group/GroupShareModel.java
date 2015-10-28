package com.shecook.wenyi.model.group;

import java.util.LinkedList;

import com.shecook.wenyi.model.WenyiGallery;

public class GroupShareModel {
	
	/**
	 * "lvlname": "厨房狮子王",
        "detail": {
            "id": 1,
            "circleid": 1,
            "uid": 1,
            "nickname": "豆泥丸",
            "uportrait": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "body": "这是说明文字",
            "comments": 0,
            "timeline": "2015-10-06 22:23:13",
            "del": 0,
        "images": [
            {
            "id": 1,
            "circleid": 1,
            "shareid": 1,
            "imageurl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "cuturl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "thumbnailurl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "originalurl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1"
            },
            {
            "id": 2,
            "circleid": 1,
            "shareid": 1,
            "imageurl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "cuturl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "thumbnailurl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1",
            "originalurl": "http://tp4.sinaimg.cn/1318266875/50/5735178339/1"
            }
        ]
        },
	 */
	
	private String lvlname;
	private String id;
	private String circleid;
	private String uid;
	private String nickname;
	private String uportrait;
	private String body;
	private String comments;
	private String timeline;
	private LinkedList<WenyiGallery> images;
	private boolean isComment;

	public GroupShareModel() {
		super();
	}

	public String getLvlname() {
		return lvlname;
	}

	public void setLvlname(String lvlname) {
		this.lvlname = lvlname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCircleid() {
		return circleid;
	}

	public void setCircleid(String circleid) {
		this.circleid = circleid;
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

	public LinkedList<WenyiGallery> getImages() {
		if(images == null){
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

	public GroupShareModel(String lvlname, String id, String circleid,
			String uid, String nickname, String uportrait, String body,
			String comments, String timeline, LinkedList<WenyiGallery> images,
			boolean isComment) {
		super();
		this.lvlname = lvlname;
		this.id = id;
		this.circleid = circleid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.body = body;
		this.comments = comments;
		this.timeline = timeline;
		this.images = images;
		this.isComment = isComment;
	}

	
}
