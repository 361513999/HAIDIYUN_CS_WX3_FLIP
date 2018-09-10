package android.hardware;

import java.io.FileDescriptor;
import java.io.IOException;

import java.nio.ByteBuffer;


public class UsbSupply {
    public static native int native_open();//充电打开
    public static native void native_close();//充电关闭
    public static native int LedControlOpen();//led控制打开
    public static native void LedControlSet(int cmd);//控制不同颜色的灯
    
    /*    写1，红灯亮
	写2，红灯灭
	写3，绿灯亮
	写4，绿灯灭
	写5，蓝灯亮
	写6，蓝灯灭*/

    public static native void LedControlClose();//led控制关闭
    public static native int buzzerExists();//判断蜂鸣器是否存在，1是存在 ，0是不存在
    public static native void buzzerOn(int timeout);//蜂鸣器响 的时长单位是ms
    public static native void buzzerOff();//关闭蜂鸣器
}