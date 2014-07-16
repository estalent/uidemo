package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ChannelBill  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String channelKey;
	
	public ArrayList<BillMonth> months;
}
