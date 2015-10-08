package com.shecook.wenyi.model.personal;

public class PersonalCollectionModel {
	private String id;
	private String recipeid;
	private String recipename; // title,image,paragraph,subhead
	private String uid;
	private String groupid;
	private String timeline;
	// 以下为菜谱实体，个别字段没有使用
	private String summary;
	private String imgoriginal;
	private String imgthumbnail;
	private String comments;
	private String tag;
	private int timg_width;
	private int timg_height;
	private String recipetimeline;
	private String groupname;

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

	public String getRecipename() {
		return recipename;
	}

	public void setRecipename(String recipename) {
		this.recipename = recipename;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getTimg_width() {
		return timg_width;
	}

	public void setTimg_width(int timg_width) {
		this.timg_width = timg_width;
	}

	public int getTimg_height() {
		return timg_height;
	}

	public void setTimg_height(int timg_height) {
		this.timg_height = timg_height;
	}

	public String getRecipetimeline() {
		return recipetimeline;
	}

	public void setRecipetimeline(String recipetimeline) {
		this.recipetimeline = recipetimeline;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public PersonalCollectionModel(String id, String recipeid,
			String recipename, String uid, String groupid, String timeline,
			String summary, String imgoriginal, String imgthumbnail,
			String comments, String tag, int timg_width, int timg_height,
			String recipetimeline, String groupname) {
		super();
		this.id = id;
		this.recipeid = recipeid;
		this.recipename = recipename;
		this.uid = uid;
		this.groupid = groupid;
		this.timeline = timeline;
		this.summary = summary;
		this.imgoriginal = imgoriginal;
		this.imgthumbnail = imgthumbnail;
		this.comments = comments;
		this.tag = tag;
		this.timg_width = timg_width;
		this.timg_height = timg_height;
		this.recipetimeline = recipetimeline;
		this.groupname = groupname;
	}

	public PersonalCollectionModel() {
		super();
	}

	@Override
	public String toString() {
		return "PersonalCollectionModel [id=" + id + ", recipeid=" + recipeid
				+ ", recipename=" + recipename + ", uid=" + uid + ", groupid="
				+ groupid + ", timeline=" + timeline + ", summary=" + summary
				+ ", imgoriginal=" + imgoriginal + ", imgthumbnail="
				+ imgthumbnail + ", comments=" + comments + ", tag=" + tag
				+ ", timg_width=" + timg_width + ", timg_height=" + timg_height
				+ ", recipetimeline=" + recipetimeline + ", groupname="
				+ groupname + "]";
	}

}
