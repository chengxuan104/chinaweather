package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * ��Ϊ�����������ں�ʱ��������������Ҫ����һ���̣߳������̲߳��ܷ������ݣ�
 * ������Ҫ�õ��ص����ƣ�����ȷ������ͨ��onFinsh����ȥ���쳣��ͨ��onError����ȥ������
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener){
		new Thread (new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					//ʹ��java.net.URL��װHTTP��Դ��url,��ʹ��openConnection�������HttpUrlConnection����
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection(); 
				
					connection.setRequestMethod("get"); 	//�������󷽷�,����GET,POST,������˵Ҫ��д��
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					
					InputStream in = connection.getInputStream(); //��������.��һ���Ƕ�HTTP��Դ�Ķ�д����.Ҳ����ͨ��InputStream��OutputStream��ȡ��д������
					BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //ֱ�Ӵӻ�������ȡ����
					StringBuilder response = new StringBuilder(); //��stringbuffer���ƣ����ǲ���synchronization�ģ�Ч�ʸ��� 
					
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinish(response.toString());
					}
					
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					if(listener !=null){
						listener.onError(e);
					}
					
				}finally{
					if(connection != null){
						connection.disconnect();
						
					}
				}
			}
			
		}).start();
	}

}
