package com.shecook.wenyi;

public class HttpUrls {
	public static final String BASE_URL = "http://api.wenyijcc.com/";
	public static final String GET_TOKEN = BASE_URL + "user/get_token";

	// presonal
	public static final String PERSONAL_USER_LOGOUT = BASE_URL + "user/logout";
	public static final String PERSONAL_REGISTER = BASE_URL + "user/reg";
	public static final String PERSONAL_LOGIN = BASE_URL + "user/login";
	
	public static final String PERSONAL_LOGIN_3RD = BASE_URL + "user/connect";
	public static final String ESSAY_WENYI_LIST_CATALOG = BASE_URL + "article/catalog";
	public static final String ESSAY_WENYI_LIST = BASE_URL + "article/list";
	public static final String ESSAY_WENYILIST_ITEM_DETAIL = BASE_URL + "article/detail";
	
	public static final String COOKBOOK_LIST_CATALOG = BASE_URL + "recipe/catalog";
	public static final String COOKBOOK_LIST = BASE_URL + "recipe/list";
	public static final String COOKBOOK_LIST_ITEM_DETAIL = BASE_URL + "recipe/detail";
	
//	public static final String PIZZA_CATALOG_
	
	public static final String PERSONAL_MYCARD = BASE_URL + "user/mycard";
	
}