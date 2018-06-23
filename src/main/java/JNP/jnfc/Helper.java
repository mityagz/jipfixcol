package JNP.jnfc;

import java.nio.ByteOrder;

/**
 * Created by mitya on 10/14/17.
 */
public class Helper {
    public static int TemplateSets = 2;
    public static int OptionsTemplateSets = 3;
    public static int DataSets = 255;
    public static int IPFIXHeaderLength = 16;
    public static int IPFIXVersion = 10;

    public static int getInt1(byte [] bytes) {
        int val = (bytes[0] & 0xff);
        return val;
    }

    public static int getInt2(byte [] bytes) {
        int val = ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
        return val;
    }

    public static int getInt4(byte [] bytes) {
        int val = ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
        return val;
    }

    public static int getInt8(byte [] bytes) {
        int val = ((bytes[0] & 0xff) << 56) | ((bytes[1] & 0xff) << 48) | ((bytes[2] & 0xff) << 40) | ((bytes[3] & 0xff) << 32) | ((bytes[4] & 0xff) << 24) | ((bytes[5] & 0xff) << 16) | ((bytes[6] & 0xff) << 8) | (bytes[7] & 0xff);
        return val;
    }

    public static int getInt(byte [] bytes) {
        if(bytes.length == 2)
            return java.nio.ByteBuffer.wrap(bytes).getShort();
        else if(bytes.length == 4)
            return java.nio.ByteBuffer.wrap(bytes).getInt();
        else return (int)java.nio.ByteBuffer.wrap(bytes).getLong();
    }

    public static String byteToIp(byte [] address) {
        String addressStr = "";
        for (int i = 0; i < 4; ++i) {
            int t = 0xFF & address[i];
            addressStr += "." + t;
        }
        addressStr = addressStr.substring(1);
        return addressStr;
    }

    public static int getIntV(byte [] bytes) {
        if(bytes.length == 1) return getInt1(bytes);
        if(bytes.length == 2) return getInt2(bytes);
        if(bytes.length == 4) return getInt4(bytes);
        if(bytes.length == 8) return getInt8(bytes);
        return 0;
    }
}
