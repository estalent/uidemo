package com.yunos.tv.source.entity;

import java.io.Serializable;

/**
 * 服务器接口返回的数据格式
 * 
 * @param <T>
 *            需要返回的数据类型
 */
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 请求结果编码,200表示请求正常
	 */
	public int code;
	/**
	 * 请求结果消息
	 */
	public String message;
	/**
	 * 接口返回的具体数据内容
	 */
	public T data;

	/**
	 * 是否请求成功
	 */
	public boolean success;
}
