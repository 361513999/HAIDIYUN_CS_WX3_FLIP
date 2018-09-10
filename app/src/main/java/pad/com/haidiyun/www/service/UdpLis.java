package pad.com.haidiyun.www.service;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.UnZipFile;
import pad.com.haidiyun.www.common.UnZipOrRarUtils;
import pad.com.haidiyun.www.common.ZipUtils;
import pad.com.haidiyun.www.download.DownloadListener;
import pad.com.haidiyun.www.download.DownloadTask;
import pad.com.haidiyun.www.download.DownloadTaskManager;
import pad.com.haidiyun.www.utils.CopyFile;

/**
 * Created by Administrator on 2017/12/8/008.
 */

public class UdpLis implements Runnable {
    private boolean Loading  = false;
    private    Boolean IsThreadDisable = false;//指示监听线程是否终止
    private SharedUtils sharedUtils;
    public void stop(boolean IsThreadDisable){
        this.IsThreadDisable = IsThreadDisable;
    }
    private Context context;
    public UdpLis(Context context){
        this.context = context;
        sharedUtils = new SharedUtils(Common.CONFIG);
        File file = new File(Common.SOURCE_ZIP);
        if(!file.exists()){
            file.mkdirs();
        }
    }
    private   String LOCAL_NAME = "updata.zip";
    @Override
    public void run() {
        start();
//        unzip();
//        try {
//            up();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void up() throws IOException {
        String str_send = "Hello UDPclient";
        byte[] buf = new byte[1024];
        //服务端在3000端口监听接收到的数据
        DatagramSocket ds = new DatagramSocket(6401);
        //接收从客户端发送过来的数据
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
        System.out.println("server is on，waiting for client to send data......");
        boolean f = true;
        while(f){
            //服务器端接收来自客户端的数据
            ds.receive(dp_receive);
            System.out.println("server received data from client：");
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) +
                    " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            System.out.println(str_receive);
            //数据发动到客户端的3000端口
            DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),dp_receive.getAddress(),9000);
            ds.send(dp_send);
            //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
            //所以这里要将dp_receive的内部消息长度重新置为1024
            dp_receive.setLength(1024);
        }
        ds.close();
    }

    String SN = Build.SERIAL;
    private String param(int status,String tip){
        return  "{\"SerialNo\":\"" + SN + "\",\"Status\":"+status+",\"Desc\":\""+tip+"\"}";
    }
    DatagramSocket datagramSocket;
    private void start(){
        // UDP服务器监听的端口
        Integer port = 10000;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[1024];
        try {
            // 建立Socket连接
            if (datagramSocket != null) {
                datagramSocket.close();
            }
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setBroadcast(true);
            WifiManager manager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock lock= manager.createMulticastLock("UDPwifi");

            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);


                while (!IsThreadDisable) {
                    // 准备接收数据
                    lock.acquire();
                    Log.d("UDP Demo", "准备接受~~");


                    datagramSocket.receive(datagramPacket);

                    String strMsg=new String(datagramPacket.getData()).trim();
                    Log.d("UDP Demo", datagramPacket.getAddress()
                            .getHostAddress().toString()
                            + ":" +strMsg );
                    Log.d("udp",datagramPacket.getAddress().getHostAddress());

//                    datagramPacket.getAddress().getHostName()
                        JSONObject jsonObject = new JSONObject(strMsg);
                        String ip = datagramPacket.getAddress().getHostAddress();

                        int pot = jsonObject.getInt("HttpPort");
                        Log.v("udp",sharedUtils.getStringValue("udp_md5"));
                         String name = ip+":"+pot+jsonObject.getString("FileUrl");
                            sharedUtils.setStringValue("udp_url",name);
                        if(!sharedUtils.getStringValue("udp_md5").equals(jsonObject.getString("Md5"))){
                            sharedUtils.setStringValue("udp_md5",jsonObject.getString("Md5"));

                            String ffileName = name.substring(name.lastIndexOf("\\")+1);
                            //启动下载
                             Log.v("udp",sharedUtils.getStringValue("udp_url")+"=="+Common.SOURCE_ZIP+"=="+ffileName);

                            Log.v("udp","不同下载");
                            try {
                                recyle(ip,pot,param(0,"准备更新"));
                                download("http://"+sharedUtils.getStringValue("udp_url"),LOCAL_NAME,Common.SOURCE_ZIP,jsonObject.getString("Md5"),ip,pot);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Loading = false;

                            }

                        }else{
                            if(!Loading){
                                //如果没有在下载
                                if(!sharedUtils.getStringValue("udp_success").equals(jsonObject.getString("Md5"))){
                                    Log.v("udp","相同下载");
                                    try {
                                        recyle(ip,pot,param(0,"准备更新"));
                                        download("http://"+sharedUtils.getStringValue("udp_url"),LOCAL_NAME,Common.SOURCE_ZIP,jsonObject.getString("Md5"),ip,pot);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Loading = false;

                                    }
                                }
                            }else{
                                Log.v("udp","无需下载");
                            }

                        }
                    datagramPacket.setLength(1024);
                    lock.release();
                    ip = null;
                }


        } catch (Exception e) {
            Log.d("UDP Demo", "SocketException");
            e.printStackTrace();
            datagramSocket.disconnect();
            datagramSocket.close();
            datagramSocket = null;
            start();
        }

    }
    private void recyle(String ip,int pot,String param){
        OkHttpUtils.postString()
                .url("http://"+ip+":"+pot+"/api/?Handler=FileDownload&Method=ReportPadSyncStatus")
//			        .addParams("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(param)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.v("udp",response);
            }
        });

    }


    private String UN_ = Common.SOURCE_ZIP+"UN/";
    private void download(String urlStr,String fileName,String savePath,String success,String ip,int pot) throws Exception{

            Loading = true;
           CopyFile copyFile = new CopyFile();
            copyFile.delFile(Common.SOURCE_ZIP+LOCAL_NAME);
            Log.v("udp下载地址",urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
             if(!conn.getContentType().equals("application/octet-stream")){
                 throw  new Exception();
             }
             InputStream inputStream = conn.getInputStream();

            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdir();
            }
            File file = new File(saveDir+File.separator+fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if(fos!=null){
                fos.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }
            recyle(ip,pot,param(1,"下载完成"));
            //下载完成

            Loading = true;
             Log.v("udp","info:"+url+" download success");
            //进行文件解压
           // unzip();
//            UnZipOrRarUtils.unRarFile(Common.SOURCE_ZIP+LOCAL_NAME,Common.SOURCE_ZIP+"UN");
           // ZipUtils.decompress(Common.SOURCE_ZIP+LOCAL_NAME,Common.SOURCE_ZIP+"UN");

        UnZipFile.unZipFiles(Common.SOURCE_ZIP+LOCAL_NAME,Common.SOURCE_ZIP+"UN");
            copyFile.delFolder(Common.SOURCE);
            copyFile.copyFolder(UN_+"source",Common.SOURCE);
             String DATABASE_PATH = "/data/data/"
                + BaseApplication.application.getPName() + "/databases/";
            copyFile.delFolder(DATABASE_PATH);
            File fs = new File(DATABASE_PATH);
            if(!fs.exists()){
                fs.mkdirs();
            }
            copyFile.copyFile(UN_+"BookSystem.sqlite",DATABASE_PATH+"BookSystem.sqlite");
        P.c("更新完成");
        sharedUtils.setStringValue("udp_success",success);
            BaseApplication.application.resetApplicationAll();

        }





    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public    byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            Log.v("udp","下载");
        }
        bos.close();
        return bos.toByteArray();
    }
}
