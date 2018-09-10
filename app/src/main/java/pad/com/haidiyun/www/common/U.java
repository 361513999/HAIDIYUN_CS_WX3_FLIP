package pad.com.haidiyun.www.common;

public class U {
	 private static final String BASE_URL = "/DataService.svc/";
	 public static final String URL = "/DataService.svc/";
	 public static final String URL_INDEX = BASE_URL+"index";
	 public static final String URL_DOWNLOAD_DATA = BASE_URL+"DownloadData";
	 public static final String URL_DOWNLOAD_IMAGES= BASE_URL+"DownloadPadImgZip";
	 public static final String URL_ORDER_SEND = BASE_URL+"SendOrderDtl";
	 public static final String URL_TABLE_OPEN =BASE_URL+"AddOrder";
	 public static final String URL_REFREF_TABLE_STATUS = BASE_URL+"GetRestTable";
	 public static final String URL_GET_ORDER = BASE_URL+"GetOrderDtl";
	 
	 public static String VISTER(String IP,String url){
		 return "http://"+IP+url;
	 }
	 
}
