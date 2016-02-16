package com.shecook.wenyi.model;

public class WenyiImage{

	private String id;
	private String followid;
	private String imageurl;
	private String cuturl;
	private String thumbnailurl;
	private String originalurl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFollowid() {
		return followid;
	}

	public void setFollowid(String followid) {
		this.followid = followid;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getCuturl() {
		return cuturl;
	}

	public void setCuturl(String cuturl) {
		this.cuturl = cuturl;
	}

	public String getThumbnailurl() {
		return thumbnailurl;
	}

	public void setThumbnailurl(String thumbnailurl) {
		this.thumbnailurl = thumbnailurl;
	}

	public String getOriginalurl() {
		return originalurl;
	}

	public void setOriginalurl(String originalurl) {
		this.originalurl = originalurl;
	}

	public WenyiImage(String id, String followid, String imageurl,
			String cuturl, String thumbnailurl, String originalurl) {
		super();
		this.id = id;
		this.followid = followid;
		this.imageurl = imageurl;
		this.cuturl = cuturl;
		this.thumbnailurl = thumbnailurl;
		this.originalurl = originalurl;
	}

	public WenyiImage() {
		super();
	}
}
