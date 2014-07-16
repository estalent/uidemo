package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class JuheSource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int high; //清晰度 取值  2:超清，1: 高清, 0:标清
	
	public String key; //视频源提供商名称: 乐视
	
	public int flag; // html5标记 ，暂无用
	
	public String status; //  节目状态，中文: 共56集
	
	public String sourcePic; // 视频源logo
	
	public ArrayList<JuheSourceUrl> url; // 播放列表
}
