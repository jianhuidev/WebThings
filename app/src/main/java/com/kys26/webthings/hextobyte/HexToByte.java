package com.kys26.webthings.hextobyte;

/**
 * Created by Administrator on 2015/9/11.
 */
public class HexToByte {
    /**
     * 十六进制字符串转Byte数组
     */
    private    byte[] HexStringToByte(String hexString)
    {
        if (hexString == null || hexString.equals(""))
        {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] returnByte = new byte[length];
        for (int i = 0; i < length; i++)
        {
            int pos = i * 2;
            returnByte[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return returnByte;
    }

    private byte charToByte(char c)
    {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    /**
     * bytez数组转为十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytes2hex03(byte[] bytes) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }
}
