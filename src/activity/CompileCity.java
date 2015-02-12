package activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinaweather.app.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class CompileCity extends Activity {
	
	int index;
	/**
	 * 
	 */
	ArrayList<Integer> cityList = new ArrayList<Integer>();
	/**
	 * ����һ��list,������hashmap
	 */
	List<Map<String, Object>> from_weatherAcv_listItems = new ArrayList<Map<String,Object>>();    //��������weather_activity��list����
	/**
	 * ����һ��listview����
	 */
	ListView lv;
	/**
	 * ����һ��������������ʾ����
	 */
	SimpleAdapter madapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.compile_city);
		
		from_weatherAcv_listItems = (ArrayList)getIntent().getSerializableExtra("from_weatherAcv_listItems");
		lv = (ListView)findViewById(R.id.compile_listview);
		 
		 //˼·�����¶���һ��layout������ʾɾ��list�Ľ���
		 madapter = new SimpleAdapter(  this, 
										from_weatherAcv_listItems,
										R.layout.del_city, 
										new String[]{"delete", "cityName", "temperature"},
										new int []{R.drawable.del, R.id.del_cityname, R.id.del_templature});
		lv.setAdapter(madapter);
		
		//������һ��item�����//ɾ��listview����
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						from_weatherAcv_listItems.remove(position); //ɾ����Ӧ�����������
						madapter.notifyDataSetChanged();      //ˢ������
						switch(position)
						{
						 case 0:
							index = 0;						
							break;
						 case 1:
							index = 1;
							break;
						 case 2:
							index = 2;
							break;
						 case 3:
							index = 3;
							break;
						}
						cityList.add(index);   //�ѵ����index�ŵ�citylist��
					}
				});
			}
		});
	}
	
	//��ɰ�ť
	public void done(View view){
		//���ص���һ��activity
		Intent intent = new Intent(CompileCity.this, WeatherActivity.class);
		
		intent.putExtra("from_compileCity_listItems", (Serializable)cityList);  //��list��ʱ��һ��Ҫ���л��ӿ�
		startActivity(intent);
		//cityList.clear();
		onBackPressed();
	}
	
	@Override
	public void onBackPressed(){

		finish();
	}
}
