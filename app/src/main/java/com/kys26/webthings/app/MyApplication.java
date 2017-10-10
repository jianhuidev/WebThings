/**
 * MyApplication.java [V 1..0.0]
 * classes : com.hb56.DriverReservation.android.app.MyApplication
 * zhangyx Create at 2014-11-26 涓嬪崍4:04:30
 */
package com.kys26.webthings.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.rong.imkit.RongIM;

/***
 *
 *com.zhangyx.MyGestureLock.app.MyApplication
 * @author Admin-zhangyx
 *
 * create at 2015-1-16 涓嬪崍3:14:07
 */
public class MyApplication extends Application {

//	private LockPatternUtils mLockPatternUtils;// 鎵嬪娍锟�?

    private String userName;

    /**
     * 是否开启debug
     */
    public boolean isDebug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
        Context context = getApplicationContext();
// 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
// 初始化Bugly
        CrashReport.initCrashReport(context, "ea159daedf", isDebug, strategy);
// 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
// CrashReport.initCrashReport(context, strategy);
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
