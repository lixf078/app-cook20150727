package com.shecook.wenyi.model.cookbook;

import java.util.LinkedList;

import com.shecook.wenyi.model.CookbookHomeworkListItem;

public class SampleCookbookItemDetail extends CookBookModel{
	private String id;
	private String recipename;//菜谱名称
	private String summary; //描述
	private String imgoriginal; // 主图
	private String comments;
	private String follows;
	private String tag;
	private String timeline;
	private boolean collected; //布尔型，用于标识当前菜谱是否被当前用户所收藏
	private LinkedList<Indredients> indredients; //原料集合
	private LinkedList<Seasonings> seasonings; //调料集合
	private LinkedList<Content> contents; //"contents": [//内容集合
	private LinkedList<Note> notes; //超级啰嗦数据集;
	private LinkedList<CookbookComment> recipefollows; // 评论
	private LinkedList<CookbookHomeworkListItem> cookbookList; // 家庭作业

	public SampleCookbookItemDetail() {
		super();
	}

	public SampleCookbookItemDetail(String id, String recipename, String summary,
			String imgoriginal, String comments, String follows, String tag,
			String timeline, boolean collected,
			LinkedList<Indredients> indredients,
			LinkedList<CookbookHomeworkListItem> cookbookList,
			LinkedList<CookbookComment> recipefollows) {
		super();
		this.id = id;
		this.recipename = recipename;
		this.summary = summary;
		this.imgoriginal = imgoriginal;
		this.comments = comments;
		this.follows = follows;
		this.tag = tag;
		this.timeline = timeline;
		this.collected = collected;
		this.indredients = indredients;
		this.cookbookList = cookbookList;
		this.recipefollows = recipefollows;
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

	public boolean isCollected() {
		return collected;
	}

	public void setCollected(boolean collected) {
		this.collected = collected;
	}

	public LinkedList<Indredients> getIndredients() {
		return indredients;
	}

	public void setIndredients(LinkedList<Indredients> indredients) {
		this.indredients = indredients;
	}

	public LinkedList<CookbookHomeworkListItem> getCookbookList() {
		return cookbookList;
	}

	public void setCookbookList(
			LinkedList<CookbookHomeworkListItem> cookbookList) {
		this.cookbookList = cookbookList;
	}

	public LinkedList<CookbookComment> getRecipefollows() {
		return recipefollows;
	}

	public void setRecipefollows(LinkedList<CookbookComment> recipefollows) {
		this.recipefollows = recipefollows;
	}

	class CookbookItemContentUnit {
		private String id;
		private String recipeid;
	}

	class Indredients extends CookbookItemContentUnit {
		private String ingredients; // title,image,paragraph,subhead
		private String unit;
	}

	class Seasonings extends CookbookItemContentUnit {
		private String seasoning; // title,image,paragraph,subhead
		private String unit;
	}

	class Content extends CookbookItemContentUnit {
		private String rowtype; // title,image,paragraph,subhead
		private String rowcontent;
	}

	class Note extends CookbookItemContentUnit {
		private String rowtype; // title,image,paragraph,subhead
		private String rowcontent;
	}
}
