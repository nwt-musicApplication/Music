package com.example.musicapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Date;


public class playerActivity extends BaseActivity {

    private Button play;
    private Button last;
    private Button next;
    private TextView title;
    private TextView singeralbum;
    private VoisePlayingIcon voisePlayingIcon;
    private TextView runningtime;
    private TextView alltime;
    private SeekBar seekBar;
    private int position;
    private int totleTime;
    private Song song;
    private int size;
    boolean mBound=false;
    boolean flag=false;
    Thread myThread;
    boolean playStatus=true;
    MusicService musicService;

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                double progress=msg.getData().getDouble("progress");
                int currentTime=msg.getData().getInt("currentTime");
                int max=seekBar.getMax();
                int position=(int)(max*progress);
                seekBar.setProgress(position);
                runningtime.setText(formatime(currentTime));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        play=findViewById(R.id.play);
        last=findViewById(R.id.last);
        next=findViewById(R.id.next);
        voisePlayingIcon=findViewById(R.id.voise_playint_icon);
        title=findViewById(R.id.title);
        singeralbum=findViewById(R.id.singeralbum);
        runningtime=findViewById(R.id.runningtime);
        alltime=findViewById(R.id.alltime);
        seekBar=findViewById(R.id.playSeekBar);

        last.setBackgroundResource(R.drawable.last);
        next.setBackgroundResource(R.drawable.next);
        position=getIntent().getIntExtra("position",0);
        song=Song.getALLsongs(this).get(position);
        size=Song.getALLsongs(this).size();
        play.setBackgroundResource(R.drawable.play);
        init();
        voisePlayingIcon.start();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if(position==size)
                    position=0;
                title.setText(song.getFileName());
                singeralbum.setText(song.getSinger()+"-"+song.getAlbum());
                playStatus=false;
                mBound=false;
                unbindService(mConnection);
                init();
                voisePlayingIcon.start();
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if(position==-1)
                    position=size-1;
                title.setText(song.getFileName());
                singeralbum.setText(song.getSinger()+"-"+song.getAlbum());
                playStatus=false;
                mBound=false;
                unbindService(mConnection);
                init();
                voisePlayingIcon.start();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound&&flag){
                    play.setBackgroundResource(R.drawable.play);
                    musicService.pause();
                    flag=false;
                    voisePlayingIcon.stop();
                }
                else {
                    play.setBackgroundResource(R.drawable.stop);
                    musicService.play();
                    flag=true;
                    voisePlayingIcon.start();
                }
            }
        });
    }
    public void init(){
        if(MusicService.isRunning==true){
            stopService(new Intent(this,MusicService.class));
        }
        song=Song.getALLsongs(this).get(position);
        title.setText(song.getFileName());
        singeralbum.setText(song.getSinger()+"-"+song.getAlbum());
        play.setBackgroundResource(R.drawable.stop);
        totleTime=song.getDuration();
        alltime.setText(formatime(totleTime));
        Intent serviceInetnt=new Intent(this,MusicService.class);
        serviceInetnt.putExtra("position",position);
        if(mBound==false){
            startService(serviceInetnt);
            bindService(serviceInetnt,mConnection,BIND_AUTO_CREATE);
        }
        myThread=new Thread(new UpdateProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int dest=seekBar.getProgress();
                int max=seekBar.getMax();
                musicService.setProgress(max,dest);
            }
        });
    }

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder=(MusicService.MyBinder)service;
            musicService=(MusicService)myBinder.getService();
            mBound=true;
            playStatus=true;
            play.setBackgroundResource(R.drawable.stop);
            myThread.start();
            flag=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound=false;
        }
    };
    private class UpdateProgress implements Runnable{
        Bundle data=new Bundle();
        int millisecond=100;
        double progress;
        int currentTime;
        @Override
        public void run(){
            while (playStatus){
                try {
                    if(mBound){
                        Message msg=new Message();
                        data.clear();
                        progress=musicService.getProgress();
                        currentTime=musicService.getDurationTime();
                        msg.what=0;
                        data.putDouble("progress",progress);
                        data.putInt("currentTime",currentTime);
                        msg.setData(data);
                        mhandler.sendMessage(msg);
                    }
                    Thread.sleep(millisecond);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String formatime(int lengrh) {
        Date date = new Date(lengrh);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String totalTime = sdf.format(date);
        return totalTime;
    }
    public void onPanelOpened(View view) {
        Intent data=new Intent();
        String number=song.getFileName();
        data.putExtra("number",number);
        data.putExtra("position1",position);
        setResult(0,data);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        playStatus=false;
        finish();
    }
    public void finish(){
        Intent data=new Intent();
        String number=song.getFileName();
        data.putExtra("number",number);
        data.putExtra("position1",position);
        setResult(1,data);
        playStatus=false;
        super.finish();
    }
}