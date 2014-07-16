package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 节目详细信息
	 */
	public Program program;
	
	/**
	 * 资源标签列表
	 */
	public ArrayList<Tag> tag;
	
	/**
	 * 相关视频
	 */
	public ArrayList<Program> recommend;
}
