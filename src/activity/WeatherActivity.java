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

	private LocationClient mLocationClient;    //��������ǰٶȵ�ͼ��
	private TextView LocationResult;
	private Button startLocation;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="bd09ll";
	
	/**
	 * ����ˢ��
	 */
	 private SwipeRefreshLayout mSwipeLayout;
	 
	/**
	 * �໬����
	 */
	private DrawerLayout mDrawerLayout;
	

	/**
	 *  �رջ��ߴ��Զ�����
	 */
	private Boolean closeAutoFromChooseArea;
	/**
	 * ���ذ�ť
	 */
	private Button return_button;
	
	/**
	 * ����������ť
	 */
	private Button refresh_button;
	
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	
	/**
	 * ������ʾ��������
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	
	/**
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;
	
	
	/**
	 * �س�
	 */
	 String countyCode;
	 
	 //����selectPicpopupWindow
	 SelectPicPopupWindow menuWindow;  

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ���ؼ�
		cityNameText = (TextView) findViewById(R.id.city_name);
		//return_button =  (Button) findViewById(R.id.return_button); //cx
		//refresh_button =  (Button) findViewById(R.id.refresh_button); //cx
		
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		//����ˢ��
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);  
        mSwipeLayout.setOnRefreshListener(this);  
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  
        							android.R.color.holo_orange_light, android.R.color.holo_red_light);         
        
		//�����  
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);  //���˵�dian���ܳ���
		initEvents(); //���ƻ���
		
		//�򿪵ײ�PopupWindow������
		Button popupWindow = (Button) this.findViewById(R.id.PopupWindow);
        //�����ֿؼ���Ӽ�������������Զ��崰��  
		popupWindow.setOnClickListener(new OnClickListener(){             
            public void onClick(View v) {  
                //ʵ����SelectPicPopupWindow  
                menuWindow = new SelectPicPopupWindow(WeatherActivity.this, itemsOnClick);  
                //��ʾ����  
                menuWindow.showAtLocation(WeatherActivity.this.findViewById(R.id.drawer_layout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 100, 0); //����PopupWindow��layout����ʾ��λ��  
            }  
        });
		
		countyCode = getIntent().getStringExtra("county_code"); //��choose������һ��putExtra��county_code
		
//		//���button��ʾ�ײ������ı����
//		GridView gridView = (GridView) findViewById(R.id.gridView_layout);
//		ArrayList<HashMap<String, Object>> listImageItem = new ArrayList<HashMap<String,Object>>();
//		HashMap<String, Object> map = new HashMap<String, Object>();   //new һ��key-value��hashmap����
//		map.put("popup_Image", R.drawable.weixin);
//		map.put("popup_Text", "΢��");
//		listImageItem.add(map);  //���ͼƬ���������
//		
//		// ����һ�ǵ�ǰ������Context����  
//        // ��������ͼƬ�����б�Ҫ��ʾ���ݶ�������  
//        // �������ǽ����XML�ļ���ע�⣬����������棬����Ҫ��ʾ��GridView�еĵ���Item�Ľ���XML  
//        // �������Ƕ�̬��������map��ͼƬ��Ӧ���Ҳ����map�д洢��ȥ�����Ӧ��ͼƬvalue��key  
//        // �������ǽ���XML�е�ͼƬID  
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
	        
		
		
//		//���ذ�ť
//		return_button.setOnClickListener(new OnClickListener() {	
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//��city_selected�޸�Ϊfalse����������ChooseAreaActivity�Ͳ���ֱ����ʾ�س������ˡ�
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

			
//		//ˢ��������ť
//		refresh_button.setOnClickListener(new OnClickListener() {	
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				/**
//				 * �� str �ǿգ�null����մ���""��ʱ����Ϊ��
//				 */
//				if(!TextUtils.isEmpty(countyCode)){
//					//���ؼ�����ʱ��ȥ��ѯ����
//					publishText.setText("ͬ����...");
//					weatherInfoLayout.setVisibility(View.INVISIBLE);
//					cityNameText.setVisibility(View.INVISIBLE);
//					queryWeatherCode(countyCode);
//				}
//			}
//		});
		
		/**
		 * �� str �ǿգ�null����մ���""��ʱ����Ϊ��
		 */
		if(!TextUtils.isEmpty(countyCode)){
			//���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			//û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		
		//�ٶȵ�ͼ��λsdk
		mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
		
		LocationResult = (TextView)findViewById(R.id.textView1);
		 ((LocationApplication)getApplication()).mLocationResult = LocationResult;

		startLocation = (Button)findViewById(R.id.addfence);
		startLocation.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InitLocation();   //���ö�λģʽ����λƵ��	
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
	 * ���ƻ���
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
				//���¶����Ƿ�qq��
				float scale = 1- slideOffset;   //������������
				float rightScale = 0.8f + scale * 0.2f; //������������ű���
				if (drawerView.getTag().equals("LEFT")){
					 float leftScale = 1 - 0.1f * scale;   //�˵������ű�������
					 
					 //������com.nineoldandroids.view.ViewHelper; �ṩ��api�ӿ�
					 ViewHelper.setScaleX(mMenu, leftScale);  //���ò˵�������ʾ
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
	 * ��������������button��������xml����
	 * @param view
	 */
	public void OpenLeftMenu(View view){
		 mDrawerLayout.openDrawer(Gravity.LEFT);
	}
	
	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 */
	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	

	/**
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";//��ַ���˻ᱨorg.json.JSONException: Value <!DOCTYPE of type java.lang.String cannot be converted to JSONObject
		queryFromServer(address, "weatherCode");
	}
	

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {		
			//final��why is need
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				
				//��ѯ�ؼ���������Ӧ����������
				if("countyCode".equals(type)) {
					if(!TextUtils.isEmpty(response)) {
						//�ӷ��������ص������н�����ȥ��������
						String[] array = response.split("\\|");
						if (array != null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
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
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		} );
	}



	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", "cx1")); //û�еõ�����ֵ  //������city_name��ͬ�ĵ��ַ���������ֵ�����û�У��ͷ��� Ĭ�ϵ��ַ�����������""
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����" + prefs.getString("publish_time", "") + "����");
		currentDateText.setText(prefs.getString("current_date", ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
//		//����autoUpdate�����жϣ�autoUpdate����or�رպ�̨����
//		if(prefs.getBoolean("autoUpdate", false))
//		{
//			Toast.makeText(this,
//					"�ر��Զ�����", Toast.LENGTH_LONG).show();
//			Intent intent = new Intent(this, AutoUpdateService.class);
//			stopService(intent);
//		}
//		else{
//			Toast.makeText(this,
//					"�����Զ�����", Toast.LENGTH_LONG).show();
//			Intent intent = new Intent(this, AutoUpdateService.class);
//			startService(intent);
//		}
	}
	
	/**
	 * ͨ��handlerȥ����
	 */
    private Handler mHandler = new Handler()  
    {  
        public void handleMessage(android.os.Message msg)  
        {  
            switch (msg.what)  
            {  
            case REFRESH_COMPLETE:  
            	//ˢ�¶���,���Ҫˢ�µĶ���
				publishText.setText("ͬ����...");
//				weatherInfoLayout.setVisibility(View.INVISIBLE);
//				cityNameText.setVisibility(View.INVISIBLE);
//				
//				//���countyCode���ܵõ�����ΪchooseareaActivityû�д�����
//				/**
//				 * �� str �ǿգ�null����մ���""��ʱ����Ϊ��
//				 */
//				if(!TextUtils.isEmpty(countyCode)){
//					//���ؼ�����ʱ��ȥ��ѯ����
//					publishText.setText("ͬ����...");
//					weatherInfoLayout.setVisibility(View.INVISIBLE);
//					cityNameText.setVisibility(View.INVISIBLE);
//					queryWeatherCode(countyCode);
//				} else {
//					//û���ؼ�����ʱ��ֱ����ʾ��������
//					showWeather();
//				}
//				          
//                mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));  
//                mAdapter.notifyDataSetChanged();  
                mSwipeLayout.setRefreshing(false);     //���ػ�����ʾ������   
                break;
            }  
        };  
    };
    
    //ˢ��
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub 
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);   //what=272  milles = 2000
	}
	
	 //Ϊ�ײ���������ʵ�ּ�����  
    private OnClickListener  itemsOnClick = new OnClickListener(){  
    	
        public void onClick(View v) {  
            menuWindow.dismiss();      //�ȴ�switch�¼�ִ�����Ժ�popupwindow��ʧ
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
     * ������add��ť�����¼�
     * @param view
     */
	public void add(View view){
		//��city_selected�޸�Ϊfalse����������ChooseAreaActivity�Ͳ���ֱ����ʾ�س������ˡ�
//		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//		editor.putBoolean("city_selected", false);
//		editor.commit();
		Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
		return;
	}
	
	//�ٶȵ�ͼ��λ
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}
	
	//�ٶȵ�ͼ��λsdk��ʼ�����趨��λģʽ��λƵ��
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//���ö�λģʽ
		option.setCoorType(tempcoor);//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵbd09ll
		int span=1000;
		option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);   //checkGeoLocation.isChecked()  //����Ϊ���������
		mLocationClient.setLocOption(option);
	}
	
}
