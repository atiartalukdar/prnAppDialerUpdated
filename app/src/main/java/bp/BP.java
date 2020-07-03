package bp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import info.atiar.prnappdialer.NumberDialActivity;

public class BP {
    private static final String PREFS_NAME = "pref";
    public static final String SMS_SENT = "sent";
    public static final String SMS_NOT_SENT = "notSent";
    public static final String SMS_DELIVERED = "delivered";
    public static final String SMS_NOT_DELIVERED = "notDelivered";
    public static final String simpleMessage = "sending a test message";
    public static boolean isWaitNeededToSentSMS = false;

    public static String  websiteLimitKey = "websiteLimit";
    public static String numberLimitKey = "numberLimit";

    public static Activity numberDialActivity;
    public static Queue<String> queue = new LinkedList<>();
    public static boolean isSingleNumber = false;
    public static boolean isCallFromApp = false;
    public static boolean isCallRunning = false;

    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public static void callNumber(Activity activity, String phoneNumber) {
        isCallFromApp = true;
        numberDialActivity = activity;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }

    public static void callNumberFromNumberDialActivity() {
        callNumber(numberDialActivity,queue.remove());
    }

    public static void callNumberFromNumberDialActivity(Activity activity) {
        numberDialActivity = activity;
        try {
            callNumber(numberDialActivity,queue.remove());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void savePreviousState(Boolean previousState){
            setPreference("prev",previousState);
    }

    public static boolean getPreviousState(){
            return getPreference("prev");
    }

    public static void setWebsiteLimit(int limit){
        setPreference(websiteLimitKey,limit);
    }

    public static void setNumberLimit(int limit){
        setPreference(numberLimitKey,limit);
    }

    public static int getNumberLimit(){
        return getPreferenceInt(numberLimitKey);
    }
    public static int getWebsiteLimit(){
        return getPreferenceInt(websiteLimitKey);
    }
    //SharedPreferences
    private static boolean setPreference(String key, boolean value) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static boolean setPreference(String key, int value) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    private static boolean getPreference(String key) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    private static int getPreferenceInt(String key) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, 10);
    }

    private static void removeSingleItem(String keyToRemove) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().remove(keyToRemove).commit();
    }

    private static void removeAllItem() {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

}
