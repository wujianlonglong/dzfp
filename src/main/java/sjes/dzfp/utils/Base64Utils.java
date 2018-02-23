package sjes.dzfp.utils;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Base64Utils {

    /**
     * 使用jdk的base64 加密字符串
     */
    public static String jdkBase64Encoder(String str) {
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(str.getBytes());
        return encode;
    }

    //加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }


    // 解密
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 使用commons-codec的base64 加密字符串
     */
    public static String CCBase64Encoder(String str) {

        return new String(Base64.encodeBase64(str.getBytes()));
    }


    /**
     * 解码base64图片
     *
     * @param str
     * @throws IOException
     */
    public static void DecodeToFile(String str) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        FileOutputStream write = new FileOutputStream(new File("C:/Users/wujianlong/Desktop/ttt.jpg"));
        byte[] decoderBytes = decoder.decodeBuffer(str);
        write.write(decoderBytes);
        write.close();
    }


}
