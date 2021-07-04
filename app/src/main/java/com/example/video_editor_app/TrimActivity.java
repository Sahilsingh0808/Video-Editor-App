package com.example.video_editor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;

public class TrimActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    Uri uri;
    ImageView imageView;
    VideoView videoView;
    Button speed_button;
    EditText speed;
    TextView textLeft,textRight;
    RangeSeekBar rangeSeekBar;
    boolean isPlaying=false;
    int duration;
    String filePrefix;
    String[] command;
    File dest;
    String original_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        imageView=(ImageView)findViewById(R.id.pause);
        speed_button=(Button)findViewById(R.id.speed_button);
        videoView=(VideoView)findViewById(R.id.videoView);
        textLeft=(TextView)findViewById(R.id.tvvLeft);
        textRight=(TextView)findViewById(R.id.tvvRight);
        rangeSeekBar=(RangeSeekBar)findViewById(R.id.seekbar);
        speed=(EditText)findViewById(R.id.speed);
        Intent intent=getIntent();

        if(intent!=null)
        {
            String imgPath= intent.getStringExtra("uri");
            uri=Uri.parse(imgPath);
            videoView.setVideoURI(uri);
            videoView.start();
            isPlaying=true;
        }
        setListeners();
        speed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed(Float.parseFloat(speed.getText().toString()));
            }
        });

    }

    private void setListeners()
    {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying)
                {
                    imageView.setImageResource(R.drawable.play);
                    videoView.pause();
                    isPlaying=false;
                }
                else
                {
                    videoView.start();
                    imageView.setImageResource(R.drawable.pause);
                    isPlaying=true;
                }
            }
        });



    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();
            duration=mp.getDuration()/1000;
            textLeft.setText("00:00:00");
            textRight.setText(getTime(duration));
            mp.setLooping(true);
            rangeSeekBar.setRangeValues(0,duration);
            rangeSeekBar.setSelectedMaxValue(duration);
            rangeSeekBar.setEnabled(true);
            rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                    videoView.seekTo((int)minValue*1000);
                    textLeft.setText(getTime((int)bar.getSelectedMinValue()));
                    textRight.setText(getTime((int)bar.getSelectedMaxValue()));
                }
            });

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(videoView.getCurrentPosition()>=rangeSeekBar.getSelectedMaxValue().intValue()*1000)
                        videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue()*1000);

                }
            },1000);
        }
    });
    }

    private String getTime(int seconds)
    {
        int hr=seconds/3600;
        int rem=seconds%3600;
        int min=rem/60;
        int sec=rem%60;
        return String.format("%02d",hr)+":"+String.format("%02d",min)+":"+String.format("%02d",sec);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.trim)
        {
            final AlertDialog.Builder alert=new AlertDialog.Builder(TrimActivity.this);
            LinearLayout linearLayout=new LinearLayout(TrimActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50,0,50,100);
            final EditText input=new EditText(TrimActivity.this);
            input.setLayoutParams(layoutParams);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linearLayout.addView(input,layoutParams);
            alert.setMessage("Set video name");
            alert.setTitle("Change video name");
            alert.setView(linearLayout);
            alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            alert.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filePrefix=input.getText().toString();
                    trimVideo(rangeSeekBar.getSelectedMinValue().intValue()*1000,rangeSeekBar.getSelectedMaxValue().intValue()*1000,filePrefix);
                    Intent intent=new Intent(TrimActivity.this,ProgressBarActivity.class);
                    intent.putExtra("duration",duration);
                    intent.putExtra("command",command);
                    intent.putExtra("destination",dest.getAbsolutePath());
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                }
            });
            alert.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void trimVideo(int start,int end,String fileName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                Log.i("TAG","permission");
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                Log.i("TAG","granted");
            }
        }
        File folder=new File(Environment.getExternalStorageDirectory()+"/TrimVideos");
        if(!folder.exists())
        {
            folder.mkdir();
//            try {
//                folder.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            Log.i("TAG","making new folders "+String.valueOf(folder.mkdir()));
        }
        filePrefix=fileName;
        String fileExt=".mp4";
        dest=new File(folder,filePrefix+fileExt);
        original_path=getRealPathfromUri(getApplicationContext(),uri);
        duration=(end-start)/1000;
        command=new String[]{"-ss",""+start/1000,"-y","-i",original_path,"-t",""+(end-start)/1000,"-vcodec","mpeg4","-b:v","2097152","-b:a",
        "48000","-ac","2","-ar","22050",dest.getAbsolutePath()};
    }

    private String getRealPathfromUri(Context context, Uri uri)
    {
        Cursor cursor=null;
        try {

            String[] proj={MediaStore.Images.Media.DATA};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cursor=context.getContentResolver().query(uri,proj,null,null);
            }
            int column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(cursor!=null)
                cursor.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setSpeed(float a)
    {
        Log.i("TAG","sahil");

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                PlaybackParams myPlayBackParams = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    myPlayBackParams = new PlaybackParams();
                    myPlayBackParams.setSpeed(a); //you can set speed here
                    Log.i("TAG","sahil");
                    mp.setPlaybackParams(myPlayBackParams);
                }
            }
        });
    }
}