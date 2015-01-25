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

package com.entwickeln.enmenu;

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

public class MainActivity extends
                org.qtproject.qt5.android.bindings.QtActivity {
        private static NotificationManager m_notificationManager;
        private static Notification.Builder m_builder;
        private static NotificationClient m_instance;

        private static WifiHandler wifi_handler;
        private static PackageHandler m_package_handler;
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
               return wifi_handler.wifi_scan();
        }

        /**
         * 获取WIFI信息
         */
        public static String wifi_info() {
               return wifi_handler.wifi_info();
        }

        /**
         * 获取WIFI状态
         */
        public static String wifi_state() {
                return wifi_handler.wifi_state();
        }
        // 提供一个外部接口，传入要连接的无线网
        public static String connect_wifi(String SSID, String Password, int Type) {
            Log.i(" ", "connct wifi....\n");
                return wifi_handler.connect_wifi(SSID, Password, Type);
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
                //m_wifi_manager = (WifiManager) m_instance
                 //               .getSystemService(Context.WIFI_SERVICE);
                //m_wifi_info = m_wifi_manager.getConnectionInfo();


                wifi_handler = new WifiHandler(m_instance);
                m_package_handler = new PackageHandler(m_instance);
                looper = Looper.getMainLooper(); // 主线程的Looper对象
                // m_handler = new MyHandler(looper);
                // m_handler.removeMessages(0);
                _handler = new MyHandler(looper);
                // _handler.removeMessages(0);

                //Intent i = new Intent();
                //i.setClass(context, DaemonService.class);
                // 启动service
                // 多次调用startService并不会启动多个service 而是会多次调用onStart
                //context.startService(i);
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


        public static void install (String file_path){
               m_package_handler.install(file_path);
       }

       public static void install_apk(String absPath){
                 m_package_handler.install_apk(absPath);
       }
}
