package util;

/**
 * �ص����񷵻ؽ��
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
