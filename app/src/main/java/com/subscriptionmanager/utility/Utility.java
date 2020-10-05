package com.subscriptionmanager.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utility {

    public List<String> validate(TextView newSubName, Date startDate, Date endDate, Date remindDate, EditText editText) {
        List<String> issues = new ArrayList<>();
        if(newSubName == null || newSubName.getText().toString().length() == 0){
            issues.add("Please add a valid Subscription name");
        }

        if(startDate == null){
            issues.add("Please fill in the Start Date");
        }
        if(endDate == null){
            issues.add("Please fill in the End Date");
        }
        if(remindDate == null){
            issues.add("Please fill in the Notify Date");
        }

        if(startDate != null && endDate != null && remindDate != null) {
            if (endDate.before(startDate)) {
                issues.add("End Date can not be before Start Date");
            }
            if (remindDate.before(startDate)) {
                issues.add("Notify Date can not be before Start Date");
            }
            if (endDate.before(remindDate)) {
                issues.add("End Date can not be before Notify Date");
            }
        }
        if(editText == null || editText.getText().toString().length() < 1 || Double.parseDouble(editText.getText().toString()) < 0){
            issues.add("Please enter a valid cost");
        }
        return issues;
    }

    public static String convertDate(Date date){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            return formatter.format(date);
        }
        catch(Exception e){
            Log.i("submgr.info", "The date I received to parse is "+ date);
            e.getLocalizedMessage();
        }
        return null;
    }

    public static String convertDateTime(Date date){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:mm");
            return formatter.format(date);
        }
        catch(Exception e){
            Log.i("submgr.info", "The date I received to parse is "+ date);
            e.getLocalizedMessage();
        }
        return null;
    }

    public static void setAlarm(long milliSec, AlarmManager alarmManager, Context context, long id){
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)id, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milliSec, pendingIntent);
    }

    public static void cancelAlarm(Context context, long id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)id, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
