package com.shecook.wenyi.model.cookbook;

public class CookBookModel {
	
	private String id;
	private String recipeid;
	private String rowType;// title, imgoriginal, indredients, seasonings, contents, notes, recipecomments
	private String rowContent = ""; // title, 主图, 调料，原料等
	
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
	public String getRowType() {
		return rowType;
	}
	public void setRowType(String rowType) {
		this.rowType = rowType;
	}
	public String getRowContent() {
		return rowContent;
	}
	public void setRowContent(String rowContent) {
		this.rowContent += rowContent;
	}
	
	
}
