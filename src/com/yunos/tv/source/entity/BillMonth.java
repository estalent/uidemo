package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class BillMonth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int month;
	
	public ArrayList<BillDay> days;
	
}
