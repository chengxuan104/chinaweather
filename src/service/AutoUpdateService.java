package service;

import java.util.Date;

import org.apache.http.protocol.HTTP;

import receiver.AlarmReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import activity.WeatherActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public void onCreate(){
//		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
//		int sTime =  1000 ;   //2s
//		long triggerAtTime = SystemClock.elapsedRealtime() + sTime;//ϵͳ���������ʱ�䣬����˯��ʱ��
//		Intent i = new Intent(this, AlarmReceiver.class);       //�½�һ����ͼ���ȴ��л���AlarmRreceiverclass
//		PendingIntent pi =	PendingIntent.getBroadcast(this, 0, i, 0); //��ȡһ���ܹ�ִ�еĹ㲥��pendingIntent
//		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);   //2s��AlarmReceiver���onReceiver���õ�ִ��
//
//	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("AutoUpdateService", new Date().toString());
				updateWeather();     //��������
			}
		}).start();
		
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		int sTime = 60 * 1000 ;   //1hour
		long triggerAtTime = SystemClock.elapsedRealtime() + sTime;//ϵͳ���������ʱ�䣬����˯��ʱ��
		Intent i = new Intent(this, AlarmReceiver.class);       //�½�һ����ͼ���ȴ��л���AlarmRreceiverclass
		PendingIntent pi =	PendingIntent.getBroadcast(this, 0, i, 0); //��ȡһ���ܹ�ִ�еĹ㲥��pendingIntent
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);   //2s��AlarmReceiver���onReceiver���õ�ִ��
		
		//queryWeatherCode(countyCode);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * ��������
	 */
	public void updateWeather() {
	// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onDestroy(){
		
	}

}
