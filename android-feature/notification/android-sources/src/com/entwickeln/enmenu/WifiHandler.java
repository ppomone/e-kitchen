 /*
  * Copyright (C) 2014-2015
  * Contact: http://www.en-wickeln.com/legal
  *
  */

package com.entwickeln.enmenu;

import java.util.List;
import android.util.Log;
import org.json.*;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;

public class WifiHandler {
    private static final String TAG = "EnmenuWifiHandler";

    private static WifiManager m_wifi_manager;
    private static WifiInfo m_wifi_info;
    private static List<ScanResult> m_wifi_list;
    private static List<WifiConfiguration> m_wifi_configurations;

    public WifiHandler(Context ctx){
        m_wifi_manager = (WifiManager) ctx
                        .getSystemService(Context.WIFI_SERVICE);
        m_wifi_info = m_wifi_manager.getConnectionInfo();
    }

    public String wifiScan() {
            String ret = null;
            boolean is_ready_open = true;

            Log.i (TAG, "try to wifi_scan...\n");
            if (!openWifi()) {
                    try {

                    } catch (Exception je) {
                    }
                        Log.i(TAG, "wifi switch can not open!\n");
                    return ret;
            }
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (m_wifi_manager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                is_ready_open = false;
                    try {
                            // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                            Thread.currentThread();
                            Thread.sleep(100);
                    } catch (InterruptedException ie) {
                    }
            }
            if (is_ready_open == false){
                is_ready_open = true;
               try {
                        // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                        Thread.currentThread();
                        Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }

            }

            m_wifi_manager.startScan();
            // 得到扫描结果
            m_wifi_list = m_wifi_manager.getScanResults();
            m_wifi_configurations = m_wifi_manager.getConfiguredNetworks();

            if (m_wifi_list != null) {
                    JSONObject _wifi_scan = new JSONObject();

                    JSONArray _wifi_list_array = new JSONArray();
                    JSONArray _wifi_cfg_array = new JSONArray();
                    try {
                            // wifi list
                            for (int i = 0; i < m_wifi_list.size(); i++) {
                                    ScanResult sr = m_wifi_list.get(i);

                                    JSONObject obj = new JSONObject();
                                    obj.put("BSSID", sr.BSSID);
                                    obj.put("SSID", sr.SSID);
                                    obj.put("capabilities", sr.capabilities);
                                    obj.put("frequency", sr.frequency);
                                    obj.put("level", sr.level);
                                    // obj.put(sr.toString());
                                    _wifi_list_array.put(obj);
                            }
                            _wifi_scan.put("wifi_list", _wifi_list_array);
                            // _wifi_scan.put("wifi_scan", _wifi_list_array);
                            // wifi configurations
                            for (int i = 0; i < m_wifi_configurations.size(); i++) {
                                    WifiConfiguration wc = m_wifi_configurations.get(i);
                                    JSONObject obj = new JSONObject();
                                    obj.put("SSID", wc.SSID);
                                    obj.put("allowedProtocols", wc.allowedProtocols);
                                    obj.put("allowedAuthAlgorithms", wc.allowedAuthAlgorithms);
                                    obj.put("allowedKeyManagement", wc.allowedKeyManagement);
                                    obj.put("allowedPairwiseCiphers", wc.allowedPairwiseCiphers);
                                    obj.put("networkId", wc.networkId);
                                    // obj.put(wc.toString());
                                    _wifi_cfg_array.put(obj);
                            }
                            // _wifi_scan.put("wifi_configurations", _wifi_cfg_array);

                    } catch (Exception je) {
                    }
                    ret = _wifi_scan.toString();

                    Log.i(TAG, ret);
                } else {
                    Log.i(TAG, "列表为空");
                    ret = null;
                }

            return ret;
    }

    /**
     * 获取WIFI连接信息
     */
    public String getConnectionInfo() {
            String ret = null;
            m_wifi_info = m_wifi_manager.getConnectionInfo();

            JSONObject wifi_info = new JSONObject();
            try {
                    if (m_wifi_info != null) {
                            wifi_info.put("wifi_info", m_wifi_info.toString());
                    } else {
                            wifi_info.put("wifi_info", "null");
                    }
            } catch (Exception je) {
            }

            ret = wifi_info.toString();
            return ret;
    }

    /**
     * 获取WIFI状态
     */
    public String getWifiState() {
            String ret = null;
            JSONObject wifi_state = new JSONObject();

            try {
                    wifi_state.put("wifi_state", m_wifi_manager.getWifiState());
            } catch (Exception je) {
            }

            ret = wifi_state.toString();
            return ret;
    }

    public void removeNetwork(String SSID) {
            //String ret = null;
            WifiConfiguration _temp_config = null;
            _temp_config = IsExsits(SSID);
            if (_temp_config != null) {
                    m_wifi_manager.removeNetwork(_temp_config.networkId);
            }
            //return ret;
    }

    public void disconnect() {
            WifiInfo curWifi = m_wifi_manager.getConnectionInfo();
            if (curWifi == null) {
                    return;
            }
            int netId = curWifi.getNetworkId();
            m_wifi_manager.disableNetwork(netId);
            m_wifi_manager.disconnect();
    }

    // 提供一个外部接口，传入要连接的无线网
    public String connectWifi(String SSID, String Password, int Type) {
            String ret = null;
            int net_id = 0;
            boolean b_ret = false;
            JSONObject connect_wifi = new JSONObject();
            WifiConfiguration _wifi_config = null;
            WifiConfiguration _temp_config = null;

            if (!openWifi()) {
                    try {
                            connect_wifi.put("connect_wifi", b_ret);
                    } catch (Exception je) {
                    }
                    return connect_wifi.toString();
            }
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (m_wifi_manager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                            // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                            Thread.currentThread();
                            Thread.sleep(100);
                    } catch (InterruptedException ie) {
                    }
            }

            _wifi_config = CreateWifiInfo(SSID, Password, Type);
            if (_wifi_config == null) {
                    try {
                            connect_wifi.put("connect_wifi", b_ret);
                    } catch (Exception je) {
                    }
                    return connect_wifi.toString();
            }
            _temp_config = IsExsits(SSID);
            if (_temp_config != null) {
                    m_wifi_manager.removeNetwork(_temp_config.networkId);
            }
            net_id = m_wifi_manager.addNetwork(_wifi_config);
            b_ret = m_wifi_manager.enableNetwork(net_id, true);

            if (true == b_ret) {
                b_ret = m_wifi_manager.saveConfiguration ();
            }
            try {
                    connect_wifi.put("connect_wifi", b_ret);
            } catch (Exception je) {
            }
            return connect_wifi.toString();
    }

    /*
     * private boolean openWifi() { boolean bRet = true; if
     * (!m_wifi_manager.isWifiEnabled()){ bRet =
     * m_wifi_manager.setWifiEnabled(true); } return bRet; }
     */

    // 查看以前是否也配置过这个网络
    private static WifiConfiguration IsExsits(String SSID) {
            List<WifiConfiguration> existingConfigs = m_wifi_manager
                            .getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                    if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                            return existingConfig;
                    }
            }
            return null;
    }

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    // public enum WifiCipherType {
    // WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    // }


    public boolean isWifiEnabled() {
        return m_wifi_manager.isWifiEnabled();
    }

    // 打开wifi功能
    public boolean setWifiEnabled(boolean isEnabled) {
            boolean bRet = true;
            bRet = m_wifi_manager.setWifiEnabled(isEnabled);
            return bRet;
    }

    // 用于在wifi关闭的情况下，打开wifi
    private boolean openWifi(){
        boolean bRet = true;
        if (!m_wifi_manager.isWifiEnabled()){
             bRet = m_wifi_manager.setWifiEnabled(true);
        }
        return bRet;
    }


    /*private static boolean close_wifi() {
            boolean bRet = true;
            if (!m_wifi_manager.isWifiEnabled()) {
                    bRet = m_wifi_manager.setWifiEnabled(false);
            }
            return bRet;
    }
    */

    private static WifiConfiguration CreateWifiInfo(String SSID,
                    String Password, int Type) {
            WifiConfiguration config = new WifiConfiguration();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();

            config.SSID = "\"" + SSID + "\"";
            if (Type == 0) {
                    // config.wepKeys[0] = "";
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    // config.wepTxKeyIndex = 0;
            } else if (Type == 1) {
                    //config.preSharedKey = "\"" + Password + "\"";
                    config.hiddenSSID = true;
                    config.allowedAuthAlgorithms
                                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    config.allowedGroupCiphers
                                    .set(WifiConfiguration.GroupCipher.WEP104);
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    config.wepKeys[0] = Password;
                    config.wepTxKeyIndex = 0;
            } else if (Type == 2) {
                    config.preSharedKey = "\"" + Password + "\"";
                    // config.hiddenSSID = true;
                    // config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                    /*
                     * config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                     * ;
                     * config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP
                     * );
                     *
                     * config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                     * ;
                     * config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher
                     * .TKIP);
                     * config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher
                     * .CCMP);
                     *
                     * //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); //
                     * For WPA
                     * config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); //
                     * For WPA2
                     *
                     * config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
                     * ;
                     *
                     * config.status = WifiConfiguration.Status.ENABLED;
                     */

                    config.status = WifiConfiguration.Status.ENABLED;
                    config.allowedAuthAlgorithms
                                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    config.allowedPairwiseCiphers
                                    .set(WifiConfiguration.PairwiseCipher.TKIP);
                    config.allowedPairwiseCiphers
                                    .set(WifiConfiguration.PairwiseCipher.CCMP);
                    config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    Log.i(TAG, "llllllllllllllll\n");

            } else {
                    return null;
            }
            return config;
    }
}

