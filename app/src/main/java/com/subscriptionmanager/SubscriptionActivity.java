package com.subscriptionmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.subscriptionmanager.dao.SubscriptionManagerDAO;
import com.subscriptionmanager.model.Subscription;
import com.subscriptionmanager.utility.DatePickerFragment;
import com.subscriptionmanager.utility.TimePickerFragment;
import com.subscriptionmanager.utility.Utility;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SubscriptionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "SubMgr.info";
    private Button satrtCalendarButton, endCalendarButton, remindCalendarButton, remindTimeButton, saveButton;
    private TextView startDateTextView, endDateTextView, remindDateTextView, newSubName;
    private int request;
    private String dateView, useCase, remindDateString, temp, myRemindDate;
    private Date date, startDate, endDate, remindDate;
    private EditText editText;
    private Subscription newSubscription, oldSubscription;
    private List<String> errorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
        Intent intent = getIntent();
        if(intent.hasExtra("Subscription")) {
            oldSubscription = (Subscription) intent.getSerializableExtra("Subscription");
            if(oldSubscription != null) {
                setupData(oldSubscription);
            }
            useCase = "update";
        }
        else{
            useCase = "create";
        }
    }

    private void initialize() {
        satrtCalendarButton = findViewById(R.id.startcalendarbutton);
        satrtCalendarButton.setOnClickListener(this);
        endCalendarButton = findViewById(R.id.endcalendarbutton);
        endCalendarButton.setOnClickListener(this);
        remindCalendarButton = findViewById(R.id.remindcalendarbutton);
        remindCalendarButton.setOnClickListener(this);
        remindTimeButton = findViewById(R.id.remindtimebutton);
        remindTimeButton.setEnabled(false);
        remindTimeButton.setOnClickListener(this);
        saveButton = findViewById(R.id.savebutton);
        saveButton.setOnClickListener(this);
        startDateTextView = findViewById(R.id.newstartdate);
        endDateTextView = findViewById(R.id.newenddate);
        remindDateTextView = findViewById(R.id.newreminddate);
        editText = findViewById(R.id.edittextnumberdecimal);
        newSubName = findViewById(R.id.newsubname);
    }

    private void setupData(Subscription oldSubscription){
        newSubName.setText(oldSubscription.getSubscription());
        editText.setText(Double.toString(oldSubscription.getCost()));
        startDateTextView.setText("Start Date : "+Utility.convertDate(oldSubscription.getStartDate()));
        endDateTextView.setText("End Date : "+Utility.convertDate(oldSubscription.getEndDate()));
        remindDateTextView.setText("Remind On : "+Utility.convertDateTime(oldSubscription.getNotifyDate()));
    }

    @Override
    public void onClick(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        switch(view.getId()){
            case R.id.startcalendarbutton :
                request = 1;
                datePicker.show(getSupportFragmentManager(), "Start Date Picker");
                break;
            case R.id.endcalendarbutton :
                request = 2;
                datePicker.show(getSupportFragmentManager(), "End Date Picker");
                break;
            case R.id.remindcalendarbutton :
                request = 3;
                datePicker.show(getSupportFragmentManager(), "Reminder Date Picker");
                break;
            case R.id.remindtimebutton :
                TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Reminder Time Picker");
                break;
            case R.id.savebutton :
                if(useCase.equals("create")){
                    errorList = new Utility().validate(newSubName, startDate, endDate, remindDate, editText);
                    if(errorList.size() == 0){
                        SubscriptionManagerDAO subscriptionManagerDAO = new SubscriptionManagerDAO(this);
                        long id = subscriptionManagerDAO.getAvailableId();
                        Subscription subscription = new Subscription(id, newSubName.getText().toString(), startDate, endDate, remindDate, Double.parseDouble(editText.getText().toString()));
                        List<String> result = subscriptionManagerDAO.addSubcription(subscription);
                        if(result.size() == 0){
                            //create the subscription for alarm
                            Utility.setAlarm(remindDate.getTime(), (AlarmManager)getSystemService(Context.ALARM_SERVICE), this, id);
                            Log.i("submgr.info","Submission added for : "+newSubName.getText().toString());
                            finish();
                        }
                        else{
                            errorList.addAll(result);
                        }
                    }
                    else{
                        Toast.makeText(this, errorList.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                else if(useCase.equals("update")){
                    if(startDate == null){
                        startDate = oldSubscription.getStartDate();
                    }
                    if(endDate == null){
                        endDate = oldSubscription.getEndDate();
                    }
                    if(remindDate == null){
                        remindDate = oldSubscription.getNotifyDate();
                    }
                    errorList = new Utility().validate(newSubName, startDate, endDate, remindDate, editText);
                    if(errorList.size() == 0){
                        SubscriptionManagerDAO subscriptionManagerDAO = new SubscriptionManagerDAO(this);
                        long id = subscriptionManagerDAO.getAvailableId();
                        newSubscription = new Subscription(
                                id, newSubName.getText().toString(), startDate, endDate, remindDate, Double.parseDouble(editText.getText().toString()));
                        List<String> result = subscriptionManagerDAO.updateSubscription(oldSubscription, newSubscription);
                        if(result.size() == 0){
                            //update the subscription for alarm
                            Utility.cancelAlarm(this, id);
                            Utility.setAlarm(remindDate.getTime(), (AlarmManager)getSystemService(Context.ALARM_SERVICE), this, id);
                            Log.i("submgr.info","Submission updated for : "+newSubscription.getSubscription());
                            finish();
                        }
                        else{
                            errorList.addAll(result);
                        }
                    }
                    else{
                        Toast.makeText(this, errorList.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month++;
        String dateString = day+"/"+month+"/"+year;
        try{
            date = new SimpleDateFormat("dd/M/yyyy").parse(dateString);
        }
        catch(Exception e){
            Log.e(TAG, "Error due to : "+e.getLocalizedMessage());
        }
        switch(request){
            case 1 :
                dateView = "Start Date : "+day+"/"+month+"/"+year;
                startDateTextView.setText(dateView);
                startDate = date;
                Log.i(TAG, "Start date is : "+ startDate.toString());
                break;
            case 2 :
                dateView = "End Date : "+day+"/"+month+"/"+year;
                endDateTextView.setText(dateView);
                endDate = date;
                Log.i(TAG, "End date : "+ endDate.toString());
                break;
            case 3 :
                dateView = "Remind On : "+day+"/"+month+"/"+year;
                remindDateString = dateView;
                remindDateTextView.setText(dateView);
                remindTimeButton.setEnabled(true);
                remindTimeButton.setOnClickListener(this);
                temp = ""+day+"/"+month+"/"+year;
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String result;
        if(hour>12){
            result = remindDateString+" "+(hour-12)+":"+minute+" PM";
            myRemindDate = temp +" " + (hour) + ":" + minute;
        }
        else if(hour==0){
            result = remindDateString+" "+(hour+12)+":"+minute+" AM";
            myRemindDate = temp +" " + (hour) + ":" + minute;
        }
        else if(hour==12){
            result = remindDateString+" "+hour+":"+minute+" PM";
            myRemindDate = temp +" " + (hour) + ":" + minute;
        }
        else{
            result = remindDateString+" "+hour+":"+minute+" AM";
            myRemindDate = temp +" "+ (hour) + ":" + minute;
        }
        remindDateTextView.setText(result);
        try {
            remindDate = new SimpleDateFormat("dd/M/yyyy H:mm").parse(myRemindDate);
            Log.i(TAG, "Successfully parsed date : "+ remindDate.toString());
        } catch (ParseException e) {
            Log.i(TAG, "Faied for Remind Date "+ result + " where I was going to parse "+ myRemindDate);
            e.printStackTrace();
        }
    }
}