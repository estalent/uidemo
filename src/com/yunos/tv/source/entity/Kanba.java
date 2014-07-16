package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Kanba implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ArrayList<KanbaItem> items;
	
	public ArrayList<ChannelInfo> playlist;
	
}
