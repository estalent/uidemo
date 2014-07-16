package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 视频资源标签列表
 * 同一个视频可能会有多个资源标签,对应不同的清晰度
 */
public class Tag implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 标签id
	 */
	public int id;
	
	/**
	 * 视频码率
	 */
	public int rate;
	
	/**
	 * 标签名称(如:流畅,标清,高清等)
	 */
	public String tag;
	
	/**
	 * 当前标签对应的视频源列表.多个则表示当前视频有多集
	 */
	public ArrayList<Source> source;
}
