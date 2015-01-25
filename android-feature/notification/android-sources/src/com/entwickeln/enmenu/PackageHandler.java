package com.entwickeln.enmenu;

import android.util.Log;
import java.io.File;
import java.io.DataOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.net.Uri;
public class PackageHandler {
    private Context m_ctx;

    public PackageHandler(Context ctx){
        m_ctx = ctx;
    }

    public void install (String file_path){
           //String apkName = Environment.getExternalStorageDirectory()
           //						.toString() + File.separator + "test_vivi.apk";
           haveRoot("pm install -r " + file_path);
   }

   public void install_apk(String absPath){
               String chmodCmd = "chmod 666 " + absPath;
               try {
                         Runtime.getRuntime().exec(chmodCmd);
               } catch (Exception e) {
               }
              //__log("the abs Path is " + absPath);
              Intent intent = new Intent(Intent.ACTION_VIEW);
              intent.setDataAndType(Uri.fromFile(new File(absPath)),
                               "application/vnd.android.package-archive");
              m_ctx.startActivity(intent);
              //return false;
          }


    private boolean haveRoot(String cmd) {
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
    private int execRootCmdSilent(String paramString) {
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

