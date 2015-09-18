package com.shecook.wenyi.model.cookbook;


public class SingleCookbookItemDetail extends CookBookModel{
	private String recipename;//菜谱名称
	private String comments;
	private String follows;
	private String tag;
	private String timeline;
	private boolean collected; //布尔型，用于标识当前菜谱是否被当前用户所收藏
}
