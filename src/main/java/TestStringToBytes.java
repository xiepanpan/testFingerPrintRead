import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author: xiepanpan
 * @Date: 2020/5/12
 * @Description:
 */
public class TestStringToBytes {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "13638b9de15f96e841560fd481498f152147a7d8814693e9a143a22b61438f54012a8c84a129aa17c1252ec16113a0abc1692d9d62671a52a24b22d5823c89db623bad82223ba517e22023d6820000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        System.out.println("转换后的字节数组：" + Arrays.toString(toBytes(str)));
        System.out.println(new String(toBytes(str), "utf-8"));
    }

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
