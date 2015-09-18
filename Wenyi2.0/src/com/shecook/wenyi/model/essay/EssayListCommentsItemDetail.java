package com.shecook.wenyi.model.essay;

import java.util.ArrayList;

import com.shecook.wenyi.model.EssayListItemDetail;

public class EssayListCommentsItemDetail extends EssayListItemDetail {
	private String id;//本条评论主id 在发布评论的对应接口中传递的commentid 为此值
	private String articleid;//本条文章的id
	private String commentid;// 如果是二级评论，则为所属的一级评论id
	private String uid;//用户唯一标识id
	private String nickname;//当前评论人昵称
	private String uportrait;//头像
	private String comment;//评论内容
	private String floor;
	private String comments;//本条一级评论所拥有的2级评论数
	private String timeline;//评论时间 
	private ArrayList<EssayListCommentsItemDetail> comment_items;//本条一级评论的二级评论 在一级评论列表中，每个评论节点只最多返回3条附属评论 

	public EssayListCommentsItemDetail(String id, String articleid, String commentid, String uid,
			String nickname, String uportrait, String comment, String floor,
			String comments, String timeline,
			ArrayList<EssayListCommentsItemDetail> comment_items) {
		super();
		this.id = id;
		this.articleid = articleid;
		this.commentid = commentid;
		this.uid = uid;
		this.nickname = nickname;
		this.uportrait = uportrait;
		this.comment = comment;
		this.floor = floor;
		this.comments = comments;
		this.timeline = timeline;
		this.comment_items = comment_items;
	}

	public EssayListCommentsItemDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArticleid() {
		return articleid;
	}

	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}

	public String getCommentid() {
		return commentid;
	}

	public void setCommentid(String commentid) {
		this.commentid = commentid;
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

	public ArrayList<EssayListCommentsItemDetail> getComment_items() {
		if(null == comment_items){
			comment_items = new ArrayList<EssayListCommentsItemDetail>();
		}
		return comment_items;
	}

	public void setComment_items(
			ArrayList<EssayListCommentsItemDetail> comment_items) {
		this.comment_items = comment_items;
	}

	@Override
	public String toString() {
		return "EssayListCommentsItemDetail [id=" + id + ", articleid="
				+ articleid + ", uid=" + uid + ", nickname=" + nickname
				+ ", uportrait=" + uportrait + ", comment=" + comment
				+ ", floor=" + floor + ", comments=" + comments + ", timeline="
				+ timeline + ", comment_items=" + comment_items + "]";
	}

}
