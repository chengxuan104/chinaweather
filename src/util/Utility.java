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
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, 
			String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(","); //分割字符串
			if (allProvinces != null && allProvinces.length > 0) {
				for(String p : allProvinces){
					String[] array = p.split("\\|");	//   |分割字符串的时候要加\\
					Province province = new Province();	
					province.setProvinceCode(array[0]);	//通过model封装
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表中
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, 
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(","); //分割字符串
			if (allCities != null && allCities.length > 0) {
				for(String p : allCities){
					String[] array = p.split("\\|");	//   |分割字符串的时候要加\\
					City city = new City();	
					city.setCityCode(array[0]);	//通过model封装
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到city表中
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, 
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCouties = response.split(","); //分割字符串
			if (allCouties != null && allCouties.length > 0) {
				for(String p : allCouties){
					String[] array = p.split("\\|");	//   |分割字符串的时候要加\\
					County county = new County();	
					county.setCountyCode(array[0]);	//通过model封装
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储到county表中
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 其中handleWeatherResponse()方法用于将JSON格式的天气信息全部解析出来，saveWeather方法用于将数据存储到sharepreferences文件中
	 */
	/*
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
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
	 * 将服务器返回的所有的天气信息存储到SharedPreferences文件中
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();//允许更改SharedPreferences对象，但是记得更改了之后要commit  //Create a new Editor for these preferences, through which you can make modifications to the data in the preferences and atomically commit those changes back to the SharedPreferences object.
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date(0)));	//书上是new Date() 
		editor.commit();
	}
}
