package com.yunos.tv.yingshi.activity;

import java.util.List;

import yunos.tv.app.LeftNavBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.aidl.Catalog;
import com.yunos.tv.yingshi.fragment.BaseTVFragment;
import com.yunos.tv.yingshi.fragment.YingshiFragment;
import com.yunos.tv.yingshi.fragment.ZixunFragment;

/**
 * 只有一个fragment的activity
 * 注意ppvPath = intent.getStringExtra("ppvPath"); //TODO 注意必须指定catalog_name，大类目名称，从哪来的
 */
public class YingshiListActivity extends BaseTvActivity {
	protected static final String TAG = "YingshiListActivity";
	
	public static final String SCHEME_CATALOG = "catalog";
	public static final String SCHEME_TOPIC = "topic";
	
	private LeftNavBar mLeftNavBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingshi);
		ZixunFragment.clearFragmentStack(YingshiListActivity.this);
		Intent intent = getIntent();
		Catalog iCatalog = new Catalog();
		boolean isTopic = false;
		String ppvPath = "";//TODO
		
		//通过scheme调用
		Uri uri = intent.getData();
		if (uri != null) {
			List<String> path = uri.getPathSegments();
			String pathPrefix = null;
			if (path != null && path.size() > 0) {
				pathPrefix = path.get(0);
			} else {
				showToast(R.string.toast_uri_error);
				finish();
				return;
			}
			if (pathPrefix == null) {
				showToast(R.string.toast_uri_error);
				finish();
				return;
			}
			 
			if (SCHEME_CATALOG.equals(pathPrefix)) {//影视分类
				ppvPath = uri.getQueryParameter("ppvPath"); //TODO 注意必须指定catalog_name，大类目名称，从哪来的
				isTopic = false;
				iCatalog.id = uri.getQueryParameter("id");
				iCatalog.name = uri.getQueryParameter("name");
				iCatalog.picUrl = uri.getQueryParameter("picUrl");
				iCatalog.type = Integer.valueOf(uri.getQueryParameter("type")); // 0非影视  1影视
				iCatalog.hasProgram = Integer.valueOf(uri.getQueryParameter("hasProgram"));//0目录列表  1视频列表
			} else if (SCHEME_TOPIC.equals(pathPrefix)) { // 影视专题
//				ppvPath = uri.getQueryParameter("ppvPath");//TODO
				isTopic = true;
				iCatalog.id = uri.getQueryParameter("id");
				iCatalog.name = uri.getQueryParameter("name");
				iCatalog.picUrl = uri.getQueryParameter("picUrl");
				iCatalog.hasProgram = 1;
				String type = path.get(1);
				if ("list".equals(type)) {
					iCatalog.type = 0;
				} else if ("grid".equals(type)) {
					iCatalog.type = 1;
				}
//				iCatalog.type = Integer.valueOf(uri.getQueryParameter("type")); // 0非影视  1影视
			}
		} else {//通过intent指定指定activity调用
			ppvPath = intent.getStringExtra("ppvPath"); //TODO 注意必须指定catalog_name，大类目名称，从哪来的
			iCatalog.id = intent.getStringExtra("id");
			iCatalog.name = intent.getStringExtra("name");
			iCatalog.picUrl = intent.getStringExtra("picUrl");
			iCatalog.type = intent.getIntExtra("type", 1); // 0非影视  1影视
			iCatalog.hasProgram = intent.getIntExtra("hasProgram", 1);//0目录列表  1视频列表
			isTopic = intent.getBooleanExtra("isTopic", false); // 从IndexFragment的coverflow的专题项过来
		}
		
		LeftNavBar bar = getLeftNavBar();
		bar.setTitle(iCatalog.name);
		bar.setLogo(R.drawable.tui_ic_huashulogo);
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		BaseTVFragment frag = null;
		if (iCatalog.hasProgram == 1 && iCatalog.type == 1) {//请求视频列表并且是影视类
			frag = YingshiFragment.newInstance(iCatalog.id, R.id.yingshi_main, this, ppvPath, isTopic);
		} else {
			frag = ZixunFragment.newInstance(iCatalog, R.id.yingshi_main, this, isTopic, ppvPath);
		}
		frag.setIsRequestFocus(true);//请求完结果之后吸取焦点
		fragmentTransaction.replace(R.id.yingshi_main, frag);
		fragmentTransaction.commit();
	}
	
    private LeftNavBar getLeftNavBar() {
        if (mLeftNavBar == null) {
        	mLeftNavBar = new LeftNavBar(this, LeftNavBar.TYPE_TOP_NAVBAR);
            mLeftNavBar.setOnClickOptionListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
        }
        return mLeftNavBar;
    }
	
}
