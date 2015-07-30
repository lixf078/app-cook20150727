package com.shecook.wenyi.model;

public class EssayListItemDetail {
	private String id;
	private String cataid;
	private String title; // title,image,paragraph,subhead
	private String articleid;
	private String rowtype;
	private String rowcontent;

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

	public EssayListItemDetail(String id, String cataid, String title,
			String articleid, String rowtype, String rowcontent) {
		super();
		this.id = id;
		this.cataid = cataid;
		this.title = title;
		this.articleid = articleid;
		this.rowtype = rowtype;
		this.rowcontent = rowcontent;
	}

	public EssayListItemDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "EssayListItemDetail [id=" + id + ", cataid=" + cataid
				+ ", title=" + title + ", articleid=" + articleid
				+ ", rowtype=" + rowtype + ", rowcontent=" + rowcontent + "]";
	}

}
