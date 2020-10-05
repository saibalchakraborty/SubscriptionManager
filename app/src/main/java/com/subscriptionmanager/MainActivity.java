package com.subscriptionmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.subscriptionmanager.dao.SubscriptionManagerDAO;
import com.subscriptionmanager.model.Subscription;
import com.subscriptionmanager.utility.RecyclerAdapter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton sendButton;
    private RecyclerView recyclerView;
    private Intent subscriptionIntent;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<Subscription> subscriptionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subscriptionIntent = new Intent(this, SubscriptionActivity.class);
        setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptionList = new SubscriptionManagerDAO(this).getAllSubscriptions();
        recyclerAdapter = new RecyclerAdapter(subscriptionList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setup() {
        sendButton = findViewById(R.id.newitem);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(subscriptionIntent);
            }
        });
    }
}