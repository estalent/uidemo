package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class BillDay implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int day;
	
	public ArrayList<BillItem> bills;
}
