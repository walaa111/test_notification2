package com.plugin.gcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";
    private String severity;
    private String type;
    private static String[] alert_parameters;
    private String alertMessage;
    private final String SEVERITY_CRITICAL = "0";
    private final String SEVERITY_IMPORTANT = "1";

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    public void onRegistered(Context context, String regId) {

        Log.v(TAG, "onRegistered: " + regId);

        JSONObject json;

        try {
            json = new JSONObject().put("event", "registered");
            json.put("regid", regId);

            Log.v(TAG, "onRegistered: " + json.toString());

            // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
            // In this case this is the registration ID
            PushPlugin.sendJavascript(json);

        } catch (JSONException e) {
            // No message to the user is sent, JSON failed
            Log.e(TAG, "onRegistered: JSON exception");
        }
    }

    @Override
    public void onUnregistered(Context context, String regId) {
        Log.d(TAG, "onUnregistered - regId: " + regId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "onMessage - context: " + context);

        // Extract the payload from the message
        Bundle extras = intent.getExtras();
        JSONObject extraJSON = null, temp1 = null, temp2 = null, root = null;
        JSONArray param = null;
        if (extras != null) {
            // the function does what the name says and puts the "notification"
            //part of the data into a string called notification parsed, 
            //why convert a string to json and back into string?
            //well the first string has a lot of data that we have no interest in, hence converting 
            // it to json to filter out the unnecessary junk and just keep the good parts
            extraJSON = convertBundleToJson(extras);
            try {
                root = new JSONObject(extraJSON.getString("notification_parsed"));
                severity = root.getString("severity");
                type = root.getString("type");
                param = root.getJSONArray("parameters");
                int length = param.length();
                if (length > 0) {
                    alert_parameters = new String[length];
                    for (int i = 0; i < length; i++) {
                        alert_parameters[i] = param.getString(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            extras.putBoolean("foreground", false);
            if (type != null && type.length() != 0)
                //this also does what its name says, it will generate the message to be displayed 
                //in the notification tray 
                createMessage(type, context, extras);
            if (!(extras.getString("message").equals("")))
                createNotification(context, extras);
        }

    }

    private void createMessage(String type, Context context, Bundle extras) {
        int nameResourceID = context.getResources().getIdentifier(type, "string", context.getApplicationInfo().packageName);
        if (nameResourceID == 0) {
            extras.putString("message", "");
            throw new IllegalArgumentException("No resource string found with name " + type);
        } else {
            alertMessage = context.getString(nameResourceID);
            Pattern r = Pattern.compile("\\{([0-9]+)\\}");
            Matcher m = r.matcher(alertMessage);
            int count = 0;
            while (m.find())
                count++;
            try {
                if (count == 0) {
                } else if (count == 1 && alert_parameters.length > 0) {
                    alertMessage = MessageFormat.format(alertMessage, alert_parameters[0]);
                } else if (count == 2 && alert_parameters.length > 1) {
                    alertMessage = MessageFormat.format(alertMessage, alert_parameters[0], alert_parameters[1]);
                } else {
                    alertMessage = "";
                }
                extras.putString("message", alertMessage);
            }catch(IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }

    public void createNotification(Context context, Bundle extras) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String appName = getAppName(this);

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(100);


        Intent notificationIntent = new Intent(this, PushHandlerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("pushBundle", extras);
        PendingIntent contentIntent = PendingIntent.getActivity(this, randomInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int defaults = Notification.DEFAULT_ALL;

        if (extras.getString("defaults") != null) {
            try {
                defaults = Integer.parseInt(extras.getString("defaults"));
            } catch (NumberFormatException e) {
            }
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setDefaults(defaults)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(appName)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true);
        if (SEVERITY_CRITICAL.equals(severity)) {
            //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.important);
        	
        	int critResourceID = context.getResources().getIdentifier("critical", "drawable", context.getApplicationInfo().packageName);
        	if(critResourceID != 0){
        		mBuilder.setSmallIcon(critResourceID)/*.setLargeIcon(bm)*/;
        	}
        	
        	
        } else if (SEVERITY_IMPORTANT.equals(severity)) {
            //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.critical);
            //mBuilder.setSmallIcon(R.drawable.important)/*.setLargeIcon(bm)*/;
            
            int impResourceID = context.getResources().getIdentifier("important", "drawable", context.getApplicationInfo().packageName);
        	if(impResourceID != 0){
        		mBuilder.setSmallIcon(impResourceID)/*.setLargeIcon(bm)*/;
        	}
        	
        } else {
            //mBuilder.setSmallIcon(R.drawable.advisory);
            
            int adviResourceID = context.getResources().getIdentifier("advisory", "drawable", context.getApplicationInfo().packageName);
        	if(adviResourceID != 0){
        		mBuilder.setSmallIcon(adviResourceID)/*.setLargeIcon(bm)*/;
        	}
        }
        String message = extras.getString("message");
        if (message != null) {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message).setTicker(appName + ": " + message);
        } else {
            mBuilder.setContentText("<missing message content>");
        }

        String msgcnt = extras.getString("msgcnt");
        if (msgcnt != null) {
            mBuilder.setNumber(Integer.parseInt(msgcnt));
        }

       /* int notId = 0;

        try {
            notId = Integer.parseInt(extras.getString("notId"));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number format exception - Error parsing Notification ID: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Number format exception - Error parsing Notification ID" + e.getMessage());
        }*/

        mNotificationManager.notify((String) appName, randomInt, mBuilder.build());
    }

    private static String getAppName(Context context) {
        CharSequence appName =
                context
                        .getPackageManager()
                        .getApplicationLabel(context.getApplicationInfo());

        return (String) appName;
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.e(TAG, "onError - errorId: " + errorId);
    }

    protected static JSONObject convertBundleToJson(Bundle extras) {
        try {
            JSONObject json;
            json = new JSONObject();
            String temp = extras.getString("notification").replaceAll("(\r\n|\n)", null).replaceAll("\\\\", null);
            extras.putString("notification_parsed", temp);
            JSONObject jsondata = new JSONObject();
            Iterator<String> it = extras.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = extras.get(key);
                if (value instanceof String) {
                    // Try to figure out if the value is another JSON object
                    String strValue = (String) value;
                    if (strValue.startsWith("{")) {
                        try {
                            JSONObject json2 = new JSONObject(strValue);
                            jsondata.put(key, json2);
                        } catch (Exception e) {
                            jsondata.put(key, value);
                        }
                        // Try to figure out if the value is another JSON array
                    } else if (strValue.startsWith("[")) {
                        try {
                            JSONArray json2 = new JSONArray(strValue);
                            jsondata.put(key, json2);
                        } catch (Exception e) {
                            jsondata.put(key, value);
                        }
                    } else {
                        jsondata.put(key, value);
                    }
                }
            }
            // while
            extras.remove("notification_parsed");
            return jsondata;
        } catch (
                JSONException e
                )

        {
            Log.e(TAG, "extrasToJSON: JSON exception");
        }

        return null;
    }
}
