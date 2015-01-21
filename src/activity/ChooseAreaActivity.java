package activity;

import java.util.ArrayList;
import java.util.List;

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
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog; 
	private TextView titleText;
	private ListView listView;              //
	private ArrayAdapter<String> adapter;   //���������������Ӻ�����ݺ�ǰ����ʾ���������ӿ�
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);//ʹ���ⲿ���ڵ���
		setContentView(R.layout.choose_area);    //�O��activity�@ʾ����
		
		Log.d("ChooseAreaActivity", "onCreate execute");
		listView = (ListView) findViewById(R.id.list_view); //��ȡxml�ļ������Ӧ��id
		titleText = (TextView) findViewById(R.id.title_text); //��ȡxml�ļ������Ӧ��id
		
		//���ݵ���ͼһ�����������裬1���½�һ������������ 2����������������Դ 3����ͼListView����������
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList); //��������������Դ
		listView.setAdapter(adapter);    //3����ͼListView����������
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);	//?
		//ΪListView�����б��������������ȴ���ť����
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int index, long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE){			//��ѡ����ǳ��л���ʡ
					selectedProvince = provinceList.get(index);   
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}
			}
		});
		queryProvince(); //������ǳ�ʼ���������￪ʼ����ʡ�����ݡ�
	}
	
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvince(){
		provinceList = coolWeatherDB.loadProvinces();   
		if(provinceList.size() > 0){	
			dataList.clear();
			for (Province province : provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();  //ˢ������
			listView.setSelection(0);		//���õ�ǰѡ����
			titleText.setText("�й�");		//Sets the string value of the TextView
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null, "province");
		}
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
			listView.setSelection(0);		//���õ�ǰѡ����   ?
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
			listView.setSelection(0);		//���õ�ǰѡ����
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
			finish();
		}
	}
}



