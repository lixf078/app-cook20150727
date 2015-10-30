package com.shecook.wenyi;

public class HttpUrls {
	
	public static final String BASE_IMG_URL = "http://img.wenyijcc.com/";
	public static final String UPLOAD_IMG = BASE_IMG_URL + "sys/postpicture";
	
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
	
	public static final String ESSAY_WENYILIST_ITEM_DETAIL_COMMENT = BASE_URL + "article/comment_list";
	public static final String ESSAY_WENYI_ITEM_DETAI_ADD_COMMENT = BASE_URL + "article/comment_add";
	
	public static final String COOKBOOK_LIST_CATALOG = BASE_URL + "recipe/catalog";
	public static final String COOKBOOK_LIST = BASE_URL + "recipe/list";
	public static final String COOKBOOK_LIST_ITEM_DETAIL = BASE_URL + "recipe/detail";
	
	public static final String COOKBOOK_HOMEWORK_LIST = BASE_URL + "follow/list";
	public static final String COOKBOOK_HOMEWORK_LIST_ITEM_DETAIL = BASE_URL + "follow/detail";
	public static final String COOKBOOK_WENYI_ITEM_DETAI_ADD_COMMENT = BASE_URL + "recipe/comment_add";
	public static final String COOKBOOK_WENYI_HOMEWORK_ADD = BASE_URL + "follow/add";
	
	public static final String COOKBOOK_COLLECTION_GROUP_LIST = BASE_URL + "user/group"; // 收藏分组列表
	public static final String COOKBOOK_COLLECTION_COLLECTED = BASE_URL + "user/collect"; // 收藏
	public static final String COOKBOOK_COLLECTION_GROUP_CREATE = BASE_URL + "user/new_group"; // 创建收藏分组
	public static final String PERSONAL_COLLECTION_LIST_FOR_GROUP = BASE_URL + "user/collect_list";
	public static final String PERSONAL_COLLECTION_DELECT_COOKBOOK = BASE_URL + "user/del_collect"; // 删除个人收藏
	public static final String PERSONAL_COLLECTION_CHANGE_COOKBOOK_GROUP = BASE_URL + "user/change_group"; // 更改收藏分组

	public static final String PIZZA_DISCOVER_LIST_NEW = BASE_URL + "square/list_new";
	public static final String PIZZA_DISCOVER_LIST_HISTORY = BASE_URL + "square/list_history";
	public static final String PIZZA_TOPIC_LIST = BASE_URL + "topic/list"; // piazza question list
	public static final String PIZZA_TOPIC_LIST_ITEM_DETAIL = BASE_URL + "topic/detail"; // piazza question list
	public static final String PIZZA_TOPIC_LIST_ITEM_DETAIL_COMMENT_LIST = BASE_URL + "topic/comment_list"; // piazza question list
	public static final String PIZZA_TOPIC_LIST_ITEM_DETAIL_ADD_COMMENT = BASE_URL + "topic/comment_add";
	public static final String PIZZA_TOPIC_ADD = BASE_URL + "topic/add";
	
	public static final String GROUP_HOT_LIST = BASE_URL + "circle/list";
	public static final String GROUP_MY_LIST = BASE_URL + "circle/mylist";
	public static final String GROUP_MEM_KICK = BASE_URL + "circle/kick";
	public static final String GROUP_ITEM_DETAIL = BASE_URL + "circle/detail";
	public static final String GROUP_ITEM_SHARED_LIST = BASE_URL + "circle/share_list";
	public static final String GROUP_ITEM_MEM_LIST = BASE_URL + "circle/mem_list";
	public static final String GROUP_ITEM_MEM_AUDITING = BASE_URL + "circle/auditing";
	public static final String GROUP_ITEM_MEM_AUDIT_list = BASE_URL + "circle/auditlist";
	public static final String GROUP_CREATE_CIRCLE = BASE_URL + "circle/circle_new";
	public static final String GROUP_CIRCLE_ADD = BASE_URL + "circle/add";
	public static final String GROUP_CIRCLE_DISBAND = BASE_URL + "circle/disband";
	public static final String GROUP_CIRCLE_EDIT = BASE_URL + "circle/circie_edit";
	public static final String GROUP_CIRCLE_PUB = BASE_URL + "circle/pub";
	public static final String GROUP_CIRCLE_EXIT = BASE_URL + "circle/quit";
	public static final String GROUP_CIRCLE_SHARE_DETAIL = BASE_URL + "circle/share_detail";
	public static final String GROUP_CIRCLE_SHARE_COMMON_LIST = BASE_URL + "circle/comment_list";
	public static final String GROUP_CIRCLE_SHARE_ADD_COMMENT = BASE_URL + "circle/comment_add";
	public static final String GROUP_CIRCLE_SHARE_DEL = BASE_URL + "circle/share_del";
	
	public static final String PERSONAL_MYCARD = BASE_URL + "user/mycard";
	
	public static final String PERSONAL_MY_COMMENTS_LIST = BASE_URL + "user/mycomment";
	
	
	public static final String PERSONAL_TOPIC_MY_LIST = BASE_URL + "topic/mylist";//　我的提问
	public static final String PERSONAL_TOPIC_LIST_DEL_ITEM = BASE_URL + "topic/del";
	
	public static final String PERSONAL_MY_SHARE_LIST = BASE_URL + "circle/share_mylist";//我的分享
	
	public static final String PERSONAL_EDITION_HOMEWORK = BASE_URL + "follow/mylist";// 我的作业
	public static final String PERSONAL_EDITION_HOMEWORK_DEL = BASE_URL + "follow/del";
	
	public static final String PERSONAL_KITCHEN_INIT = BASE_URL + "kitchen/init";
	public static final String PERSONAL_KITCHTN_FOOD_DATA = BASE_URL + "kitchen/cata";
	public static final String PERSONAL_KITCHEN_FOOD_KEY = BASE_URL + "kitchen/key";
	public static final String PERSONAL_KITCHEN_FOOD_COMB = BASE_URL + "kitchen/comb";
	
	
	

	
}