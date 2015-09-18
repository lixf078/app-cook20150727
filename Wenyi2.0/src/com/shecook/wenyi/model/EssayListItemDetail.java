package com.shecook.wenyi.model;

public class EssayListItemDetail {
	private String id;
	private String cataid;
	private String title; // title,image,paragraph,subhead
	private String articleid;
	private String rowtype;// content(title,image,paragraph,subhead), commentOne, commentTwo
	private String rowcontent;
	private int width;
	private int height;

	public EssayListItemDetail() {
		super();
	}

	public EssayListItemDetail(String id, String cataid, String title,
			String articleid, String rowtype, String rowcontent, int width,
			int height) {
		super();
		this.id = id;
		this.cataid = cataid;
		this.title = title;
		this.articleid = articleid;
		this.rowtype = rowtype;
		this.rowcontent = rowcontent;
		this.width = width;
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCataid() {
		return cataid;
	}

	public void setCataid(String cataid) {
		this.cataid = cataid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArticleid() {
		return articleid;
	}

	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}

	public String getRowtype() {
		return rowtype;
	}

	public void setRowtype(String rowtype) {
		this.rowtype = rowtype;
	}

	public String getRowcontent() {
		return rowcontent;
	}

	public void setRowcontent(String rowcontent) {
		this.rowcontent = rowcontent;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
