package com.example.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
    }

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity,ProxyActivity.class));
    }
}
