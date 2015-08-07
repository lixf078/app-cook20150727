package com.shecook.wenyi.model;

import com.shecook.wenyi.R;
import java.util.LinkedList;

public class CookbookCatalog {
	
	private String id;
	private String cataname;
	private String parentid;
	private LinkedList<CookbookCatalog> cata_items;

	public CookbookCatalog(String id, String cataname, String parentid,
			LinkedList<CookbookCatalog> cata_items) {
		super();
		this.id = id;
		this.cataname = cataname;
		this.parentid = parentid;
		this.cata_items = cata_items;
	}

	public CookbookCatalog() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCataname() {
		return cataname;
	}

	public void setCataname(String cataname) {
		this.cataname = cataname;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public LinkedList<CookbookCatalog> getCata_items() {
		return cata_items;
	}

	public void setCata_items(LinkedList<CookbookCatalog> cata_items) {
		this.cata_items = cata_items;
	}

}
