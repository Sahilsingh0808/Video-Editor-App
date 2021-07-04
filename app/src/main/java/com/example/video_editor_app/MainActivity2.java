package com.example.video_editor_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    TextView datafrom,name,age,gender,email;
    ImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent=getIntent();
        datafrom=(TextView)findViewById(R.id.textView);
        name=(TextView)findViewById(R.id.textView2);
        age=(TextView)findViewById(R.id.textView3);
        gender=(TextView)findViewById(R.id.textView4);
        profile = (ImageView) findViewById(R.id.imageView);
        email=(TextView)findViewById(R.id.textView5);
        ArrayList<String> data=intent.getStringArrayListExtra("DATA");
        if (data.get(0).equals("Google"))
        {
            datafrom.setText("Data From Google");
            name.setText("Display Name: "+data.get(1));
            age.setText("Given Name: "+data.get(2));
            email.setText("Email: "+data.get(3));
            gender.setText("Person Id: "+data.get(4));

            Picasso.get().load(data.get(5)).into(profile);
            Log.d("Tag",data.get(5));
        }
        else
        {
            datafrom.setText("Data From Facebook");
            name.setText("Name: "+data.get(1));
            age.setText("Email: "+data.get(4));
            gender.setText("Gender: "+data.get(5));
            email.setText("Id: "+data.get(2));
            Log.d("TAG","hello"+data.get(3));
            Picasso.get().load(data.get(3)).into(profile);
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        if(src!=null) {
            try {

                Log.e("src", src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap", "returned");
                return myBitmap;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception", e.getMessage());
                return null;
            }
        }
        return  null;
    }

    public void signOut(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("CONFIRM SIGN OUT");
        builder.setMessage("Sure, you want to quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finishAffinity();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.create().show();
    }

    public void video(View view) {
        startActivity(new Intent(MainActivity2.this,Video_Initial_Screen.class));
    }
}