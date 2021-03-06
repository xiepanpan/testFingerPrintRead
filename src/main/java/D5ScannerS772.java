import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

import java.nio.ByteBuffer;

/**
 * @author: xiepanpan
 * @Date: 2020/5/11
 * @Description:
 */
public interface D5ScannerS772 extends Library {
    D5ScannerS772 instanceDll  = (D5ScannerS772) Native.loadLibrary("D5ScannerS77", D5ScannerS772.class);
//     short WINAPI D5EnumDevice (char pDeviceName[DEV_MAX_NUM][128])
    public short D5EnumDevice(byte[] pDeviceName);
    //打开端口
    public Long D5OpenDevice(short uDeviceID);
//    long D5GetImage ( short wDeviceID, char *pImage);
    //检测是否有指纹
    boolean D5CheckFP(ByteBuffer pImage);

    long  D5GetImage(short wDeviceID, byte[] pImage);

    /**
     * 指纹转特征值
     * @param pImage
     * @param pFeature
     * @return
     */
    NativeLong D5Process(ByteBuffer pImage, ByteBuffer pFeature);

    long D5Match(ByteBuffer pFeature1, ByteBuffer pFeature2,
                 short uRotate, short uLevel);
    NativeLong D5MatchN(ByteBuffer pFeatureIn, ByteBuffer pDeviceName,
                        int lFingernum, short uRotate, short uLevel);

    long D5Beep (short uDeviceID, short uMS);
}
