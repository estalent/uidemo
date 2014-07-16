package com.yunos.tv.source.entity;

import java.io.Serializable;

public class ChannelInfo  implements Serializable/*, Parcelable */{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int channelKey; // 频道KEY
	
	public String channelName; // 频道名称
	
	public String channelLogo; // 频道台标
	
	public int playType;
	
	public ChannelUrl playUrl; // 直播调用地址，HTTP直播流调用地址
	
	public String playBackUrl; //回放播放调用地址
	
	public String playBackBill; //回放播放调用地址
	
	public String picUrl;
	
	public String linkUrl;
	
	public class ChannelUrl implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public String httpUrl; // HTTP直播流调用地址
		
		public String channelID; // 原力平台频道ID,用于直播和轮播
		
		public String fccs; // 原力服务器地址
	}
	
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//	
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeInt(channelKey);
//		dest.writeString(channelName); 
//		dest.writeString(channelLogo);
//		dest.writeString(playUrl);
//		dest.writeString(playBackUrl);
//		dest.writeString(playBackBill);
//		dest.writeString(picUrl);
//		dest.writeString(linkUrl);
//	}
//	
//	public static final Parcelable.Creator<ChannelInfo> CREATOR = new Parcelable.Creator<ChannelInfo>() {
//
//		@Override
//		public ChannelInfo createFromParcel(Parcel source) {
//			//写入parcel和反序列化parcel时顺序一定要相同，不然数据会出错
//			ChannelInfo d = new ChannelInfo();
//			d.channelKey = source.readInt();
//			d.channelName = source.readString();
//			d.channelLogo = source.readString();
//			d.playUrl = source.readString();
//			d.playBackUrl = source.readString();
//			d.playBackBill = source.readString();
//			d.picUrl = source.readString();
//			d.linkUrl = source.readString();
//			return d;
//		}
//
//		@Override
//		public ChannelInfo[] newArray(int size) {
//			return new ChannelInfo[size];
//		}
//	};
}
