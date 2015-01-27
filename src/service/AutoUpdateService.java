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
//		long triggerAtTime = SystemClock.elapsedRealtime() + sTime;//系统从运行起的时间，包括睡眠时期
//		Intent i = new Intent(this, AlarmReceiver.class);       //新建一个意图，等待切换到AlarmRreceiverclass
//		PendingIntent pi =	PendingIntent.getBroadcast(this, 0, i, 0); //获取一个能够执行的广播的pendingIntent
//		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);   //2s后AlarmReceiver里的onReceiver将得到执行
//
//	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("AutoUpdateService", new Date().toString());
				updateWeather();     //更新天气
			}
		}).start();
		
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		int sTime = 60 * 1000 ;   //1hour
		long triggerAtTime = SystemClock.elapsedRealtime() + sTime;//系统从运行起的时间，包括睡眠时期
		Intent i = new Intent(this, AlarmReceiver.class);       //新建一个意图，等待切换到AlarmRreceiverclass
		PendingIntent pi =	PendingIntent.getBroadcast(this, 0, i, 0); //获取一个能够执行的广播的pendingIntent
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);   //2s后AlarmReceiver里的onReceiver将得到执行
		
		//queryWeatherCode(countyCode);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 更新天气
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
