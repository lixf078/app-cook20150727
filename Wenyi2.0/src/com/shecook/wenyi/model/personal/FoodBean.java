package com.shecook.wenyi.model.personal;

import java.io.Serializable;
import java.util.LinkedList;

public class FoodBean implements Serializable{
	private String id;
	private String name;
	private LinkedList<FoodBean> subList;
	private String catalogId;

	public FoodBean() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<FoodBean> getSubList() {
		if(subList == null){
			subList = new LinkedList<FoodBean>();
		}
		return subList;
	}

	public void setSubList(LinkedList<FoodBean> subList) {
		this.subList = subList;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public FoodBean(String id, String name, LinkedList<FoodBean> subList,
			String catalogId) {
		super();
		this.id = id;
		this.name = name;
		this.subList = subList;
		this.catalogId = catalogId;
	}

	@Override
	public String toString() {
		return "{name:\"" + name + "}";
	}
	
}
