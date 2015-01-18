package util;

/**
 * 回调服务返回结果
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
