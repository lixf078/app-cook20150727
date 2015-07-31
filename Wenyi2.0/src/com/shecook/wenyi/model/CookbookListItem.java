package com.shecook.wenyi.model;

public class CookbookListItem {
	
	private String id;// 菜谱主id
	private String recipename;// 菜谱标题 爽口茼蒿
	private String summary; // 菜谱摘要 可口，非常好吃的菜哦
	private String imgoriginal;// 列表缩略图，正方形
	private String imgthumbnail;
	private String comments;
	private String follows;
	private String tag; // 文字标签 我爱素菜
	private String timeline;

	public CookbookListItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CookbookListItem(String id, String recipename, String summary,
			String imgoriginal, String imgthumbnail, String comments,
			String follows, String tag, String timeline) {
		super();
		this.id = id;
		this.recipename = recipename;
		this.summary = summary;
		this.imgoriginal = imgoriginal;
		this.imgthumbnail = imgthumbnail;
		this.comments = comments;
		this.follows = follows;
		this.tag = tag;
		this.timeline = timeline;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecipename() {
		return recipename;
	}

	public void setRecipename(String recipename) {
		this.recipename = recipename;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getImgoriginal() {
		return imgoriginal;
	}

	public void setImgoriginal(String imgoriginal) {
		this.imgoriginal = imgoriginal;
	}

	public String getImgthumbnail() {
		return imgthumbnail;
	}

	public void setImgthumbnail(String imgthumbnail) {
		this.imgthumbnail = imgthumbnail;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getFollows() {
		return follows;
	}

	public void setFollows(String follows) {
		this.follows = follows;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

}
