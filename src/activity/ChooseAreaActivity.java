package activity;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.List;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;



import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;

import com.chinaweather.app.R;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;  


public class ChooseAreaActivity extends Activity{
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog; 
	private TextView titleText;
	private ListView listView;              //
	private ArrayAdapter<String> adapter;   //适配器，用于连接后端数据和前端显示的设配器接口
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	
	/**
	 * 主界面设置
	 */
	private Button setting;
	
	
	/**
	 *	是否从weatherActivity中跳转过来 
	 */
	private boolean isFromWeatherActivity;
	
	/**
	 * 省列表     
	 */
	private List<Province> provinceList;
	
	/**
	 * 市列表 
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<County> countyList;
	
	/**
	 *  选中的省份
	 */
	private Province selectedProvince;
	
	/**
	 *  选中的城市
	 */
	private City selectedCity;
	
	/**
	 *  当前选中的级别
	 */
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);        //保存活动的状态	
		requestWindowFeature(Window.FEATURE_NO_TITLE);//不在活动中显示标题栏
		setContentView(R.layout.choose_area);    //設置activity顯示界面
		
//		//判断当前是否已经是county，假如是已经选择了county直接跳转到WeatherActivity
//		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
//			Intent intent = new Intent(this, WeatherActivity.class);
//			startActivity(intent);
//			finish();
//			return;
//		}
		
		
		listView = (ListView) findViewById(R.id.list_view);      //获取xml文件里相对应的id
		titleText = (TextView) findViewById(R.id.title_text);    //获取xml文件里相对应的id
		setting = (Button) findViewById(R.id.setting_button);    //开关自动更新
		

		
		//设置一个autoUpdate的值
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
		editor.putBoolean("autoUpdate", true);
		editor.commit();
		
		//侦听设置按钮，假如点下，关闭自动更新
		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this);
				//判断autoUpdate的值，设为相反的值，实现类似滑动按钮 
				if(prefs.getBoolean("autoUpdate", false))
				{
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
					editor.putBoolean("autoUpdate", false);
					editor.commit();
				}
				else
				{
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
					editor.putBoolean("autoUpdate",true);
					editor.commit();
				}
			}
		});
		
		//数据到视图一般是三个步骤，1、新建一个数据适配器 2、适配器加载数据源 3、视图ListView加载适配器
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList); //适配器加载数据源
		listView.setAdapter(adapter);    //3、视图ListView加载适配器
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);	//?
		//为ListView设置列表项点击监听器，等待按钮按下
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int index, long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE){			//看选择的是城市还是省
					selectedProvince = provinceList.get(index);   
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(index).getCountyCode();   //得到县级的代号
					
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class); //切换activity
					intent.putExtra("county_code", countyCode);    //把这个countycode传到另外一个activity里面去
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvince(); //上面的是初始化，从这里开始加载省级数据。
	}
	

	
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryProvince(){
		provinceList = coolWeatherDB.loadProvinces();   
		if(provinceList.size() > 0){	
			dataList.clear();
			for (Province province : provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();  //刷新数据
			listView.setSelection(0);		//设置当前选中项
			titleText.setText("中国");		//Sets the string value of the TextView
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
	}
	
	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryCities(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId()); //第一次读取怎么可能有数据？当然会报没有province_id的错误啊？
		if (cityList.size() > 0){
			dataList.clear();
			for (City city : cityList)
			{
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();  //刷新数据
			listView.setSelection(0);		//设置当前选中项   ?
			titleText.setText(selectedProvince.getProvinceName());		//Sets the string value of the TextView
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryCounties(){
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0){
			dataList.clear();
			for (County county : countyList)
			{
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();  //刷新数据
			listView.setSelection(0);		//设置当前选中项
			titleText.setText(selectedCity.getCityName());		//Sets the string value of the TextView
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");  //从服务器上查询
		}
	}
	
	/**
	 * 
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * @param type
	 */
	//无法传入provincecode或者不存在provincecode
	private void queryFromServer(final String code, final String type){
		String address;
		if (!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml"; //不空为市级或者县的数据
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml"; //省级数据
		}
		showProgressDialog();   //显示进度对话框
		
		 //调用sendHttpRequest传入地址和对象
		 //实现HttpCallbackListener这个接口，并调用onFinsh，产生回调，等待数据传回来。
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				//得到返回的response数据以后开始对response进行解析，首先要判断到底是省的数据还是。。的数据
				if ("province".equals(type)){
					result = Utility.handleProvinceResponse(coolWeatherDB, response);   //解析返回数据
				}else if ("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)){
					result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑。另一种方法是handle也可以实现这样的效果
					//用于更新ui视图，调用query...的方法进行查询，这样可以实时的更新数据
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();     //关闭显示进度对话框
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
										"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
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
			}
			finish();
		}
	}
}



