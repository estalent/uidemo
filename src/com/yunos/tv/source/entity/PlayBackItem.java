package com.yunos.tv.source.entity;

import java.io.Serializable;

public class PlayBackItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String name;
	public String picurl;
	public String type;
	public ChannelInfo extra;
}
