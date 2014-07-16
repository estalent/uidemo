package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.yunos.tv.yingshi.aidl.Catalog;

public class Dianbo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	public String name;
	public String picurl;
	public String type;
	public ArrayList<Catalog> extra;
}
