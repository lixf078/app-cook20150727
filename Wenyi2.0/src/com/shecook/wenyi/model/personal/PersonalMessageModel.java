package com.shecook.wenyi.model.personal;

/**
 * 
 * @author lixufeng
 * 
 *         {
        "uid": 1,
        "clickto": "article",
    
        //存在5种类型的跳转
        1)article(随笔详情)
        2)recipe(菜谱详情)
        3) topic(话题详情)
        4)follow(作业详情)
        5)share(圈子分享详情)       
        "mainid": 1264,
        "commentid": 1,
        //结合clickto 举例 如为article_comment 则调用 article/comment_list 接口 对应接口中传递 articleid:mainid(1264), commentid:commentid(1)
        //                 如为 article 则调用 article/detail 接口 跳转到随笔详情页 对应接口中传递 articleid:mainid(1264)
        "from_nickname": "豆泥丸",
        "from_imageurl": "static/u/0.jpg",
        "from_desc": "我来输入文字发表一条新的对评论的评论啊啊啊",
        "_id": "81bdbbb38d8e4012976e7e89ded9ec2d",
        "timeline": "2015-09-20 18:46:48"
        }
 */

public class PersonalMessageModel {
	private String _id;
	private String uid;
	private String clickto;
	private String mainid;
	private String commentid;
	private String from_nickname;
	private String from_imageurl;
	private String from_desc;
	private String timeline;

	public PersonalMessageModel() {
		super();
	}

	public PersonalMessageModel(String _id, String uid, String clickto,
			String mainid, String commentid, String from_nickname,
			String from_imageurl, String from_desc, String timeline) {
		super();
		this._id = _id;
		this.uid = uid;
		this.clickto = clickto;
		this.mainid = mainid;
		this.commentid = commentid;
		this.from_nickname = from_nickname;
		this.from_imageurl = from_imageurl;
		this.from_desc = from_desc;
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

	public String getFrom_nickname() {
		return from_nickname;
	}

	public void setFrom_nickname(String from_nickname) {
		this.from_nickname = from_nickname;
	}

	public String getFrom_imageurl() {
		return from_imageurl;
	}

	public void setFrom_imageurl(String from_imageurl) {
		this.from_imageurl = from_imageurl;
	}

	public String getFrom_desc() {
		return from_desc;
	}

	public void setFrom_desc(String from_desc) {
		this.from_desc = from_desc;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

}
