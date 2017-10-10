package com.kys26.webthings.model;

/**
 * @author 李赛鹏
 *         Created by kys_9 on 2017/3/11.
 */

public class WifiBean {
    private String SSID;
    private String BSSID;
    private int WifiLevel;
    private String capabilities;
    public WifiBean(String BSSID, String SSID, String capabilities, int WifiLevel) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.WifiLevel = WifiLevel;
        this.capabilities=capabilities;
//        this.isConnecting = isConnecting;
    }

    public String GetBSSID() {
        return BSSID;
    }

    public void SetBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String GetSSID() {
        return SSID;
    }

    public void SetSSID(String SSID) {
        this.SSID = SSID;
    }

    public int GetWifiLevel() {
        return WifiLevel;
    }

    public void SetWifiLevel(int WifiLevel) {
        this.WifiLevel = WifiLevel;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
}
