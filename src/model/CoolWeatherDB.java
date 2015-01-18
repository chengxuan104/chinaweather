package model;

import java.util.ArrayList;
import java.util.List;

import db.CoolWeatherOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 将构造方法私有化
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
												//Create and/or open a database that will be used for reading and writing
		db = dbHelper.getWritableDatabase();    //在调用这个方法的时候假如是第一次，那么系统会先调用onCreate()
	}
	
	/**
	 * 获取CoolWwatherDB的实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if (coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
			
		}
		return coolWeatherDB;
	}
	
	/**
	 * 将province实例存储到数据库中
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();  //可以存储一组值
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);    	//将得到的一组值插入到Province中
		}
	}
	
	/**
	 * 从数据库中取出province的信息
	 * 从数据库取出来数据， 然后 通过model封装， 再添加到list 
	         然后返回list，list里面就是你查到的data
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();  //创建一个可以接受Province的数组
		Cursor cursor =db.query("Province", null,  null,  null,  null,  null,  null); //限制查找出来的结果，包括过滤，分页
		if(cursor.moveToFirst()){							//把游标移动第一位，
			do{
				Province province = new Province(); 		
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));//這段不是太懂，不知道為什麼不用get而用set
				province.setProvinceName(cursor
						.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor
						.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	/**
	 * 将City实例存储到数据库
	 * 
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
			
		}
	}

	/**
	 * 从数据库读取某省下所有的城市信息
	 * 从数据库取出来数据， 然后 通过model封装， 再添加到list 
	         然后返回list，list里面就是你查到的data
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor =db.query("City", null,  "province_id = ?", 
				new String[]{String.valueOf(provinceId)},  null,  null,  null); 
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor
						.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor
						.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	/**
	 * 将County实例存储到数据库中
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	/**
	 * 从数据库读取某城市下所有的县信息。
	 * 从数据库取出来数据， 然后 通过model封装， 再添加到list 
	         然后返回list，list里面就是你查到的data
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor =db.query("County", null,  "city_id = ?", 
				new String[]{String.valueOf(cityId)},  null,  null,  null); 
		
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor
						.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor
						.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		
		if(cursor != null){
			cursor.close();
		}
		return list;
		
	}
}
