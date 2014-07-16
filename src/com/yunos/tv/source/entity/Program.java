package com.yunos.tv.source.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * 节目详情
 */
public class Program implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_CLASSID = "WASU_FILM";
	
	public String id;
	
	public String name;
	
	public String type; // hot推荐:  program / topic
	
	public int nodeType; // 
	
	public String engname;
	
	public String picUrl;
	
	public String description;
	
    public String director;
    
    public String region;
    
    public String actor;
    
    public String language;
    
    public String pubtime; //发行时间,只有年月日,时分秒均为0
    
    public int year; // 发行年份
    
    public String viewPoint;
    
    public int clickNum;

    /**
     * 播放地址.只有非影视类的节目,并且是搜索结果中才有值(不到详情页,再列表页点击直接播放),否则为null
     * 如果为获取节目详情,则该字段为null,需要从tag里面去获取播放地址
     */
    public String cdn;
    
    /**
     * 以下两种情况：
     * 1. 非影视类的节目，并且是搜索结果中才有值,否则为空
     * 2. 一级分类的高清栏目(记录,综艺)，视频列表的每项program中
     */
    public String fileId;
    
    public int showType; //节目类型  0-非影视,高清-综艺，高清-记录(要以列表形式展示) 1-电影 3-电视剧
    
    public String nodeName; //栏目名称(搜索返回的节目列表才有该字段)
    
    public String nodeId; //栏目id
    
    public String nodePic; //栏目图片(搜索返回的节目列表才有该字段)
    
    public ArrayList<Tag> tags; //资源标签列表(获取节目详情的时候才有该字段)
    
    public ArrayList<Program> recommend; //相关节目列表(猜你喜欢,获取节目详情的时候才有该这段)
    
    public String cpCode; //CP的标识
    
    //码流类型  0：普通 1：高清 2：蓝光 根据该属性显示小图标
    public int rateType = 0;
    
    //===以下非网络获取字段
    public boolean isFavor = false;
    public String lastPlayTagName = null;
    public String lastPlayFileName = ""; // 上次播放第几集, 对应Source中的fineName, 或deTitle // TODO 我把"1"改为""，不知道会不会有影响
    //http://10.249.201.60:8080/v2/juhe/video/info?videoId=73518&classId=M返回的deTitle为""
    public long lastPlayTime = 0; //上次播放到这集的哪一块
    
    public long startTime;
	public long endTime;
	public long length;
	
	//聚合
	public boolean isJuheVideo = false; // true为聚合视频，isJuheVideo字段不存在则为华数视频
	public String classId = "WASU_FILM"; //聚合视频类型，在调用聚合detail和play接口为必填参数 : M: 电影  T:电视剧  E: 综艺  C: 动漫  D: 纪录片，华数资产时，就默认为WASU_FILM
	//在lastplay数据库中，classId是联合主键之一
	public String videoId; //聚合视频ID，一般地，放在上面id中。这里特殊的，在详情页返回数据的推荐recommends字段中，出现viedoId，json解析需要这个字段。
	//对于videoId使用时，要特别小心。
    
	public String score;
	public String juheType; // 古装/武侠/情感/爱情/青春
	public String tag; //电视剧
	public String flagOfHtml5; // 1
	public ArrayList<JuheSource> player;
	public String ppvPath;
//	public String juheLink;//通过playerId和classId来请求接口获得
}
