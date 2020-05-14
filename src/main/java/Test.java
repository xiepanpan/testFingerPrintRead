import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author: xiepanpan
 * @Date: 2020/5/9
 * @Description:
 */
public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {

        String[] pName = new String[8*128];
        short portReturnCode;
//        //1.枚举设备
//        try
//        {
//            portReturnCode = D5EnumDevice(pName);
//            System.out.println("打开端口返回值：" + String.valueOf(portReturnCode));
//            if (portReturnCode==0) {
//                System.out.println("失败");
//            }
//        }
//        catch(Exception ex)
//        {
//            System.out.println("打开端口调用异常！"+ ex.getMessage());
//        }

//        long code;
//        short uDeviceID = 0;
//        short uMS =40;
//        try
//        {
//            code = D5Beep(uDeviceID,uMS);
//            logger.info("打开端口返回值：" + String.valueOf(code));
//            if (code==0) {
//                System.out.println("失败");
//            }
//        }
//        catch(Exception ex)
//        {
//            logger.error("打开端口调用异常！"+ ex.getMessage());
//        }

        long code;
        short uDeviceID = 1;
        short uMS =40;
        try
        {
            code = D5OpenDevice(uDeviceID);
            System.out.println(code);
            if (code==0) {
                System.out.println("失败");
            }
        }
        catch(Exception ex)
        {
            logger.error("打开端口调用异常！"+ ex.getMessage());
        }

    }

    private static long D5OpenDevice(short uDeviceID) throws NativeException, IllegalAccessException{
        JNative n = null;
        try
        {
            n = new JNative("D5ScannerS77.dll", "D5OpenDevice");
            n.setRetVal(Type.LONG); // 指定返回参数的类型
            n.setParameter(0, uDeviceID);
            n.invoke(); // 调用方法
            System.out.println(n.getRetVal());
            return Long.parseLong(n.getRetVal());
        }
        finally
        {

        }
    }

    //蜂鸣
    private static long D5Beep(short uDeviceID, short uMS)throws NativeException, IllegalAccessException, UnsupportedEncodingException {
        JNative n = null;
        try
        {
            n = new JNative("D5ScannerS77.dll", "D5Beep");
            n.setRetVal(Type.LONG); // 指定返回参数的类型
            n.setParameter(0, uDeviceID);
            n.setParameter(1, uMS);
            n.invoke(); // 调用方法
            return Long.parseLong(n.getRetVal());
        }

        finally
        {

        }
    }


    private static short D5EnumDevice(String[] pDeviceName)throws NativeException, IllegalAccessException, UnsupportedEncodingException {
        JNative n = null;
        try
        {
            n = new JNative("D5ScannerS77.dll", "D5EnumDevice");
//            n.setRetVal(Type.INT); // 指定返回参数的类型
//            Pointer b = new Pointer(MemoryBlockFactory.createMemoryBlock(4*10));

            Pointer rva=new Pointer(MemoryBlockFactory.createMemoryBlock(16));
            rva.setStringAt(0, pDeviceName[0]);
            n.setParameter(0,rva);
            n.invoke(); // 调用方法
            byte[] by = new byte[8*128];
            by = rva.getMemory();
            return Short.parseShort(n.getRetVal());
        }
        finally
        {

        }
    }
}
