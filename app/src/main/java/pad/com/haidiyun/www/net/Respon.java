package pad.com.haidiyun.www.net;

public interface Respon {
	// 失败
	public void onFail();

	// 进度
	public void onProgress(String progress);

	// 成功
	public void onSuccess(String str);
	public void timeOut();
}