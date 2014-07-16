package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramLists implements Serializable {
	private static final long serialVersionUID = 1L;
	public String nodeId;
	public String nodeName;
	public ArrayList<Program> programList;
}
