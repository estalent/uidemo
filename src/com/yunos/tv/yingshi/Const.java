package com.yunos.tv.yingshi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Const {
	/**
	 * 对ImageArg.arg的约定
	 * 1. ImageArg.arg3  资讯页带倒影的
	 * 2. ImageArg.arg1  CoverflowAdapter中
	 * 3. ImageArg.thumbnail  影视详情页-猜你喜欢
	 * 4. ImageArg.middle  影视列表页
	 * 5. ImageArg.arg2   资讯首页橫着的图   
	 */
	
	public static final int IMAGE_TRANS_TIME = 300; // 渐变时间
	
	/**
	 * 图片宽高比 1:1.5
	 * 
		需要的尺寸 396*594 279*327 410*543 144*186
		
		每个页面详细尺寸
		1.首页:396*594
		2.列表页:279*327
		3.详情页大图:410*543
		4.详情页猜你喜欢:144*186
		5.列表页左侧大图:396*594
		
		使用范例：
		a.jpg 
		a.jpg_6x6.jpg
	 */
//	public static final String IMAGE_SIZE_HOME = "_396x594";  // homeshell首页
	public static final String IMAGE_SIZE_YINGSHI_GRID = "_234x327"; //影视grid
	public static final String IMAGE_SIZE_DETAIL_BIG = "_410x543"; // 详情页大图
	public static final String IMAGE_SIZE_DETAIL_SMALL = "_144x186"; //详情页小图
//	public static final String IMAGE_SIZE_ZIXUN_LIST = "_396x594"; // 资讯列表页左方大图
	
	//action
	public static final String ACTION_WASU_PLAYER_NOTIFY = "com.wasuali.action.playnotification";
	public static final String ACTION_UPDATE_TITLE = "com.yunos.tv.action.update_title";
	public static final String ACTION_PLAY = "com.yunos.tv.action.play";//广播通知homeshell
	
	//最近播放取几条
	public static final int LAST_PLAY_FETCH_NUM = 10;
	
	//最多收藏两百条
	public static final int MAX_SIZE_FAVOR = 18 * 15; // 18条一屏*15屏
	
	public static final int REQUEST_CODE_NET_SETTING = 101;
	
	public static final int ATTR_TYPE_HOT = 3;	//热门影视属性
	
	/*
	 * startActivityForResult  request codes
	 */
	public static final int REQ_CODE_FROM_FAVORACTIVITY = 101;
	public static final int REQ_CODE_FROM_YINGSHIACTIVITY = 102;
	
	public static final Map<String, Integer> SOURCE_MAP = new HashMap<String, Integer>();
	public static final String[] SOURCE_KEYS = {
		//优酷，土豆，乐视，搜狐，腾讯，奇艺，PPS，PPTV，CNTV，天翼，电影网，56，新浪，激动，凤凰，酷六，风行
		"优酷", "土豆", "乐视", "搜狐",  "腾讯", "奇艺", "PPS", "PPTV", "CNTV", "天翼", "电影网", "我乐",
		"新浪", "激动", "凤凰", "酷六", "风行", "56", "电影"
	};
	public static final int[] SOURCE_VALUES = { 
		R.drawable.icon_youku,
		R.drawable.icon_tudou,
		R.drawable.icon_letv,
		R.drawable.icon_souhu,
		R.drawable.icon_tencent,
		R.drawable.icon_aiqiyi,
		R.drawable.icon_pps,
		R.drawable.icon_pptv,
		R.drawable.icon_cntv,
		R.drawable.icon_tianyi,
		R.drawable.icon_m1905, //电影网
		R.drawable.icon_56,
		R.drawable.icon_sina,
		R.drawable.icon_jidong,
		R.drawable.icon_fenghuang,
		R.drawable.icon_ku6,
		R.drawable.icon_funshion,//风行
		R.drawable.icon_56,
		R.drawable.icon_m1905,
	};
	
	static {
		for (int i = 0, len = SOURCE_KEYS.length; i < len; ++i) {
			SOURCE_MAP.put(SOURCE_KEYS[i], SOURCE_VALUES[i]);
		}
	}
	
	public static final String[] INDEX_IDS = new String[] {
		"12029", "12032", "12030", // 老的
		"35176", "35181", "35177"
	};
}
