package pad.com.haidiyun.www.common;

import android.hardware.UsbSupply;

public class Led {
    //static SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
    public static void led(int index){
        P.c("打开LED");
        try {
            close();
            UsbSupply.LedControlOpen();
            UsbSupply.LedControlSet(index);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        //sharedUtils.setIntValue("led",index);
    }

    public static void ledClose(){
        try {
            UsbSupply.LedControlClose();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        try {
            UsbSupply.LedControlOpen();
            UsbSupply.LedControlSet(2);
            UsbSupply.LedControlSet(4);
            UsbSupply.LedControlSet(6);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }
    public static void reset(int index){

        try {
            close();
            UsbSupply.LedControlOpen();

            UsbSupply.LedControlSet(index);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        // sharedUtils.setIntValue("led",index);

    }
}
