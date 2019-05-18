package com.example.musicapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service{
    int position;
    private Song song;
    private MediaPlayer mediaPlayer;
    public static boolean isRunning=false;
    public MyBinder myBinder=new MyBinder();
    public IBinder onBind(Intent intent){
        return myBinder;
    }
    public class MyBinder extends Binder {
        public  Service getService(){
            return MusicService.this;
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }
    public int onStartCommand(Intent intent,int flags,int startId){
        isRunning=true;
        position=intent.getIntExtra("position",0);
        song=Song.getALLsongs(this).get(position);
        init();
        return Service.START_NOT_STICKY;
    }
    public void init(){
        mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.reset();
            Uri uri1=Uri.parse(song.getFileUrl());
            mediaPlayer.setDataSource(this,uri1);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public double  getProgress(){
        int position=mediaPlayer.getCurrentPosition();
        int time=mediaPlayer.getDuration();
        double progress=(double)position/(double)time;
        return progress;
    }
    public int getDurationTime(){
        return mediaPlayer.getCurrentPosition();
    }
    public void setProgress(int max,int dest){
        int time=mediaPlayer.getDuration();
        mediaPlayer.seekTo(time*dest/max);
    }
    public void play(){
        if(mediaPlayer!=null) {
            mediaPlayer.start();
        }
    }
    public void pause(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
    public void onDestroy(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            isRunning=false;
        }
        super.onDestroy();
    }
}
