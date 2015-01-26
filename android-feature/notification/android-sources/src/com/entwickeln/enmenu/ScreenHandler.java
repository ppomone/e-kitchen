 /*
  * Copyright (C) 2014-2015
  * Contact: http://www.en-wickeln.com/legal
  *
  */

package com.entwickeln.enmenu;

import android.util.Log;
import org.json.*;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.content.ContentResolver;
import android.net.Uri;

import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.app.Activity;

public class ScreenHandler {
    private static final String TAG = "EnmenuScreenHandler";

    private static Context m_ctx;

    public ScreenHandler(Context ctx){
        m_ctx = ctx;
    }

    public static String screen_brightness() {
            String ret = null;
            int value = -1;
            int mode = -1;

            ContentResolver cr = m_ctx.getContentResolver();

            try {
                    value = Settings.System.getInt(cr,
                                    Settings.System.SCREEN_BRIGHTNESS);
                    mode = Settings.System.getInt(m_ctx.getContentResolver(),
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
                                m_ctx.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (SettingNotFoundException e) {

        }
        if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                set_screen_brightness_mode(0);
        }

        try {
                Uri uri = android.provider.Settings.System
                                .getUriFor("screen_brightness");
                Settings.System.putInt(m_ctx.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, (int) value);
                m_ctx.getContentResolver().notifyChange(uri, null);
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
    private void __set_screen_brightness_value(float value) {

            Log.i(TAG, "___set_screen_brightness_valuessssssssssssssssssssssss");
            int screenMode = -1;
            try {
                    screenMode = Settings.System.getInt(
                                    m_ctx.getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (SettingNotFoundException e) {

            }
            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    set_screen_brightness_mode(0);
            }

            try {
                    LayoutParams lp = ((Activity)m_ctx).getWindow()
                                    .getAttributes();
                    lp.screenBrightness = (value <= 0 ? 1 : value) / 255.0f;
                    ((Activity)m_ctx).getWindow().setAttributes(lp);
                    Log.i(TAG, "ssssssssssssssssssssssss");

                    Uri uri = android.provider.Settings.System
                                    .getUriFor("screen_brightness");
                    Settings.System.putInt(m_ctx.getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, (int) value);
                    m_ctx.getContentResolver().notifyChange(uri, null);
            } catch (Exception e) {

                    Log.e(TAG, "exception", e);
            }

    }

    /**
     * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void set_screen_brightness_mode(int mode) {

            // Settings.System.putInt(m_instance.getContentResolver(),
            // Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            Settings.System.putInt(m_ctx.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }
}

