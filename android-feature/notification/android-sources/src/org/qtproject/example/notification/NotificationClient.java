/****************************************************************************
 **
 ** Copyright (C) 2013 Digia Plc and/or its subsidiary(-ies).
 ** Contact: http://www.qt-project.org/legal
 **
 ** This file is part of the QtAndroidExtras module of the Qt Toolkit.
 **
 ** $QT_BEGIN_LICENSE:LGPL$
 ** Commercial License Usage
 ** Licensees holding valid commercial Qt licenses may use this file in
 ** accordance with the commercial license agreement provided with the
 ** Software or, alternatively, in accordance with the terms contained in
 ** a written agreement between you and Digia.  For licensing terms and
 ** conditions see http://qt.digia.com/licensing.  For further information
 ** use the contact form at http://qt.digia.com/contact-us.
 **
 ** GNU Lesser General Public License Usage
 ** Alternatively, this file may be used under the terms of the GNU Lesser
 ** General Public License version 2.1 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file.  Please review the following information to
 ** ensure the GNU Lesser General Public License version 2.1 requirements
 ** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
 **
 ** In addition, as a special exception, Digia gives you certain additional
 ** rights.  These rights are described in the Digia Qt LGPL Exception
 ** version 1.1, included in the file LGPL_EXCEPTION.txt in this package.
 **
 ** GNU General Public License Usage
 ** Alternatively, this file may be used under the terms of the GNU
 ** General Public License version 3.0 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.GPL included in the
 ** packaging of this file.  Please review the following information to
 ** ensure the GNU General Public License version 3.0 requirements will be
 ** met: http://www.gnu.org/copyleft/gpl.html.
 **
 **
 ** $QT_END_LICENSE$
 **
 ****************************************************************************/

package org.qtproject.example.notification;

import java.util.List;
import android.util.Log;
import org.json.*;

import android.app.Notification;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

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
import android.net.Uri;

import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

//import android.view.Window;
import android.view.WindowManager;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;
//import android.os.Process;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
//import org.qtproject.example.notification.VersionManager;

class MyHandler extends Handler {

        public MyHandler(Looper looper) {
                super(looper);
        }

        public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int value1 = msg.arg1;
                float f_value = value1;
                NotificationClient.__set_screen_brightness_value(f_value);
        }
}

public class NotificationClient extends
                org.qtproject.qt5.android.bindings.QtActivity {
        private static NotificationManager m_notificationManager;
        private static Notification.Builder m_builder;
        private static NotificationClient m_instance;

        // wifi module
        private static WifiManager m_wifi_manager;
        private static WifiInfo m_wifi_info;
        private static List<ScanResult> m_wifi_list;
        private static List<WifiConfiguration> m_wifi_configurations;

        // battary module
        private static int m_battary_level;
        private static int m_battary_scale;
        // screen setting

        // version manager
        // private static VersionManager m_version_manager;

        private static Handler m_handler;
        private static Looper looper;
        private static MyHandler _handler;
        // 工作线程
        /*
         * private class WorkThread extends Thread {
         *
         * @Override public void run() { try { Message msg = new Message(); msg.arg1
         * = 150; m_handler.sendMessage(msg); } catch (Exception e){ Log.e("123",
         * "exception", e); } } }
         */
        // private static WorkThread w1;

        private BroadcastReceiver __battary_info_receviver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                                m_battary_level = intent.getIntExtra("level", 0);
                                m_battary_scale = intent.getIntExtra("scanle", 100);
                        }
                }
        };

        public NotificationClient() {
                m_instance = this;
        }

        public static void notify(String s) {
                if (m_notificationManager == null) {
                        m_notificationManager = (NotificationManager) m_instance
                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                        m_builder = new Notification.Builder(m_instance);
                        m_builder.setSmallIcon(R.drawable.icon);
                        m_builder.setContentTitle("A message from Qt!");
                }

                m_builder.setContentText(s);
                m_notificationManager.notify(1, m_builder.build());
        }

        public static String wifi_scan() {
                String ret = null;

                if (!OpenWifi()) {
                        try {

                        } catch (Exception je) {
                        }
                        return ret;
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
                try {
                        // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                        Thread.currentThread();
                        Thread.sleep(1500);
                } catch (InterruptedException ie) {
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

                        Log.i("", ret);
                    } else {
                        Log.i("", "列表为空");
                        ret = null;
                    }

                return ret;
        }

        /**
         * 获取WIFI信息
         */
        public static String wifi_info() {
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
        public static String wifi_state() {
                String ret = null;
                JSONObject wifi_state = new JSONObject();

                try {
                        wifi_state.put("wifi_state", m_wifi_manager.getWifiState());
                } catch (Exception je) {
                }

                ret = wifi_state.toString();
                return ret;
        }

        @Override
        public void onCreate(Bundle saveInstanceState) {
                super.onCreate(saveInstanceState);

                registerReceiver(__battary_info_receviver, new IntentFilter(
                                Intent.ACTION_BATTERY_CHANGED));

                // fill screen
                // requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                // WindowManager.LayoutParams.FLAG_FULLSCREEN);

                //Log.i("threaedid", String.valueOf(Process.myTid()));

                /*
                 * float value = 250.0f; try {
                 * Settings.System.putInt(m_instance.getContentResolver(),
                 * Settings.System.SCREEN_BRIGHTNESS, (int)value);
                 * WindowManager.LayoutParams lp =
                 * m_instance.getWindow().getAttributes(); lp.screenBrightness = (value
                 * <= 0?1:value) / 255.0f; m_instance.getWindow().setAttributes(lp);
                 * Log.i("123", "ssssssssssssssssssssssss"); } catch (Exception e){ }
                 */

                // initialize wifi
                m_wifi_manager = (WifiManager) m_instance
                                .getSystemService(Context.WIFI_SERVICE);
                m_wifi_info = m_wifi_manager.getConnectionInfo();

                // version manager
                // m_version_manager = new VersionManager(this);

                // w1 = new WorkThread();
                looper = Looper.getMainLooper(); // 主线程的Looper对象
                // m_handler = new MyHandler(looper);
                // m_handler.removeMessages(0);
                _handler = new MyHandler(looper);
                // _handler.removeMessages(0);
        }

        public static String battery_state() {
                String ret = null;
                JSONObject battery_state = new JSONObject();

                JSONObject obj = new JSONObject();
                try {
                        obj.put("level", m_battary_level);
                        obj.put("scale", m_battary_scale);

                        battery_state.put("battery_state", obj);
                } catch (Exception je) {
                }

                ret = battery_state.toString();
                return ret;
        }

        public static String screen_brightness() {
                String ret = null;
                int value = -1;
                int mode = -1;

                ContentResolver cr = m_instance.getContentResolver();

                try {
                        value = Settings.System.getInt(cr,
                                        Settings.System.SCREEN_BRIGHTNESS);
                        mode = Settings.System.getInt(m_instance.getContentResolver(),
                                        Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (SettingNotFoundException e) {
                }

                JSONObject screen_brightness = new JSONObject();
                JSONObject obj = new JSONObject();
                try {
                        obj.put("value", value);
                        obj.put("max_value", 255);
                        obj.put("mode",
                                        (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) ? "AUTOMATIC"
                                                        : "MANUAL");
                        screen_brightness.put("screen_brightness", obj);
                } catch (Exception je) {
                }

                ret = screen_brightness.toString();
                return ret;
        }

        /*
         * private static Handler m_handler = new Handler() {
         *
         * @Override public void handleMessage(Message msg) { Log.i("threaedid",
         * "handler:" + String.valueOf(Process.myTid()) ); super.handleMessage(msg);
         * Log.i(" ", "handleMessage"); int value1 = msg.arg1; float f_value =
         * value1; __set_screen_brightness_value(f_value);
         *
         * } };
         */
        public static void set_screen_brightness_value(int value) {
            int screenMode = -1;
            try {
                    screenMode = Settings.System.getInt(
                                    m_instance.getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (SettingNotFoundException e) {

            }
            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    set_screen_brightness_mode(0);
            }

            try {
                    Uri uri = android.provider.Settings.System
                                    .getUriFor("screen_brightness");
                    Settings.System.putInt(m_instance.getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, (int) value);
                    m_instance.getContentResolver().notifyChange(uri, null);
            } catch (Exception e) {

                    Log.e("123", "exception", e);
            }
        }

        /*public static void set_screen_brightness_value(int value) {

                Looper looper1 = Looper.getMainLooper();
                // Log.i(" ", "set_screen_brightness_value./....");
                // Log.i("threaedid", "set:" + String.valueOf(Process.myTid()) );
                // w1.start();
                // try {
                // Message msg = new Message();
                // msg.arg1 = 150;
                // m_handler.sendMessage(msg);
                // } catch (Exception e){
                // Log.e("123", "exception", e);
                // }

                // 这里以主线程的Looper对象创建了handler，
                // 所以，这个handler发送的Message会被传递给主线程的MessageQueue。

                //
                // 构建Message对象
                // 第一个参数：是自己指定的message代号，方便在handler选择性地接收
                // 第二三个参数没有什么意义
                // 第四个参数需要封装的对象
                // Message msg = new Message();
                //
                // msg.arg1 = value;

                // _handler.sendMessage(msg); //发送消息

                new Thread() {
                        public void run() {
                                m_instance.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                __set_screen_brightness_value(150.0f);
                                        }
                                });
                        }
                }.start();
        }
        */
        /**
         * 设置当前屏幕亮度值 0--255，并使之生效, 注意：该函数会强制将当前“亮度模式”设置为手动
         */
        public static void __set_screen_brightness_value(float value) {

                Log.i(" ", "___set_screen_brightness_valuessssssssssssssssssssssss");
                int screenMode = -1;
                try {
                        screenMode = Settings.System.getInt(
                                        m_instance.getContentResolver(),
                                        Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (SettingNotFoundException e) {

                }
                if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        set_screen_brightness_mode(0);
                }

                try {
                        WindowManager.LayoutParams lp = m_instance.getWindow()
                                        .getAttributes();
                        lp.screenBrightness = (value <= 0 ? 1 : value) / 255.0f;
                        m_instance.getWindow().setAttributes(lp);
                        Log.i("123", "ssssssssssssssssssssssss");

                        Uri uri = android.provider.Settings.System
                                        .getUriFor("screen_brightness");
                        Settings.System.putInt(m_instance.getContentResolver(),
                                        Settings.System.SCREEN_BRIGHTNESS, (int) value);
                        m_instance.getContentResolver().notifyChange(uri, null);
                } catch (Exception e) {

                        Log.e("123", "exception", e);
                }

        }

        /**
         * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
         * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
         */
        public static void set_screen_brightness_mode(int mode) {

                // Settings.System.putInt(m_instance.getContentResolver(),
                // Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
                Settings.System.putInt(m_instance.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
        }

        // 提供一个外部接口，传入要连接的无线网
        public static String connect_wifi(String SSID, String Password, int Type) {
                String ret = null;
                int net_id = 0;
                boolean b_ret = false;
                JSONObject connect_wifi = new JSONObject();
                WifiConfiguration _wifi_config = null;
                WifiConfiguration _temp_config = null;

                if (!OpenWifi()) {
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

                try {
                        connect_wifi.put("connect_wifi", b_ret);
                } catch (Exception je) {
                }
                return connect_wifi.toString();
        }

        /*
         * private boolean OpenWifi() { boolean bRet = true; if
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

        private static void disconnect_wifi() {
                WifiInfo curWifi = m_wifi_manager.getConnectionInfo();
                if (curWifi == null) {
                        return;
                }
                int netId = curWifi.getNetworkId();
                m_wifi_manager.disableNetwork(netId);
                m_wifi_manager.disconnect();
        }

        // 打开wifi功能
        private static boolean OpenWifi() {
                boolean bRet = true;
                if (!m_wifi_manager.isWifiEnabled()) {
                        bRet = m_wifi_manager.setWifiEnabled(true);
                }
                return bRet;
        }

        private static boolean close_wifi() {
                boolean bRet = true;
                if (!m_wifi_manager.isWifiEnabled()) {
                        bRet = m_wifi_manager.setWifiEnabled(false);
                }
                return bRet;
        }

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
                        Log.i("", "llllllllllllllll\n");

                } else {
                        return null;
                }
                return config;
        }

        public static void install (String file_path){
               //String apkName = Environment.getExternalStorageDirectory()
               //						.toString() + File.separator + "test_vivi.apk";
               haveRoot("pm install -r " + file_path);
       }

       public static void install_apk(String absPath){
                   String chmodCmd = "chmod 666 " + absPath;
                   try {
                             Runtime.getRuntime().exec(chmodCmd);
                   } catch (Exception e) {
                   }
                  //__log("the abs Path is " + absPath);
                  Intent intent = new Intent(Intent.ACTION_VIEW);
                  intent.setDataAndType(Uri.fromFile(new File(absPath)),
                                   "application/vnd.android.package-archive");
                  m_instance.startActivity(intent);
                  //return false;
              }


        private static boolean haveRoot(String cmd) {
                int i = execRootCmdSilent(cmd);
                if (i != -1) {
                        return true;
                }
                return false;
        }

        /**
         * 执行静默安装
         *
         * @param paramString
         * @return
         */
        private static int execRootCmdSilent(String paramString) {
                int result = -1;
                try {
                        Process localProcess = Runtime.getRuntime().exec("su");
                        OutputStream os = localProcess.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeBytes(paramString + "\n");
                        dos.flush();
                        dos.writeBytes("exit\n");
                        dos.flush();
                        localProcess.waitFor();
                        result = localProcess.exitValue();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return result;
        }
}
