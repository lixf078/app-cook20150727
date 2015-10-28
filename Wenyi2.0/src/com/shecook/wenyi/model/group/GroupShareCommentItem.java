package com.shecook.wenyi.model.group;

import java.util.LinkedList;

public class GroupShareCommentItem {
	
	/**
	 * {
        "id": 3,//本条评论主id 在发布评论的对应接口中传递的commentid 为此值
        "shareid": 1265,//本条分享的id
        "uid": 0,//用户唯一标识id
        "nickname": "日安-微光",//当前评论人昵称
        "uportrait": "http://tp2.sinaimg.cn/3486167073/180/40024077453/0",//头像
        "comment": "吃了n斤的橙子，到今天才学会吃橙子",//评论内容
        "floor": 0,
        "comments": 2,//本条一级评论所拥有的2级评论数
        "timeline": "2013-12-29 23:23:38",//评论时间
        "comment_items": [//本条一级评论的二级评论 在一级评论列表中，每个评论节点只最多返回3条附属评论
        {
        "id": 1,
        "shareid": 1265,
        "commentid": 3,
        "uid": 0,//用户唯一标识Id
        "nickname": "王大头",//昵称
        "uportrait": "http://tp4.sinaimg.cn/2269598527/180/5707900439/1",//头像
        "comment": "哈哈哈，你真能吃啊",//评论内容
        "timeline": "2015-09-07 00:00:00"
        },
	 */
	private String id;
	private String shareid;
	private String commentid;
	private String uid;
	private String nickname;
	private String uportrait;
	private String comment;
	private String floor;
	private String comments;
	private String timeline;
	private LinkedList<GroupShareCommentItem> comment_items;
	public boolean isComment;
	
	public boolean isComment() {
		return isComment;
	}

	public void setComment(boolean isComment) {
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

	public LinkedList<GroupShareCommentItem> getComment_items() {
		if (comment_items == null) {
			comment_items = new LinkedList<GroupShareCommentItem>();
		}
		return comment_items;
	}

	public void setComment_items(LinkedList<GroupShareCommentItem> comment_items) {
		this.comment_items = comment_items;
	}

	public GroupShareCommentItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getShareid() {
		return shareid;
	}

	public void setShareid(String shareid) {
		this.shareid = shareid;
	}

	public String getCommentid() {
		return commentid;
	}

	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}

	public GroupShareCommentItem(String id, String shareid, String commentid,
			String uid, String nickname, String uportrait, String comment,
			String floor, String comments, String timeline,
			LinkedList<GroupShareCommentItem> comment_items, boolean isComment) {
		super();
		this.id = id;
		this.shareid = shareid;
		this.commentid = commentid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.comment = comment;
		this.floor = floor;
		this.comments = comments;
		this.timeline = timeline;
		this.comment_items = comment_items;
		this.isComment = isComment;
	}

}