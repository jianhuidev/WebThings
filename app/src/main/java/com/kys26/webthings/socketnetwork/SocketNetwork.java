package com.kys26.webthings.socketnetwork;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kys26.webthings.httpconstant.Path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketNetwork  {

    /**socket TAG*/
    private String TAG = "socket thread";
    /**连接超时值设定*/
    private int timeout = 10000;
    /**socket网络连接*/
    public Socket client = null;
    /**输出流*/
    PrintWriter out;
    /**输入流*/
    BufferedReader in;
    public boolean isRun = false;
    /**子线程中的处理消息交给handler*/
    Handler inHandler;
    /**实例化调用对象*/
    Context ctx;
    /**LOG TAG*/
    private String TAG1 = "===Send===";
    /**设置一个计时器最大值*/
    private int timeMax=2000;
    /**设置一个计时标识*/
    private int time=0;
    /**发送内容*/
    private String dataString;

    public SocketNetwork(Handler handlerin, String data,Context context) {
        inHandler = handlerin;
        ctx = context;
        dataString=data;
        Log.i(TAG, "创建线程socket");
    }

    /**
     * @function:socket网络连接发送网关配置信息
     * 连接socket服务器
     * @author kys_26使用者：徐建强 2015-9-1
     * @return null
     */
    public void conn() {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(5000);//阻断2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //开始创建连接
                try {
                    client = new Socket(Path.gatewagHost, Path.gatewayPort);
                    client.setSoTimeout(timeout);// 设置阻塞时间
                    in = new BufferedReader(new InputStreamReader(
                            client.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            client.getOutputStream())), true);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    conn();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //如果有可用连接，进入循环
                if (client != null) {
                    //发送数据
                    Log.e("12345socket","  "+6);
                    out.println(dataString);
                    out.flush();
                    //进入一个循环实时等待数据接收
                    isRun=true;
               while (isRun==true) {
                      try {
                        //计时增加
                        time++;
                            //读取网关返回确认码
                            InputStream inputStream = client.getInputStream();
                            if (inputStream != null) {
                                //创建字节流
                                byte buffer[] = new byte[125];
                                //接收字节流
                                inputStream.read(buffer);
                                Log.i("获得的数据:",bytes2hex03(buffer));
                                // 结果返回给list的UI处理
                                Message msg = inHandler.obtainMessage();
                                msg.what = 1;
                                inHandler.sendMessage(msg);
                                //接收完成，结束循环，释放内存
                                break;
                            } else if(time>timeMax){
                                Log.i("List循环接收部分:", "inputStream流为空");
                                Message msg = inHandler.obtainMessage();
                                msg.what = 0;
                                inHandler.sendMessage(msg);
                                //接收数据失败，结束循环，释放内存
                                break;
                            }
                        //线程睡眠1500毫秒
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Log.i(TAG, "数据接收错误" + e.getMessage());
                        e.printStackTrace();
                          //异常时关闭socket以及输入流输出流
                        close();
                    }
                }}
                //如果没有可用连接，Handler返回给调用类无服务连接
                 else {
                    Log.i(TAG, "<<<<<没有可用连接>>>>>");
                    //没有可用连接
                    Message msg = inHandler.obtainMessage();
                    msg.what = 2;
                    inHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }


    /**
     * @function:关闭连接
     * @author kys_26使用者：徐建强 2015-9-1
     * @return null
     */
    public void close() {
        try {
            if (client != null) {
                isRun=false;
                Log.i(TAG, "close in");
                in.close();
                Log.i(TAG, "close out");
                out.close();
                Log.i(TAG, "close client");
                client.close();
            }
        } catch (Exception e) {
            Log.i(TAG, "close err");
            e.printStackTrace();
        }

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

}
