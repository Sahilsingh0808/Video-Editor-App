package com.example.video_editor_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

public class Video_Initial_Screen extends AppCompatActivity {


    Uri selectedUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_initial_screen);
    }
    public void openVideo(View v)
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent,100);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK)
        {
            selectedUri=data.getData();
            Intent intent=new Intent(Video_Initial_Screen.this,TrimActivity.class);
            intent.putExtra("uri",selectedUri.toString());
            startActivity(intent);
            Log.i("TAG",selectedUri.toString());
            Log.i("TAG","working");

        }
    }
}