package com.yunos.tv.source.entity;

import java.io.Serializable;

/**
 * 标签(不同清晰度)对应的资源列表详细信息
 * 每种清晰度可能会有多个资源列表.对应到电视剧则为分集列表
 */
public class Source implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 资源名,如果当前视频为电视剧,则是电视的分集名称.
	 */
	public String fileName;
	
	/**
	 * 当前资源的播放地址 
	 */
	public String cdn;
	
	/**
	 * 时常
	 */
	public Long length;
	
	/**
	 * 资源文件大小,单位b
	 */
	public Long size;
	
	/**
	 * 标签id
	 */
	public int tagId;
	
	/**
	 * 华数的fileId
	 */
	public String fileId;
	
	/**
	 * 分段开始播放位置(拆段视频)
	 */
	public long startTime;
	
	/**
	 * 分段开始播放位置(拆段视频)
	 */
	public long endTime;
	
	/**
	 * 节目所绑定的计费标识
	 */
	public String ppvId;
	
	/**
	 * 影片路径
	 */
	public String ppvPath;
	
	public float price = -1f;
}
