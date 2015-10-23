package com.shecook.wenyi.model.group;

public class GroupHotListItem {
	private String id;
	private String uid;
	private String ufounder; // title,image,paragraph,subhead
	private String uportrait;
	private String title;
	private String description;
	private String iconurl;
	private String totalnum;
	private String currentnum;
	private String share;
	private String datecreated;
	private String dateupd;
	private boolean ontop;
	private int status;

	public GroupHotListItem() {
		super();
	}

	public GroupHotListItem(String id, String uid, String ufounder,String uportrait,
			String title, String description, String iconurl, String totalnum,
			String currentnum, String share, String datecreated,
			String dateupd, boolean ontop, int status) {
		super();
		this.id = id;
		this.uid = uid;
		this.ufounder = ufounder;
		this.uportrait = uportrait;
		this.title = title;
		this.description = description;
		this.iconurl = iconurl;
		this.totalnum = totalnum;
		this.currentnum = currentnum;
		this.share = share;
		this.datecreated = datecreated;
		this.dateupd = dateupd;
		this.ontop = ontop;
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getUfounder() {
		return ufounder;
	}

	public void setUfounder(String ufounder) {
		this.ufounder = ufounder;
	}

	public String getUportrait() {
		return uportrait;
	}

	public void setUportrait(String uportrait) {
		this.uportrait = uportrait;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(String totalnum) {
		this.totalnum = totalnum;
	}

	public String getCurrentnum() {
		return currentnum;
	}

	public void setCurrentnum(String currentnum) {
		this.currentnum = currentnum;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getDatecreated() {
		return datecreated;
	}

	public void setDatecreated(String datecreated) {
		this.datecreated = datecreated;
	}

	public String getDateupd() {
		return dateupd;
	}

	public void setDateupd(String dateupd) {
		this.dateupd = dateupd;
	}

	public boolean isOntop() {
		return ontop;
	}

	public void setOntop(boolean ontop) {
		this.ontop = ontop;
	}

}
