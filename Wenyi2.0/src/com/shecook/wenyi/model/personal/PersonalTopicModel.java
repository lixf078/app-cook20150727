package com.shecook.wenyi.model.personal;

/**
 * 
 * @author lixufeng
 * 
 *         "list": [ { "uid": 1, "clickto": "article_comment",//点击后跳转对应的界面
 *         //存在8种类型的跳转 // article(随笔详情) | article_comment(随笔二级评论列表) |
 *         recipe(菜谱详情) | recipe_comment(菜谱二级评论列表) | follow(作业详情) |
 *         follow_comment(作业二级评论列表) | topic(话题详情) | topic_comment(话题二级评论列表)
 *         "mainid": 1264, "commentid": 1, //结合clickto 举例 如为article_comment 则调用
 *         article/comment_list 接口 对应接口中传递 articleid:mainid(1264),
 *         commentid:commentid(1)
 * 
 *         "top_nickname": "豆泥丸",//我的昵称 "top_imageurl": "static/u/0.jpg",//我的头像
 *         "top_desc": "我来输入文字发表一条新的对评论的评论啊啊啊",//我发出的评论 "bottom_nickname":
 *         "狮子座老虎",//被评论人的昵称 为空显示空字符串 "bottom_imageurl":
 *         "http://img2.shecook.com/members/7d202c92b79647e7ba825c6a987b01c0/original/0.jpg"
 *         ,//被评论人的头像 "bottom_desc": "学习",//配评论的内容 "_id":
 *         "121a62ff164240ffaebd5b1ead7c55c6", "timeline":
 *         "2015-09-20 18:46:47"//时间戳 }, ],
 */

public class PersonalTopicModel {
	private String _id;
	private String uid;
	private String clickto;
	private String mainid;
	private String commentid;
	private String top_nickname;
	private String top_imageurl;
	private String top_desc;
	private String bottom_nickname;
	private String bottom_imageurl;
	private String bottom_desc;
	private String groupname;
	private String timeline;

	public PersonalTopicModel() {
		super();
	}

	public PersonalTopicModel(String _id, String uid, String clickto,
			String mainid, String commentid, String top_nickname,
			String top_imageurl, String top_desc, String bottom_nickname,
			String bottom_imageurl, String bottom_desc, String groupname, String timeline) {
		super();
		this._id = _id;
		this.uid = uid;
		this.clickto = clickto;
		this.mainid = mainid;
		this.commentid = commentid;
		this.top_nickname = top_nickname;
		this.top_imageurl = top_imageurl;
		this.top_desc = top_desc;
		this.bottom_nickname = bottom_nickname;
		this.bottom_imageurl = bottom_imageurl;
		this.bottom_desc = bottom_desc;
		this.timeline = timeline;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getClickto() {
		return clickto;
	}

	public void setClickto(String clickto) {
		this.clickto = clickto;
	}

	public String getMainid() {
		return mainid;
	}

	public void setMainid(String mainid) {
		this.mainid = mainid;
	}

	public String getCommentid() {
		return commentid;
	}

	public void setCommentid(String commentid) {
		this.commentid = commentid;
	}

	public String getTop_nickname() {
		return top_nickname;
	}

	public void setTop_nickname(String top_nickname) {
		this.top_nickname = top_nickname;
	}

	public String getTop_imageurl() {
		return top_imageurl;
	}

	public void setTop_imageurl(String top_imageurl) {
		this.top_imageurl = top_imageurl;
	}

	public String getTop_desc() {
		return top_desc;
	}

	public void setTop_desc(String top_desc) {
		this.top_desc = top_desc;
	}

	public String getBottom_nickname() {
		return bottom_nickname;
	}

	public void setBottom_nickname(String bottom_nickname) {
		this.bottom_nickname = bottom_nickname;
	}

	public String getBottom_imageurl() {
		return bottom_imageurl;
	}

	public void setBottom_imageurl(String bottom_imageurl) {
		this.bottom_imageurl = bottom_imageurl;
	}

	public String getBottom_desc() {
		return bottom_desc;
	}

	public void setBottom_desc(String bottom_desc) {
		this.bottom_desc = bottom_desc;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	@Override
	public String toString() {
		return "PersonalTopicModel [_id=" + _id + ", uid=" + uid + ", clickto="
				+ clickto + ", mainid=" + mainid + ", commentid=" + commentid
				+ ", top_nickname=" + top_nickname + ", top_imageurl="
				+ top_imageurl + ", top_desc=" + top_desc
				+ ", bottom_nickname=" + bottom_nickname + ", bottom_imageurl="
				+ bottom_imageurl + ", bottom_desc=" + bottom_desc
				+ ", groupname=" + groupname + "]";
	}

}
