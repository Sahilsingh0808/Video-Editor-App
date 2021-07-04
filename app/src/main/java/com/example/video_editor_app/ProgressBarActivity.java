package com.example.video_editor_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;

public class ProgressBarActivity extends AppCompatActivity {

    CircleProgressBar circleProgressBar;
    int duration;
    String[] command;
    String path;
    ServiceConnection mConnection;
    FFMpegService ffMpegService;
    Integer res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        circleProgressBar=(CircleProgressBar)findViewById(R.id.circleProgressBar);
        circleProgressBar.setMax(100);
        final Intent intent=getIntent();
        if(intent!=null)
        {
            duration=intent.getIntExtra("duration",0);
            command=intent.getStringArrayExtra("command");
            path=intent.getStringExtra("destination");
            Intent intent1=new Intent(ProgressBarActivity.this,FFMpegService.class);
            intent1.putExtra("duration",String.valueOf(duration));
            intent1.putExtra("command",command);
            intent1.putExtra("destination",path);
            startService(intent1);

            mConnection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    FFMpegService.LocalBinder binder=(FFMpegService.LocalBinder)service;
                    ffMpegService = binder.getServiceInstance();
                    ffMpegService.registerClient(getParent());

                    final Observer<Integer> resultObserver=new Observer<Integer>(){
                        @Override
                        public void onChanged(Integer integer) {
                            res=integer;
                            if(res<100){
                                circleProgressBar.setProgress(res);
                            }
                            if(res==100)
                            {
                                circleProgressBar.setProgress(res);
                                stopService(intent1);
                                Toast.makeText(ffMpegService, "Video Trimmed Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    ffMpegService.getPercentage().observe(ProgressBarActivity.this,resultObserver);

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            bindService(intent1,mConnection, Context.BIND_AUTO_CREATE);
        }
    }
}