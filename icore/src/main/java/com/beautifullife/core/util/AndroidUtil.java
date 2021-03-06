package com.beautifullife.core.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by admin on 2015/9/9.
 */
public class AndroidUtil {

    private static final String UNIQUENESS_ID = "UNIQUENESS_ID";

    public static PackageInfo getPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public static String getAppVersionName(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (pi == null) {
            return "";
        }
        return pi.versionName;
    }

    public static int getAppVersionCode(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (pi == null) {
            return -1;
        }
        return pi.versionCode;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }

    public static String getMacAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wm.getConnectionInfo().getMacAddress();
        return macAddress;
    }

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static void cancelAllNotification(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    public static void adjustVoiceToSystemSame(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, 0);
    }

    public static void hideSoftInput(Activity activity, View editText) {
        View view;
        if (editText == null) {
            view = activity.getCurrentFocus();
        } else {
            view = editText;
        }

        if (view != null) {
            InputMethodManager inputMethod = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethod.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftInput(Activity activity, View editText) {
        View view;
        if (editText == null) {
            view = activity.getCurrentFocus();
        } else {
            view = editText;
        }

        if (view != null) {
            InputMethodManager inputMethod = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethod.showSoftInput(view, 0);
        }
    }

    public static boolean isNetworkConnectedByCmwap(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getExtraInfo() != null && networkInfo.getExtraInfo().toLowerCase().contains("cmwap");
    }

    public static boolean isBundPhone(String carrier) {
        boolean isBunding = true;
        if (TextUtils.isEmpty(carrier) || carrier.equals("NIL") || carrier.equals("ROBOT")) {
            isBunding = false;
        }
        return isBunding;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            // mContext.getPackageInfo(String packageName, int
            // flags)
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isNetworkConnectedByWifi(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void openGPU(Window window) {
        try {
            Field field = WindowManager.LayoutParams.class.getField("FLAG_HARDWARE_ACCELERATED");
            Field field2 = WindowManager.LayoutParams.class.getField("FLAG_HARDWARE_ACCELERATED");
            if (field != null && field2 != null) {
                window.setFlags(field.getInt(null), field2.getInt(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppOnForeground(Context context, String packageName) {
        if (packageName != null) {
            // Returns a list of application processes that are running on the
            // device
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                    // The name of the process that this object is associated
                    // with.
                    if (appProcess.processName.equals(packageName) && ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static boolean isDefaultSms(Context context) {
        if (context == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        }
        String packageName = Telephony.Sms.getDefaultSmsPackage(context);
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        String pName = context.getPackageName();
        if (TextUtils.isEmpty(pName)) {
            return false;
        }
        return packageName.equalsIgnoreCase(pName);
    }

    public static void applyForDefaultSms(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
        context.startActivity(intent);
    }

    public static Bundle getMetaDataBundleByApplication(Context context) throws PackageManager.NameNotFoundException {
        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        return applicationInfo.metaData;
    }

    public static Bundle getMetaDataBundleByActivity(Activity context) throws PackageManager.NameNotFoundException {
        ComponentName componentName = context.getComponentName();
        PackageManager pm = context.getPackageManager();
        ActivityInfo activityInfo = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA);
        return activityInfo.metaData;
    }

    public static Bundle getMetaDataBundleByService(Context context, Service service) throws PackageManager.NameNotFoundException {
        ComponentName componentName = new ComponentName(context, service.getClass());
        PackageManager pm = context.getPackageManager();
        ServiceInfo serviceInfo = pm.getServiceInfo(componentName, PackageManager.GET_META_DATA);
        return serviceInfo.metaData;
    }

    public static Bundle getMetaDataBundleByReciver(Context context, BroadcastReceiver receiver) throws PackageManager.NameNotFoundException {
        ComponentName componentName = new ComponentName(context, receiver.getClass());
        PackageManager pm = context.getPackageManager();
        ActivityInfo activitInfo = pm.getReceiverInfo(componentName, PackageManager.GET_META_DATA);
        return activitInfo.metaData;
    }

    public static int heapSize(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = manager.getLargeMemoryClass();
        return heapSize;
    }

    public static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }
}
