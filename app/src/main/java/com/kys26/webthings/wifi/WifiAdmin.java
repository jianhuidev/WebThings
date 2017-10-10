package com.kys26.webthings.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

//download by http://www.codefans.net

public class WifiAdmin {
    private final static String TAG = "WifiAdmin";
    private StringBuffer mStringBuffer = new StringBuffer();
    private List<ScanResult> listResult;
    private ScanResult mScanResult;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiLock mWifiLock;

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_WPA2, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    /**
     * 构造方法
     */
    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 打开Wifi网卡
     */
    public void openNetCard() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi网卡
     */
    public void closeNetCard() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前Wifi网卡状态
     */
    public int checkNetCardState() {
        if (mWifiManager.getWifiState() == 0) {
            Log.i(TAG, "网卡正在关闭");
        } else if (mWifiManager.getWifiState() == 1) {
            Log.i(TAG, "网卡已经关闭");
        } else if (mWifiManager.getWifiState() == 2) {
            Log.i(TAG, "网卡正在打开");
        } else if (mWifiManager.getWifiState() == 3) {
            Log.i(TAG, "网卡已经打开");
        } else {
            Log.i(TAG, "---_---晕......没有获取到状态---_---");
        }
        return mWifiManager.getWifiState();
    }

    /**
     * 扫描周边网络
     */
    public void scan() {
        mWifiManager.startScan();
        listResult = mWifiManager.getScanResults();
        if (listResult != null) {
            Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
        } else {
            Log.i(TAG, "当前区域没有无线网络");
        }
    }

    /**
     * 得到扫描结果
     */
    public String getScanResult() {
        // 每次点击扫描之前清空上一次的扫描结果
        if (mStringBuffer != null) {
            mStringBuffer = new StringBuffer();
        }
        // 开始扫描网络
        scan();
        listResult = mWifiManager.getScanResults();
        if (listResult != null) {
            for (int i = 0; i < listResult.size(); i++) {
                mScanResult = listResult.get(i);
                mStringBuffer = mStringBuffer.append(mScanResult.SSID + "/");
            }
        }
        Log.i(TAG, mStringBuffer.toString());
        return mStringBuffer.toString();
    }

    /**
     * 连接指定网络
     */
    public void connect() {
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 断开当前连接的网络
     */
    public void disconnectWifi() {
        int netId = getNetworkId();
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
        mWifiInfo = null;
    }

    /**
     * 检查当前网络状态
     *
     * @return String
     */
    public void checkNetWorkState() {
        if (mWifiInfo != null) {
            Log.i(TAG, "网络正常工作");
        } else {
            Log.i(TAG, "网络已断开");
        }
    }

    /**
     * 得到连接的ID
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到IP地址
     */
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index >= mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public int addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
        mWifiManager.enableNetwork(wcgID, true);
        return wcgID;
    }

    // 打开wifi功能
    public boolean OpenWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    // 提供一个外部接口，传入要连接的无线网
    public int Connect(String SSID, String Password, WifiCipherType Type) {
        if (!this.OpenWifi()) {
            return 0;
        }
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        WifiConfiguration wifiConfig = this
                .createWifiConfig(SSID, Password, Type);
        if (wifiConfig == null) {
            return 0;
        }
        mWifiManager.disconnect();
        int netID = mWifiManager.addNetwork(wifiConfig);
//        if (netID == -1) {
        SSID = "\"" + SSID + "\"";
        for (WifiConfiguration c0 : mWifiManager.getConfiguredNetworks()) {
            if (c0.SSID.equals(SSID)) {
                mWifiManager.enableNetwork(c0.networkId, true);
            }
        }
//        } else {
//            mWifiManager.enableNetwork(netID, true);
//            mWifiManager.saveConfiguration();
//            mWifiManager.reconnect();
//        }
//            if (c0.networkId == netID&&c0.SSID.equals(SSID)) {
//                boolean status = mWifiManager.enableNetwork(netID, true);
//                Log.e(TAG, "status:" + status);
//                mWifiManager.saveConfiguration();
//                mWifiManager.reconnect();
//            } else {
//                mWifiManager.enableNetwork(c0.networkId, false);
//            }
//    }
        return 0;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
//
//    private WifiConfiguration CreateWifiInfo(String SSID, String Password,
//                                             WifiCipherType Type) {
//        WifiConfiguration wc = new WifiConfiguration();
//        wc.allowedAuthAlgorithms.clear();
//        wc.allowedGroupCiphers.clear();
//        wc.allowedKeyManagement.clear();
//        wc.allowedPairwiseCiphers.clear();
//        wc.allowedProtocols.clear();
//        wc.SSID = "\"" + SSID + "\"";
//        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
//            wc.wepKeys[0] = "";
//            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            wc.wepTxKeyIndex = 0;
//        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
//            wc.wepKeys[0] = "\"" + Password + "\"";
//            wc.hiddenSSID = true;
//            System.out.println("111111111111111111111111");
//            wc.allowedAuthAlgorithms
//                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
//            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            wc.wepTxKeyIndex = 0;
//            // System.out.println(wc.preSharedKey);
//            System.out.println(wc);
//        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
//            wc.preSharedKey = "\"" + Password + "\"";
//            wc.hiddenSSID = true;
//            // 用来判断加密方法。
//            // 可选参数：LEAP只用于leap,
//            // OPEN 被wpa/wpa2需要,
//            // SHARED需要一个静态的wep key
//            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            // 用来判断加密方法。可选参数：CCMP,TKIP,WEP104,WEP40
//            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            // WifiConfiguration.KeyMgmt 键管理机制（keymanagerment），使用KeyMgmt 进行。
//            // 可选参数IEEE8021X,NONE,WPA_EAP,WPA_PSK
//            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            // WifiConfiguration.PairwiseCipher 设置加密方式。
//            // 可选参数 CCMP,NONE,TKIP
//            wc.allowedPairwiseCiphers
//                    .set(WifiConfiguration.PairwiseCipher.TKIP);
//            wc.allowedPairwiseCiphers
//                    .set(WifiConfiguration.PairwiseCipher.CCMP);
//            // WifiConfiguration.Protocol 设置一种协议进行加密。
//            // 可选参数 RSN,WPA,
//            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // for WPA
//            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // for WPA2
//            // wifiConfiguration.Status 获取当前网络的状态。
//        } else {
//            return null;
//        }
//        return wc;
//    }

    /**
     * 通过反射出不同版本的connect方法来连接Wifi
     *
     * @param netId
     * @return
     * @author jiangping.li
     * @since MT 1.0
     */
    private Method connectWifiByReflectMethod(int netId) {
        Method connectMethod = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.i(TAG, "connectWifiByReflectMethod road 1");
            // 反射方法： connect(int, listener) , 4.2 <= phone‘s android version
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
            // 暂时不处理4.1的情况 , 4.1 == phone‘s android version
            Log.i(TAG, "connectWifiByReflectMethod road 2");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Log.i(TAG, "connectWifiByReflectMethod road 3");
            // 反射方法：connectNetwork(int networkId) ,
            // 4.0 <= phone‘s android version < 4.1
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connectNetwork".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else {
            // < android 4.0
            return null;
        }
        return connectMethod;
    }

    /**
     * 得到SSID
     */
    public String getSSID() {
        return (mWifiInfo == null) ? null : mWifiInfo.getSSID();
    }

    private WifiConfiguration createWifiConfig(String ssid, String password, WifiCipherType Type) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";

        //如果之前有类似的配置
        WifiConfiguration tempConfig = IsExsits(ssid);
        if (tempConfig != null) {
            //则清除旧有配置
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        //不需要密码的场景
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
}
