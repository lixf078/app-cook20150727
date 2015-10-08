package com.shecook.wenyi.model.cookbook;

public class CookbookCollectionGroup {
	private String id;
	private String uid;
	private String groupname;
	private String timeline;

	public CookbookCollectionGroup() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CookbookCollectionGroup(String id, String uid, String groupname,
			String timeline) {
		super();
		this.id = id;
		this.uid = uid;
		this.groupname = groupname;
		this.timeline = timeline;
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

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getTimeline() {
		return timeline;
	}

	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}

	@Override
	public String toString() {
		return "CookbookCollectionGroup [id=" + id + ", uid=" + uid
				+ ", groupname=" + groupname + ", timeline=" + timeline + "]";
	}

}
