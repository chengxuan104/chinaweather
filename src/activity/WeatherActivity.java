package activity;



import java.sql.Date;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.chinaweather.app.R;
import android.app.Activity;

//import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity{
	private LinearLayout weatherInfoLayout;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ���ؼ�
		cityNameText = (TextView) findViewById(R.id.city_name);
		return_button =  (Button) findViewById(R.id.return_button); //cx
		refresh_button =  (Button) findViewById(R.id.refresh_button); //cx
		
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		
		final String countyCode = getIntent().getStringExtra("county_code"); //��choose������һ��putExtra��county_code
		
		//���ذ�ť
		return_button.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//��city_selected�޸�Ϊfalse����������ChooseAreaActivity�Ͳ���ֱ����ʾ�س������ˡ�
//				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//				editor.putBoolean("city_selected", false);
//				editor.commit();
				Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
				return;
			}
		});
			
		//ˢ��������ť
		refresh_button.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * �� str �ǿգ�null����մ���""��ʱ����Ϊ��
				 */
				if(!TextUtils.isEmpty(countyCode)){
					//���ؼ�����ʱ��ȥ��ѯ����
					publishText.setText("ͬ����...");
					weatherInfoLayout.setVisibility(View.INVISIBLE);
					cityNameText.setVisibility(View.INVISIBLE);
					queryWeatherCode(countyCode);
				}
			}
		});
		
		
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
		
		//������̨����
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
}
