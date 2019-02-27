package com.example.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity,Main3Activity.class);

        intent.putExtra(Hook.PLUG_ACTIVITY,true);

        activity.startActivity(intent);
    }
}
