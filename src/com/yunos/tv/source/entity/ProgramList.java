package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramList implements Serializable  {
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页码
	 */
	public int curPage;
	
	/**
	 * 总页数
	 */
	public int totalPage;
	
	/**
	 * 总记录条数
	 */
	public int total;
	
	/**
	 * 是否有下一页
	 */
	public boolean hasNext;
	/**
	 * 节目列表
	 */
	public ArrayList<Program> programList;
}
