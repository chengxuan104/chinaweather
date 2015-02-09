package activity;



import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.chinaweather.app.R;

import android.app.Activity;

import android.content.Context;
//import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.FragmentActivity;  
import android.support.v4.widget.DrawerLayout;  
import android.support.v4.widget.DrawerLayout.DrawerListener;  
import com.nineoldandroids.view.ViewHelper; 
import android.support.v4.widget.SwipeRefreshLayout; 

public class WeatherActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener{
	
	private LinearLayout weatherInfoLayout;
	
	private static final int REFRESH_COMPLETE = 0X110;
 
// 
//    private ListView mListView;  
//    private ArrayAdapter<String> mAdapter;  
//    private List<String> mDatas = new ArrayList<String>(Arrays.asList("Java", "Javascript", "C++", "Ruby", "Json",  
//            "HTML"));

	private LocationClient mLocationClient;    //以下这段是百度地图的
	private TextView LocationResult;
	private Button startLocation;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="bd09ll";
	
	/**
	 * 下拉刷新
	 */
	 private SwipeRefreshLayout mSwipeLayout;
	 
	/**
	 * 侧滑抽屉
	 */
	private DrawerLayout mDrawerLayout;
	

	/**
	 *  关闭或者打开自动更新
	 */
	private Boolean closeAutoFromChooseArea;
	/**
	 * 返回按钮
	 */
	private Button return_button;
	
	/**
	 * 更新天气按钮
	 */
	private Button refresh_button;
	
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	
	/**
	 * 用于显示天气描述
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	
	
	/**
	 * 县城
	 */
	 String countyCode;
	 
	 //调用selectPicpopupWindow
	 SelectPicPopupWindow menuWindow;  

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//初始各控件
		cityNameText = (TextView) findViewById(R.id.city_name);
		//return_button =  (Button) findViewById(R.id.return_button); //cx
		//refresh_button =  (Button) findViewById(R.id.refresh_button); //cx
		
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		//下拉刷新
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);  
        mSwipeLayout.setOnRefreshListener(this);  
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
        							android.R.color.holo_orange_light, android.R.color.holo_red_light);         
        
		//侧边栏  
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);  //左侧菜单dian击能出现
		initEvents(); //手势滑动
		
		//打开底部PopupWindow分享栏
		Button popupWindow = (Button) this.findViewById(R.id.PopupWindow);
        //把文字控件添加监听，点击弹出自定义窗口  
		popupWindow.setOnClickListener(new OnClickListener(){             
            public void onClick(View v) {  
                //实例化SelectPicPopupWindow  
                menuWindow = new SelectPicPopupWindow(WeatherActivity.this, itemsOnClick);  
                //显示窗口  
                menuWindow.showAtLocation(WeatherActivity.this.findViewById(R.id.drawer_layout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 100, 0); //设置PopupWindow在layout中显示的位置  
            }  
        });
		
		countyCode = getIntent().getStringExtra("county_code"); //在choose里面有一个putExtra传county_code
		
//		//点击button显示底部弹出的表格栏
//		GridView gridView = (GridView) findViewById(R.id.gridView_layout);
//		ArrayList<HashMap<String, Object>> listImageItem = new ArrayList<HashMap<String,Object>>();
//		HashMap<String, Object> map = new HashMap<String, Object>();   //new 一个key-value的hashmap对象
//		map.put("popup_Image", R.drawable.weixin);
//		map.put("popup_Text", "微信");
//		listImageItem.add(map);  //添加图片和文字完成
//		
//		// 参数一是当前上下文Context对象  
//        // 参数二是图片数据列表，要显示数据都在其中  
//        // 参数三是界面的XML文件，注意，不是整体界面，而是要显示在GridView中的单个Item的界面XML  
//        // 参数四是动态数组中与map中图片对应的项，也就是map中存储进去的相对应于图片value的key  
//        // 参数五是界面XML中的图片ID  
//		SimpleAdapter saImageItems = new SimpleAdapter(this, 
//											listImageItem,   
//											R.layout.popupwindow_bottom_layout, 
//											new String[] {"popup_Image", "popup_Text"},
//											new int[] {R.id.popup_Image, R.id.popup_Text}
//											);
//		gridView.setAdapter(saImageItems);
//		//gridView.setOnItemClickListener();
		
		
		
//		mListView = (ListView) findViewById(R.id.id_listview);  
//        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);  
//  
//        mSwipeLayout.setOnRefreshListener(this);  
//        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
//                android.R.color.holo_orange_light, android.R.color.holo_red_light);  
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDatas);  
//        mListView.setAdapter(mAdapter);  
	        
		
		
//		//返回按钮
//		return_button.setOnClickListener(new OnClickListener() {	
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//把city_selected修改为false，这样跳回ChooseAreaActivity就不会直接显示县城天气了。
//				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//				editor.putBoolean("city_selected", false);
//				editor.commit();
//				Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
//				intent.putExtra("from_weather_activity", true);
//				startActivity(intent);
//				finish();
//				return;
//			}
//		});

			
//		//刷新天气按钮
//		refresh_button.setOnClickListener(new OnClickListener() {	
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				/**
//				 * 当 str 是空（null）或空串（""）时返回为真
//				 */
//				if(!TextUtils.isEmpty(countyCode)){
//					//有县级代号时就去查询天气
//					publishText.setText("同步中...");
//					weatherInfoLayout.setVisibility(View.INVISIBLE);
//					cityNameText.setVisibility(View.INVISIBLE);
//					queryWeatherCode(countyCode);
//				}
//			}
//		});
		
		/**
		 * 当 str 是空（null）或空串（""）时返回为真
		 */
		if(!TextUtils.isEmpty(countyCode)){
			//有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			//没有县级代号时就直接显示本地天气
			showWeather();
		}
		
		//百度地图定位sdk
		mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
		
		LocationResult = (TextView)findViewById(R.id.textView1);
		 ((LocationApplication)getApplication()).mLocationResult = LocationResult;

		startLocation = (Button)findViewById(R.id.addfence);
		startLocation.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InitLocation();   //设置定位模式，定位频率	
				if(startLocation.getText().equals(getString(R.string.startlocation))){
					mLocationClient.start();
					startLocation.setText(getString(R.string.stoplocation));
				}else{
					mLocationClient.stop();
					startLocation.setText(getString(R.string.startlocation));
				}
			}
		});
	}
	
	/**
	 * 手势滑动
	 */
	private void initEvents() {
		// TODO Auto-generated method stub
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			
			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				// TODO Auto-generated method stub
				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = drawerView;
				//以下动画是仿qq的
				float scale = 1- slideOffset;   //动画比例计算
				float rightScale = 0.8f + scale * 0.2f; //内容区域的缩放比例
				if (drawerView.getTag().equals("LEFT")){
					 float leftScale = 1 - 0.1f * scale;   //菜单的缩放比例计算
					 
					 //以下是com.nineoldandroids.view.ViewHelper; 提供的api接口
					 ViewHelper.setScaleX(mMenu, leftScale);  //设置菜单栏的显示
					 ViewHelper.setScaleY(mMenu, leftScale);
					 ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
			         ViewHelper.setTranslationX(mContent, 
	                            mMenu.getMeasuredWidth() * (1 - scale));  
	                 ViewHelper.setPivotX(mContent, 0);  
	                 ViewHelper.setPivotY(mContent,  
	                            mContent.getMeasuredHeight() / 2);  
	                 mContent.invalidate();  
				}
			}
			
			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	

	/**
	 * 按键打开左侧边栏，button监听放在xml里面
	 * @param view
	 */
	public void OpenLeftMenu(View view){
		 mDrawerLayout.openDrawer(Gravity.LEFT);
	}
	
	/**
	 * 查询县级代号所对应的天气代号
	 */
	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	

	/**
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";//地址错了会报org.json.JSONException: Value <!DOCTYPE of type java.lang.String cannot be converted to JSONObject
		queryFromServer(address, "weatherCode");
	}
	

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {		
			//final？why is need
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				
				//查询县级代号所对应的天气代号
				if("countyCode".equals(type)) {
					if(!TextUtils.isEmpty(response)) {
						//从服务器返回的数据中解析出去天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response); //
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		} );
	}



	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", "cx1")); //没有得到返回值  //检索和city_name相同的的字符串并返回值，如果没有，就返回 默认的字符串。这里是""
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
//		//根据autoUpdate进行判断，autoUpdate开启or关闭后台服务
//		if(prefs.getBoolean("autoUpdate", false))
//		{
//			Toast.makeText(this,
//					"关闭自动更新", Toast.LENGTH_LONG).show();
//			Intent intent = new Intent(this, AutoUpdateService.class);
//			stopService(intent);
//		}
//		else{
//			Toast.makeText(this,
//					"开启自动更新", Toast.LENGTH_LONG).show();
//			Intent intent = new Intent(this, AutoUpdateService.class);
//			startService(intent);
//		}
	}
	
	/**
	 * 通过handler去处理
	 */
    private Handler mHandler = new Handler()  
    {  
        public void handleMessage(android.os.Message msg)  
        {  
            switch (msg.what)  
            {  
            case REFRESH_COMPLETE:  
            	//刷新动作,添加要刷新的东西
				publishText.setText("同步中...");
//				weatherInfoLayout.setVisibility(View.INVISIBLE);
//				cityNameText.setVisibility(View.INVISIBLE);
//				
//				//这边countyCode不能得到，因为chooseareaActivity没有戳过来
//				/**
//				 * 当 str 是空（null）或空串（""）时返回为真
//				 */
//				if(!TextUtils.isEmpty(countyCode)){
//					//有县级代号时就去查询天气
//					publishText.setText("同步中...");
//					weatherInfoLayout.setVisibility(View.INVISIBLE);
//					cityNameText.setVisibility(View.INVISIBLE);
//					queryWeatherCode(countyCode);
//				} else {
//					//没有县级代号时就直接显示本地天气
//					showWeather();
//				}
//				          
//                mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));  
//                mAdapter.notifyDataSetChanged();  
                mSwipeLayout.setRefreshing(false);     //隐藏或者显示进度条   
                break;
            }  
        };  
    };
    
    //刷新
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub 
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);   //what=272  milles = 2000
	}
	
	 //为底部弹出窗口实现监听类  
    private OnClickListener  itemsOnClick = new OnClickListener(){  
    	
        public void onClick(View v) {  
            menuWindow.dismiss();      //等待switch事件执行完以后popupwindow消失
            switch (v.getId()) {  
            case R.id.btn_duanxin:
                break;  
            case R.id.btn_qq:                 
                break;  
            case R.id.btn_renren:
                break;  
            case R.id.btn_qqzone:                 
                break; 
            case R.id.btn_pengyouquan:
                break;  
            case R.id.btn_tengxunweibo:                 
                break; 
            case R.id.btn_weibo:
                break;  
            case R.id.btn_weixin:                 
                break; 
            default:  
                break;  
            }  
                  
        }  
          
    };  
    
    /**
     * 左侧边栏add按钮侦听事件
     * @param view
     */
	public void add(View view){
		//把city_selected修改为false，这样跳回ChooseAreaActivity就不会直接显示县城天气了。
//		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//		editor.putBoolean("city_selected", false);
//		editor.commit();
		Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
		return;
	}
	
	//百度地图定位
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}
	
	//百度地图定位sdk初始化，设定定位模式定位频率
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值bd09ll
		int span=1000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);   //checkGeoLocation.isChecked()  //设置为反地理编码
		mLocationClient.setLocOption(option);
	}
	
}
