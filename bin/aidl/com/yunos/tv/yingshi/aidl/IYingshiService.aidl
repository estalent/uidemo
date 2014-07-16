package com.yunos.tv.yingshi.aidl;

interface IYingshiService {
	List getLastPlayList();
	Map getCategoryList(int type);
	Map getCacheCatatoryList();
	Map getDianboList();
	Map getMessageList();
	Map getPlayBackList();
}