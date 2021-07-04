package com.example.video_editor_app;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class FFMpegService extends Service {

    FFmpeg fFmpeg;
    int duration;
    String[] command;
    Callbacks activity;

    public MutableLiveData<Integer> percentage;
    IBinder mybinder=new LocalBinder();

    public FFMpegService()
    {
        super();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null)
        {
            duration=Integer.parseInt(intent.getStringExtra("duration"));
            command=intent.getStringArrayExtra("command");
            try
            {
                loadFFMpegBinary();
                execFFMpegCommand();
            }
            catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void execFFMpegCommand() throws FFmpegCommandAlreadyRunningException {
        fFmpeg.execute(command,new ExecuteBinaryResponseHandler()
        {
            @Override
            public void onFailure(String message) {
                super.onFailure(message);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                percentage.setValue(100);
            }

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
            }

            @Override
            public void onProgress(String message) {
                String arr[];
                if(message.contains("time="))
                {
                    arr=message.split("time=");
                    String yalo=arr[1];
                    String abikamha[]=yalo.split(":");
                    String[] yakenda=abikamha[2].split(" ");
                    String seconds=yakenda[0];

                    int hours=Integer.parseInt(abikamha[0]);
                    hours*=3600;
                    int min=Integer.parseInt(abikamha[1]);
                    min*=60;
                    float sec=Float.valueOf(seconds);
                    float timeInSec=hours+min+sec;

                    percentage.setValue((int)((timeInSec/duration)*100));

                }
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            loadFFMpegBinary();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
        percentage=new MutableLiveData<>();

    }

    private void loadFFMpegBinary() throws FFmpegNotSupportedException {
        if(fFmpeg==null)
        {
            fFmpeg= FFmpeg.getInstance(this);

        }
        fFmpeg.loadBinary(new LoadBinaryResponseHandler()
                          {

                              @Override
                              public void onFailure() {
                                  super.onFailure();
                              }

                              @Override
                              public void onSuccess() {
                                  super.onSuccess();
                              }
                          }

        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    public  class LocalBinder extends Binder
    {
        public FFMpegService getServiceInstance()
        {
            return FFMpegService.this;
        }
    }

    public void registerClient(Activity activity)
    {
        this.activity=(Callbacks)activity;
    }

    public MutableLiveData<Integer> getPercentage()
    {
        return  percentage;
    }

    public interface Callbacks
    {
        void updateClient(float data);

    }
}
