package com.team.videoeditorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button fb,gmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fb=(Button)findViewById(R.id.fb_login);
        gmail=(Button)findViewById(R.id.gmail_login);

    }
}