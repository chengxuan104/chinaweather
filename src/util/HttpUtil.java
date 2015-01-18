package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 因为网络请求属于耗时操作，所以这里要创建一个线程，但是线程不能返回数据，
 * 所以需要用到回调机制，把正确的数据通过onFinsh传出去，异常就通过onError传出去做处理
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
					//使用java.net.URL封装HTTP资源的url,并使用openConnection方法获得HttpUrlConnection对象
					URL url = new URL(address);
					connection = (HttpURLConnection)url.openConnection(); 
				
					connection.setRequestMethod("get"); 	//设置请求方法,例如GET,POST,（网上说要大写）
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					
					InputStream in = connection.getInputStream(); //输入数据.这一步是对HTTP资源的读写操作.也就是通过InputStream和OutputStream读取和写入数据
					BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //直接从缓冲区读取内容
					StringBuilder response = new StringBuilder(); //和stringbuffer类似，但是不是synchronization的，效率更高 
					
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
