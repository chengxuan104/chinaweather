package util;

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
}
