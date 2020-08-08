package com.subscriptionmanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.subscriptionmanager.model.Subscription;
import com.subscriptionmanager.utility.Utility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubscriptionManagerDAO extends SQLiteOpenHelper {
    private static final String NAME = "SubscriptionManager.db";
    private static final int version = 1;
    private String query;
    private final String CUSTOMER_TABLE = "CUSTOMER_TABLE";
    private static final String ID = "ID";
    private static final String COLUMN_SUBSCRIPTION = "SUBSCRIPTION";
    private static final String COLUMN_START_DATE = "START_DATE";
    private static final String COLUMN_END_DATE = "END_DATE";
    private static final String COLUMN_NOTIFY_DATE = "NOTIFY_DATE";
    private static final String COLUMN_COST = "COST";

    public SubscriptionManagerDAO(@Nullable Context context) {
        super(context, NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        query = "CREATE TABLE " + CUSTOMER_TABLE + " " +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SUBSCRIPTION + " INTEGER, " + COLUMN_START_DATE + " INTEGER, " + COLUMN_END_DATE + " INTEGER, " + COLUMN_NOTIFY_DATE + " INTEGER, " + COLUMN_COST + " REAL)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
        sqLiteDatabase.execSQL(query);
    }

    public List<String> addSubcription(Subscription subscription){
        List<String> errorList = new ArrayList<>();
        ArrayList<Subscription> subscriptionArrayList = getAllSubscriptions();
        for(Subscription subs : subscriptionArrayList){
            if(subs.getSubscription().equals(subscription.getSubscription())){
                errorList.add("Subscription already exists. Please update the existing Subscription");
            }
        }
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBSCRIPTION, subscription.getSubscription());
        contentValues.put(COLUMN_START_DATE, subscription.getStartDate().getTime());
        contentValues.put(COLUMN_END_DATE, subscription.getEndDate().getTime());
        contentValues.put(COLUMN_NOTIFY_DATE, subscription.getNotifyDate().getTime());
        contentValues.put(COLUMN_COST, Double.toString(subscription.getCost()));
        long result = sqLiteDatabase.insert(CUSTOMER_TABLE, null, contentValues);
        sqLiteDatabase.close();
        if(result == -1){
            errorList.add("Failed to add the Subscription "+ subscription.getSubscription());
        }
        return errorList;
    }

    public List<String> updateSubscription(Subscription oldSubscription, Subscription newSubscription){
        List<String> deleteResult = deleteSubscription(oldSubscription);
        List<String> addresult = addSubcription(newSubscription);
        addresult.addAll(deleteResult);
        return addresult;
    }

    public List<String> deleteSubscription(Subscription subscription){
        List<String> errorList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean result = sqLiteDatabase.delete(CUSTOMER_TABLE, COLUMN_SUBSCRIPTION + "=\"" + subscription.getSubscription()+"\";", null) > 0;
        sqLiteDatabase.close();
        if(! result){
            errorList.add("Unable to delete the Subscription");
        }
        return errorList;
    }

    public ArrayList<Subscription> getAllSubscriptions(){
        ArrayList<Subscription> allSubscriptions = new ArrayList<>();
        query = "SELECT * FROM "+ CUSTOMER_TABLE;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Utility util = new Utility();
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String subscription = cursor.getString(1);
                Date startDate = new Date(cursor.getLong(2));
                Date endDate = new Date(cursor.getLong(3));
                Date notifyDate = new Date(cursor.getLong(4));
                double cost = cursor.getDouble(5);
                allSubscriptions.add(new Subscription(subscription, startDate, endDate, notifyDate, cost));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return allSubscriptions;
    }
}
