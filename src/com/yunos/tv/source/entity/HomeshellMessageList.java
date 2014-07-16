package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeshellMessageList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int curPage;
	public int totalPage;
	public int total;
	public ArrayList<HomeshellMessage> data;
}
