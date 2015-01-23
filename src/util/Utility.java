package util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

//import org.json.JSONException;
//import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import model.City;
import model.CoolWeatherDB;
import model.County;
import model.Province;

public class Utility {
	/**
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, 
			String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(","); //�ָ��ַ���
			if (allProvinces != null && allProvinces.length > 0) {
				for(String p : allProvinces){
					String[] array = p.split("\\|");	//   |�ָ��ַ�����ʱ��Ҫ��\\
					Province province = new Province();	
					province.setProvinceCode(array[0]);	//ͨ��model��װ
					province.setProvinceName(array[1]);
					//���������������ݴ洢��Province����
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��м�����
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, 
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(","); //�ָ��ַ���
			if (allCities != null && allCities.length > 0) {
				for(String p : allCities){
					String[] array = p.split("\\|");	//   |�ָ��ַ�����ʱ��Ҫ��\\
					City city = new City();	
					city.setCityCode(array[0]);	//ͨ��model��װ
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//���������������ݴ洢��city����
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��ؼ�����
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, 
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCouties = response.split(","); //�ָ��ַ���
			if (allCouties != null && allCouties.length > 0) {
				for(String p : allCouties){
					String[] array = p.split("\\|");	//   |�ָ��ַ�����ʱ��Ҫ��\\
					County county = new County();	
					county.setCountyCode(array[0]);	//ͨ��model��װ
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//���������������ݴ洢��county����
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * ����handleWeatherResponse()�������ڽ�JSON��ʽ��������Ϣȫ������������saveWeather�������ڽ����ݴ洢��sharepreferences�ļ���
	 */
	/*
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp =weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp,publishTime);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * �����������ص����е�������Ϣ�洢��SharedPreferences�ļ���
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();//�������SharedPreferences���󣬵��Ǽǵø�����֮��Ҫcommit  //Create a new Editor for these preferences, through which you can make modifications to the data in the preferences and atomically commit those changes back to the SharedPreferences object.
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date(0)));	//������new Date() 
		editor.commit();
	}
}
