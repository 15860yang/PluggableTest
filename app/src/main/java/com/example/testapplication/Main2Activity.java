package com.example.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Main3Activity.startActivity(this);
    }

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity,Main2Activity.class));
    }
}
