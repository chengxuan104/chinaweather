package activity;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;



import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;


import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.chinaweather.app.R;
import android.R.string;
//import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;  
import android.widget.SearchView;

public class ChooseAreaActivity extends Activity implements SearchView.OnQueryTextListener{
	
	private LocationClient mLocationClient;    //��������ǰٶȵ�ͼ��
	private TextView LocationResult;
	private Button startLocation;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="bd09ll";         //�ðٶȵ�ͼbd09ll

	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog; 
	private TextView titleText;
	private GridView gridView;              //
	private ArrayAdapter<String> adapter;   //���������������Ӻ�����ݺ�ǰ����ʾ���������ӿ�
	private List<String> dataList = new ArrayList<String>();
	
	private List<String> mStrings = new ArrayList<String>(); //�����е��������ص�mStrings������ 
	private ArrayAdapter<String> lvAdapter;
	private CoolWeatherDB coolWeatherDB;

	/**
	 * 
	 */
	private List<County> countyList4mStrings;
	private List<City> cityList4mStrings;
	private List<Province> provinceList4mStrings;
	private Province selectedProvince1;
	private City selectedCity1;
	/**
	 * ����һ����������
	 */
	private SearchView sv;
	
	/**
	 * ��һ���嵥�б�
	 */
	private ListView lv;
	
	/**
	 * ����������
	 */
	private Button setting;
	
	
	/**
	 *	�Ƿ��weatherActivity����ת���� 
	 */
	private boolean isFromWeatherActivity;
	
	/**
	 * ʡ�б�     
	 */
	private List<Province> provinceList;
	
	/**
	 * ���б� 
	 */
	private List<City> cityList;
	
	/**
	 * ���б�
	 */
	private List<County> countyList;

	/**
	 *  ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	
	/**
	 *  ѡ�еĳ���
	 */
	private City selectedCity;
	
	/**
	 *  ��ǰѡ�еļ���
	 */
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);        //������״̬	
		requestWindowFeature(Window.FEATURE_NO_TITLE);//���ڻ����ʾ������
		setContentView(R.layout.choose_area);    //�O��activity�@ʾ����
		
//		//�жϵ�ǰ�Ƿ��Ѿ���county���������Ѿ�ѡ����countyֱ����ת��WeatherActivity
//		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
//			Intent intent = new Intent(this, WeatherActivity.class);
//			startActivity(intent);
//			finish();
//			return;
//		}
		
		
		gridView = (GridView) findViewById(R.id.grid_view);      //��ȡxml�ļ������Ӧ��id
		titleText = (TextView) findViewById(R.id.title_text);    //��ȡxml�ļ������Ӧ��id
		//setting = (Button) findViewById(R.id.setting_button);    //�����Զ�����
		
		

		//����һ��listview��ר�������Զ���ʾ������Ŀ������ʱ����
//
//		lv = (ListView) findViewById(R.id.lv); //
//		lvAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mStrings);
//		lv.setAdapter(lvAdapter);
//		lv.setTextFilterEnabled(true);
		
		//����һ��autoUpdate��ֵ
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
		editor.putBoolean("autoUpdate", true);
		editor.commit();
		
		//�������ð�ť��������£��ر��Զ�����
//		setting.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this);
//				//�ж�autoUpdate��ֵ����Ϊ�෴��ֵ��ʵ�����ƻ�����ť 
//				if(prefs.getBoolean("autoUpdate", false))
//				{
//					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
//					editor.putBoolean("autoUpdate", false);
//					editor.commit();
//				}
//				else
//				{
//					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
//					editor.putBoolean("autoUpdate",true);
//					editor.commit();
//				}
//			}
//		});
		
		//���ݵ���ͼһ�����������裬1���½�һ������������ 2����������������Դ 3����ͼgridView����������
		adapter = new ArrayAdapter<String>(this, R.layout.items, dataList); //��������������Դ
		gridView.setAdapter(adapter);    //3����ͼgridView����������
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);	//?
		
		//����������Ϣ�����ݿ���
//		loadDataArea();
		
		//���������CoolWeatherDB.getInstance(this);֮��
//		loadData2mStrings();
		

		
		//ΪgridView�����б��������������ȴ���ť����
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int index, long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE){		   //��ѡ����ǳ��л���ʡ
					selectedProvince = provinceList.get(index);   
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(index).getCountyCode();   //�õ��ؼ��Ĵ���
					
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class); //�л�activity
					intent.putExtra("county_code", countyCode);    //�����countycode��������һ��activity����ȥ
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvince(); //������ǳ�ʼ���������￪ʼ����ʡ�����ݡ�
		
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
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvince(){
/*		provinceList = coolWeatherDB.loadProvinces();   
		if(provinceList.size() > 0){	
			dataList.clear();
			for (Province province : provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();  //ˢ������
			gridView.setSelection(0);		//���õ�ǰѡ����
			titleText.setText("�й�");		//Sets the string value of the TextView
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}*/
		queryFromServer(null, "province");
	}
	
	/**
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCities(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId()); //��һ�ζ�ȡ��ô���������ݣ���Ȼ�ᱨû��province_id�Ĵ��󰡣�
		if (cityList.size() > 0){
			dataList.clear();
			for (City city : cityList)
			{
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();  //ˢ������
			gridView.setSelection(0);		//���õ�ǰѡ����   ?
			titleText.setText(selectedProvince.getProvinceName());		//Sets the string value of the TextView
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCounties(){
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0){
			dataList.clear();
			for (County county : countyList)
			{
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();  //ˢ������
			gridView.setSelection(0);		//���õ�ǰѡ����
			titleText.setText(selectedCity.getCityName());		//Sets the string value of the TextView
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");  //�ӷ������ϲ�ѯ
		}
	}
	
	/**
	 * 
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
	 * @param type
	 */
	//�޷�����provincecode���߲�����provincecode
	private void queryFromServer(final String code, final String type){
		String address;
		if (!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml"; //����Ϊ�м������ص�����
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml"; //ʡ������
		}	

		showProgressDialog();   //��ʾ���ȶԻ���
		
		 //����sendHttpRequest�����ַ�Ͷ���
		 //ʵ��HttpCallbackListener����ӿڣ�������onFinsh�������ص����ȴ����ݴ�������
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				//�õ����ص�response�����Ժ�ʼ��response���н���������Ҫ�жϵ�����ʡ�����ݻ��ǡ���������
				if ("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);   //������������
				}else if ("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)){
					result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//ͨ��runOnUiThread()�����ص����̴߳����߼�����һ�ַ�����handleҲ����ʵ��������Ч��
					//���ڸ���ui��ͼ������query...�ķ������в�ѯ����������ʵʱ�ĸ�������
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();     //�ر���ʾ���ȶԻ���
							if ("province".equals(type)) {
								queryProvince();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("county".equals(type)){
								queryCounties();
							}
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
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/**
	 * 
	 * ��ѯʡ�������ݴ洢�����ݿ���
	 * @param type
	 */
	//�޷�����provincecode���߲�����provincecode
	private void queryFromServer4Area(final String code, final String type){
		String address;
		if (!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml"; //����Ϊ�м������ص�����
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml"; //ʡ������
		}
		showProgressDialog();   //��ʾ���ȶԻ���
		
		 //����sendHttpRequest�����ַ�Ͷ���
		 //ʵ��HttpCallbackListener����ӿڣ�������onFinsh�������ص����ȴ����ݴ�������
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				//�õ����ص�response�����Ժ�ʼ��response���н���������Ҫ�жϵ�����ʡ�����ݻ��ǡ���������
				if ("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);   //������������
				}else if ("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)){
					result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
			}
	
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog(){
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
	 */
	@Override
	public void onBackPressed(){
		if (currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			if(isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}else
			{
				//����������ʾ�Ļ���
			}
			finish();
		}
	}



	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		
		return false;
	}
	
	/**
	 * ����������Ϣ
	 */
	public void loadDataArea(){
		queryFromServer4Area(null, "province");				 //����ʡ������
		provinceList4mStrings=coolWeatherDB.loadProvinces();
		for (Province province : provinceList4mStrings)  //foreach  ����provinceList4mStrings��ÿ������provinceΪ++����
		{
			selectedProvince1 = province;       //����list��ȡ��ÿ��ʡ�����е�����
			queryFromServer4Area(selectedProvince1.getProvinceCode(), "city");  //�ӷ������϶�ȡ���ݴ浽���ݿ���
			cityList4mStrings = coolWeatherDB.loadCities(selectedProvince1.getId()); 
			for (City city : cityList4mStrings)
			{
				selectedCity1 = city;
				queryFromServer4Area(selectedCity1.getCityCode(), "county");  //�ӷ������ϲ�ѯ�س���Ϣ
			 }
		}
	}
	
	/**
	 * �������е�������mStrings��,Ȼ����ʾ����
	 */
	public void loadData2mStrings(){
		mStrings.clear();
		provinceList4mStrings=coolWeatherDB.loadProvinces(); 
		for (Province province : provinceList4mStrings)  //foreach  ����provinceList4mStrings��ÿ������provinceΪ++����
		{
			selectedProvince1 = province;       //����list��ȡ��ÿ��ʡ�����е�����
			cityList4mStrings = coolWeatherDB.loadCities(selectedProvince1.getId()); //��һ�ζ�ȡ��ô���������ݣ���Ȼ�ᱨû��province_id�Ĵ��󰡣�
			for (City city : cityList4mStrings)
			{
				//mStrings.add(city.getCityName());
				selectedCity1 = city;
				countyList4mStrings = coolWeatherDB.loadCounties(selectedCity1.getId());
				for (County county : countyList4mStrings)
				{
					
					//String countyCode = countyList.get(index).getCountyCode();   //�õ��ؼ��Ĵ���
					mStrings.add(province.getProvinceName()+"-"+
								 city.getCityName()+"-"+county.getCountyName());
				}
			 }
			 lvAdapter.notifyDataSetChanged();  //ˢ������
		}
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



