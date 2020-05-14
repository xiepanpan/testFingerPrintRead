import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author: xiepanpan
 * @Date: 2020/5/9
 * @Description:
 */
public class TestMatchN {
    private static Logger logger = LoggerFactory.getLogger(TestMatchN.class);
    static String strTmp ="";
    static byte[] by = new byte[192 * 256];
    static byte[] pFeatureLib = new byte[100*256];

    public static void main(String[] args) {

        //1.枚举设备
        byte[] pName = new byte[8*128];
        short portReturnCode;
        short i = D5ScannerS77.instanceDll.D5EnumDevice(pName);
        System.out.println("已连接设备数目:"+i);

//        String str = "13638b9de15f96e841560fd481498f152147a7d8814693e9a143a22b61438f54012a8c84a129aa17c1252ec16113a0abc1692d9d62671a52a24b22d5823c89db623bad82223ba517e22023d6820000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
//        System.out.println("转换后的字节数组：" + Arrays.toString(toBytes(str)));
        //2. 打开设备
//        byte[] pFeaTmp3 = toBytes(str);
        long code;
        short uDeviceID = 0;
        short uMS =40;
        try
        {
            code = D5OpenDevice(uDeviceID);
            System.out.println(code);
            if (code==0) {
                System.out.println("打开成功");
            }
        }
        catch(Exception ex)
        {
            logger.error("打开端口调用异常！"+ ex.getMessage());
        }

        try {
            System.out.println("请输入指纹。。。");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //3. 从设备中读取图像
        try {
            long getImageReturnCode = D5GetImage(uDeviceID);
            if (getImageReturnCode==0) {
                System.out.println("读取成功");
            }
        } catch (Exception e) {
            System.out.println("读取图像异常："+e);
        }
        //4. 检测某图像是否有指纹
        try {
            boolean getImageReturnCode = D5CheckFP();
            if (getImageReturnCode) {
                System.out.println("检测到指纹");
            }else {
                System.out.println("无指纹");
            }
        } catch (Exception e) {
            System.out.println("读取图像异常："+e);
        }
        //5. 指纹转成特征值
        ByteBuffer pImage= ByteBuffer.wrap(by);
        ByteBuffer pFeature = ByteBuffer.wrap(new byte[256]);
        System.out.println("处理前："+pFeature);
        long processReturnCode = D5ScannerS77.instanceDll.D5Process(pImage, pFeature);
        System.out.println("处理后："+pFeature);
        if (processReturnCode==0) {
            System.out.println("提取成功");
        }else if (processReturnCode == -1) {
            System.out.println("提取失败");
        }else {
            System.out.println("系统错误");
        }
        //6.  两个指纹特征值进行 1:1 比对
        //6.1 读取fea文件到字符串中

        //6.2 字符串转byte数组
        byte[] datas = fileToByteArray("E:\\我的食指.fea");//图片转为字节数组
        System.arraycopy(datas,0,pFeatureLib,0,datas.length);
        byte[] datas2 = fileToByteArray("E:\\1.fea");//图片转为字节数组
        System.arraycopy(datas2,0,pFeatureLib,datas.length,datas2.length);

        //多个匹配
        short uRotate = 180;
        short uLevel = 5;
//        ByteBuffer pFeature2 = ByteBuffer.wrap(pFeatureLib);
        long lFingernum = 10;
        try {
            long matchReturnCode = D5MatchN(pFeature, pFeatureLib,lFingernum,uRotate, uLevel);
            System.out.println("匹配返回值："+matchReturnCode);
            if (matchReturnCode >= 0) {
                System.out.println("OK");
            }
//            if (matchReturnCode==0) {
//                System.out.println("匹配成功");
//            }else if (matchReturnCode==-1) {
//                System.out.println("匹配失败");
//            }else if (matchReturnCode==-2) {
//                System.out.println("系统错误");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static long D5MatchN(ByteBuffer pFeature, byte[] pFeature2, long i, short uRotate, short uLevel) {
//        ByteBuffer pFeature3 = ByteBuffer.wrap(pFeature2);
        long l = D5ScannerS77.instanceDll.D5MatchN(pFeature, pFeature2,i, uRotate, uLevel);
        return l;
    }

    private static byte[] fileToByteArray(String pathname) {
        //创建源与目的地
        File src = new File(pathname);//获得文件的源头，从哪开始传入(源)
        byte[] dest = new byte[192 * 256];//最后在内存中形成的字节数组(目的地)
        //选择流
        InputStream is = null;//此流是文件到程序的输入流
        ByteArrayOutputStream baos= null;//此流是程序到新文件的输出流
        //操作(输入操作)
        try {
            is = new FileInputStream(src);//文件输入流
            baos = new ByteArrayOutputStream();//字节输出流，不需要指定文件，内存中存在
            byte[] flush = new byte[256];//设置缓冲，这样便于传输，大大提高传输效率
            int len = -1;//设置每次传输的个数,若没有缓冲的数据大，则返回剩下的数据，没有数据返回-1
            while((len = is.read(flush)) != -1) {
                baos.write(flush,0,len);//每次读取len长度数据后，将其写出
            }
            baos.flush();//刷新管道数据
            dest = baos.toByteArray();//最终获得的字节数组
            return dest;//返回baos在内存中所形成的字节数组
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //释放资源,文件需要关闭,字节数组流无需关闭
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

//    private static long D5Process() {
////        ByteBuffer pImage= ByteBuffer.wrap(by);
////        ByteBuffer pFeature = ByteBuffer.wrap(new byte[192 * 256]);
////        System.out.println("处理前："+pFeature);
////        long processReturnCode = D5ScannerS77.instanceDll.D5Process(pImage, pFeature);
////        System.out.println("处理后："+pFeature);
////        return processReturnCode;
//    }

    /**
     * 检测指纹
     * @return
     * @throws NativeException
     * @throws IllegalAccessException
     */
    private static boolean D5CheckFP() throws NativeException, IllegalAccessException{
        ByteBuffer name= ByteBuffer.wrap(by);
        boolean b = D5ScannerS77.instanceDll.D5CheckFP(name);
        return b;
    }

    private static long D5GetImage(short uDeviceID) throws NativeException, IllegalAccessException {

        JNative n = null;
        try
        {
            n = new JNative("D5ScannerS77.dll", "D5GetImage");
            n.setRetVal(Type.INT); // 指定返回参数的类型
            Pointer a = new Pointer(MemoryBlockFactory.createMemoryBlock(192 * 256));
            n.setParameter(0,uDeviceID);
            n.setParameter(1,a);
            n.invoke();
            by = a.getMemory();
            try
            {
                strTmp = new String(by);
                System.out.println(strTmp);
            }
            catch (Exception ex)
            {
            }

            a.dispose();
            return Integer.parseInt(n.getRetVal());
        }
        finally
        {

        }
    }

    private static long D5Match(ByteBuffer pFeature, ByteBuffer pFeature2, short uRotate, short uLevel) throws NativeException, IllegalAccessException {
        long l = D5ScannerS77.instanceDll.D5Match(pFeature, pFeature2, uRotate, uLevel);
        return l;
    }

    /**
     * 打开设备
     * @param uDeviceID
     * @return
     * @throws NativeException
     * @throws IllegalAccessException
     */
    private static long D5OpenDevice(short uDeviceID) throws NativeException, IllegalAccessException{
        JNative n = null;
        try
        {
            n = new JNative("D5ScannerS77.dll", "D5OpenDevice");
            n.setRetVal(Type.LONG); // 指定返回参数的类型
            n.setParameter(0, uDeviceID);
            n.invoke(); // 调用方法
            return Long.parseLong(n.getRetVal());
        }
        finally
        {

        }
    }
//
//    //蜂鸣
//    private static long D5Beep(short uDeviceID, short uMS)throws NativeException, IllegalAccessException, UnsupportedEncodingException {
//        JNative n = null;
//        try
//        {
//            n = new JNative("D5ScannerS77.dll", "D5Beep");
//            n.setRetVal(Type.LONG); // 指定返回参数的类型
//            n.setParameter(0, uDeviceID);
//            n.setParameter(1, uMS);
//            n.invoke(); // 调用方法
//            return Long.parseLong(n.getRetVal());
//        }
//
//        finally
//        {
//
//        }
//    }


    private static short D5EnumDevice(byte[] pDeviceName)throws NativeException, IllegalAccessException, UnsupportedEncodingException {
        JNative n = null;
        try
        {
            n = new JNative("D5ScannerS77.dll", "D5EnumDevice");
//            n.setRetVal(Type.INT); // 指定返回参数的类型
//            Pointer b = new Pointer(MemoryBlockFactory.createMemoryBlock(4*10));

            Pointer rva=new Pointer(MemoryBlockFactory.createMemoryBlock(16));
//            rva.setStringAt(0, pDeviceName[0]);
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

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
}
