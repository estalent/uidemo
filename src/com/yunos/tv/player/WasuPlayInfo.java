package com.yunos.tv.player;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.yunos.tv.source.entity.JuheSource;
import com.yunos.tv.source.entity.JuheSourceUrl;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.Source;
import com.yunos.tv.source.entity.Tag;

/**
 * 点播信息的实体类
 *
 */
public class WasuPlayInfo {
	//If a field is marked transient, (by default) it is ignored and not included in the JSON serialization or deserialization.
	private transient Program mProgram;
	public String proId;//节目ID
	public String proName;//节目名称
	public String contentId;//内容ID
	private String price = "0.00";//价格
	//-----
	public String ppvId;//节目所绑定的计费标识
	public String ppvPath;//影片路径

	public String nodeId = "0"; //节目所在栏目的ID
	public String cpId = "0"; //节目对应的CP的ID

	public String showType; // 栏目的类型 0：非影视类节目1：影视类电影栏目2：影视类电视剧栏目
	public String currentSeries; // 当前所要播放的集数,电影为1,电视剧为当前所要播放集数

	public ArrayList<SourceFile> files; // 播放的视频列表
	public String assetType; // 节目资产类型 0：自有资产，1：聚合资产
	
	//聚合视频字段
	public String videoId;//此节目对应的videoId。当assetType为0时，该值可为""。
	public String classId;//此节目对应的classId。当assetType为0时，该值可为""。M：电影 T：电视剧 E：综艺 C：动漫 D：纪录片
	public String high; // 影片清晰度。 2:超清，1: 高清, 0:标清
	public String source;//影片来源。值为汉字："优酷", "土豆", "乐视", "搜狐",  "腾讯", "奇艺"等等。当assetType为0时，该值可为空。
	
//	public ArrayList<WasuPlayInfo> recommends; // 推荐播放数据

	class SourceFile {
		public String fileId = "0"; // 電視劇每一集sourceItem中都有fileId
		public String cdn;

		public String currentPosition = "0"; // 本电视剧集，上次播放时间点
		public String start_time = "";
		public String end_time = "";

		/**
		 * 本集集数，电影默认为1
		 */
		public String series = "1";
		
		//聚合视频字段
		public String playerId = "";//节目对应的playerId。当assetType为0时，该值可为空。
	}

	/**
	 * 非聚合视频调用的构造函数。
	 * @param program
	 */
	public WasuPlayInfo(Program program, String ppvPath) {
		this(program, "", "");
		this.ppvPath = ppvPath;
	}
	
	/**
	 * 非聚合的资讯调用的构造函数。
	 * @param program
	 */
	public WasuPlayInfo(Program program) {
		this(program, "", "");
	}
	
	/**
	 * 聚合视频调用的构造函数。
	 * @param program
	 * @param source default:""
	 * @param high default:"0"
	 */
	public WasuPlayInfo(Program program, String source, String high) {
		mProgram = program;
		this.source = source;
		this.high = high;
	}
	
	public static String getRecommendsForZixun(List<Program> recommendList){
		JSONArray recommendsJSArray = new JSONArray();
		JSONObject item;
		for (Program p: recommendList) {
			try {
				item = new JSONObject(new WasuPlayInfo(p).getJsonForZixun());
				recommendsJSArray.put(item);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return recommendsJSArray.toString();
	}
	
	public String getJsonForZixun() {
		this.proId = mProgram.id;
		this.proName = mProgram.name;
		this.nodeId = mProgram.nodeId;
		this.cpId = mProgram.cpCode;
		this.showType = String.valueOf(mProgram.showType);
		this.assetType = "0";
		this.currentSeries = "1";
		
		this.files = new ArrayList<SourceFile>(1);
		SourceFile f = new SourceFile();
		f.fileId = mProgram.fileId;
		f.cdn = mProgram.cdn;
		f.currentPosition = "0";
		f.series = "1";

		/*
		 * 根据华数说明：
		 * 如果start_time不为0，肯定是拆条；如果start_time为0，就判断end_time和length长度是否相等
		 * ，小于length的，就是拆条
		 */
		if (mProgram.startTime != 0 || mProgram.endTime < mProgram.length) {// 拆条类视频
			f.start_time = String.valueOf(mProgram.startTime);
			f.end_time = String.valueOf(mProgram.endTime);
		} else {
			f.start_time = "";
			f.end_time = "";
		}
		this.files.add(f);
		
		return new Gson().toJson(this);
	}
	
	public void setPrice(String price){
		this.price = price;
	}

	/**
	 * 转化成json
	 * 
	 * @param thisTagIndex 当前用户点击的tag下标(聚合就是：乐视or搜狐， 非聚合就是:高清or标清)
	 * @param thisSourceFileName 当前用户点击的集数
	 * @param lastSourceFileName 上次播放的集数
	 * @param currentPosition 上次播放时间
	 */
	public String getJsonString(int thisTagIndex, String thisSourceFileName, String lastSourceFileName, long currentPosition) {
		this.proId = mProgram.isJuheVideo ? String.valueOf(System.currentTimeMillis()) : String.valueOf(mProgram.id);
		this.proName = mProgram.name;
		this.nodeId = mProgram.isJuheVideo ? "0" : mProgram.nodeId; 
		this.cpId = mProgram.isJuheVideo ? "0" : mProgram.cpCode;

		// 华数给的资源中showType:3为电视剧,但是播放器里面要求电视剧的showType为2,靠靠靠 TODO
		this.showType = String.valueOf(mProgram.showType == 3 ? 2 : mProgram.showType);
		
		this.assetType = mProgram.isJuheVideo ? "1" : "0";
		if (mProgram.isJuheVideo) { //聚合资产类
			this.videoId = mProgram.id;
			this.classId = mProgram.classId;
			if (mProgram.player != null && mProgram.player.get(thisTagIndex) != null) {
				JuheSource tag = mProgram.player.get(thisTagIndex);
				int sourceSize = tag.url.size();
				this.currentSeries = sourceSize > 1 ? thisSourceFileName : "1";
				this.files = new ArrayList<SourceFile>(sourceSize);
				SourceFile iFile = null;
				for (int i = 0; i < sourceSize; i++) {
					iFile = new SourceFile();
					JuheSourceUrl s = tag.url.get(i);

					iFile.fileId = "";
					iFile.playerId = s.playerId;
					iFile.cdn = "";

					// 影视类为"" 
					iFile.start_time = "";
					iFile.end_time = "";

					/*
					 * sqlite中记录fileName，而不是下标值(因为集数可能会变，或者只有22,23集的情况或者两个17集的情况
					 */
					iFile.currentPosition = "0";
					if (sourceSize > 1) {
						if (currentPosition > 0 && s.deTitle.equals(thisSourceFileName) && thisSourceFileName.equals(lastSourceFileName)) {
							iFile.currentPosition = String.valueOf(currentPosition);
						}
					} else {
						if (currentPosition > 0) {
							iFile.currentPosition = String.valueOf(currentPosition);
						}
					}
					iFile.series = sourceSize > 1 ? s.deTitle : "1";
					this.files.add(iFile);
				}
			}
		} else {//华数资产类
			this.currentSeries = mProgram.showType == 3 ? thisSourceFileName : "1";
			
			if (mProgram.tags != null && mProgram.tags.get(thisTagIndex) != null) {
				Tag tag = mProgram.tags.get(thisTagIndex);
				if (tag.source != null) { // 影视类
					int sourceSize = tag.source.size();
					this.files = new ArrayList<SourceFile>(sourceSize);
					SourceFile iFile = null;
					for (int i = 0; i < sourceSize; i++) {
						iFile = new SourceFile();
						Source s = tag.source.get(i);

						iFile.fileId = s.fileId;
						iFile.cdn = s.cdn;

						// 影视类为""
						iFile.start_time = "";
						iFile.end_time = "";

						/*
						 * sqlite中记录fileName，而不是下标值(因为集数可能会变，或者只有22,23集的情况或者两个17集的情况
						 */
						iFile.currentPosition = "0";
						if (mProgram.showType == 3) { // 电视剧
							//--------linghu-----------
							if (s.fileName.equals(thisSourceFileName)){
								this.ppvId = s.ppvId;	
								this.contentId = s.fileId;
							}
							//---------linghu------------
							
							// 如果当前播放的集数和上次播放的集数相同,则设置上次播放时长。这时fileName中存的都是数字
							if (s.fileName.equals(thisSourceFileName) && thisSourceFileName.equals(lastSourceFileName)) {
								if (s.length == 0 || s.length > 0 && currentPosition < s.length) {
									iFile.currentPosition = String.valueOf(currentPosition);
								}
							}
						} else if (mProgram.showType == 1) {// 电影
							/*
							 * 对于电影来说，fileName不是数字而是文字。
							 * http://video.tv.yunos.com/video
							 * /program/info/?id=37598 这个猫的电影，不同的源fileName不同。
							 * length也很奇怪　
							 */
							//--------linghu-----------
							if (s.fileName.equals(thisSourceFileName)){
								this.ppvId = s.ppvId;	
								this.contentId = s.fileId;
							}
							//---------linghu------------
							if (currentPosition > 0) {
								iFile.currentPosition = String.valueOf(currentPosition);
							}
						}
						iFile.series = mProgram.showType == 3 ? s.fileName : "1";
						this.files.add(iFile);
					}
				}
			} else {
				// 如果是列表页面直接播放,则需要从Program中拿cdn
				this.files = new ArrayList<SourceFile>(1);
				SourceFile f = new SourceFile();

				f.fileId = mProgram.fileId;
				f.cdn = mProgram.cdn;
				f.currentPosition = String.valueOf(currentPosition);
				f.series = "1";

				/*
				 * 根据华数说明：
				 * 如果start_time不为0，肯定是拆条；如果start_time为0，就判断end_time和length长度是否相等
				 * ，小于length的，就是拆条
				 */
				if (mProgram.startTime != 0 || mProgram.endTime < mProgram.length) {// 拆条类视频
					f.start_time = String.valueOf(mProgram.startTime);
					f.end_time = String.valueOf(mProgram.endTime);
				} else {
					f.start_time = "";
					f.end_time = "";
				}
				this.files.add(f);
			}
		}
		return new Gson().toJson(this);
	}
}
