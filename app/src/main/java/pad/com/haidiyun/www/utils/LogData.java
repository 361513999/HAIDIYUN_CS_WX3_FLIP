package pad.com.haidiyun.www.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * Created by Administrator on 2017/11/18/018.
 */

public class LogData {
    public void update() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect("120.76.195.107", 21);
        boolean log = ftpClient.login("logger", "Log123");
        if (!log) {

            return;
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setConnectTimeout(6000);

    }

}
