package com.yunos.tv.yingshi.aidl;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 目录
 */
public class Catalog implements Parcelable, Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 栏目ID
	 */
	public String id;
	/**
	 * 栏目名称
	 */
	public String name;
	
	/**
	 * 栏目属性 1-正常栏目 3-看吧
	 */
	public int attr;
	
	/**
	 * 栏目类型 0-非影视类栏目 1-影视类栏目
	 */
	public int type;
	/**
	 * 栏目图片
	 */
	public String picUrl;
	/**
	 * 当前类目下是否有节目列表  1-是（请求节目列表） 0-否（请求子栏目）
	 */	
	public int hasProgram;
	/**
	 * 该类目的子类目,只有根目录才会把子类目加载过来.否则该值为null
	 */
	public ArrayList<Catalog> child;
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id); 
		dest.writeString(name);
		dest.writeInt(type);
		//dest.writeInt(attr);
		dest.writeString(picUrl);
		dest.writeInt(hasProgram);
		dest.writeList(child);
//		dest.writeByte((byte) (bool ? 1 : 0));
	}
	
	public static final Parcelable.Creator<Catalog> CREATOR = new Parcelable.Creator<Catalog>() {

		@Override
		public Catalog createFromParcel(Parcel source) {
			//写入parcel和反序列化parcel时顺序一定要相同，不然数据会出错
			Catalog d = new Catalog();
			d.id = source.readString();
			d.name = source.readString();
			d.type = source.readInt();
			//d.attr = source.readInt();
			d.picUrl = source.readString();
			d.hasProgram = source.readInt();
//			d.child = source.readArrayList(ArrayList.class.getClassLoader());
			d.child = source.readArrayList(Catalog.class.getClassLoader());
			return d;
		}

		@Override
		public Catalog[] newArray(int size) {
			return new Catalog[size];
		}
	};
}
