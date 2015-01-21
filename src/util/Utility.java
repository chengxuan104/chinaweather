package util;

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
}
