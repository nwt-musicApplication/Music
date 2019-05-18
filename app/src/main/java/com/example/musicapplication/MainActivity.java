package com.example.musicapplication;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ListView listView;
    public SongAdapter songAdapter;
    public TextView textofsong;
    public Button btn1ofFront;
    public Button btn2ofFront;
    public LinearLayout linearLayout;
    public ImageView imageView;
    private int flag=0;
    public static MediaPlayer mediaPlayer=new MediaPlayer();
    private  Song song;
    Thread thread;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0)
            {
                int progress=msg.getData().getInt("info");
                songAdapter.progress=progress;
                songAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frontpage);

        imageView=findViewById(R.id.imageView2);
        linearLayout=findViewById(R.id.linearLayout);
        textofsong=findViewById(R.id.song);
        btn1ofFront=findViewById(R.id.button1);
        btn2ofFront=findViewById(R.id.button2);
        btn1ofFront.setBackgroundResource(R.drawable.frontplay);
        btn2ofFront.setBackgroundResource(R.drawable.frontfinish);

        final int i= ContextCompat.checkSelfPermission(getApplication(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if (i!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        listView=findViewById(R.id.listview);
        songAdapter.position1=-1;
        songAdapter=new SongAdapter(this,R.layout.item,Song.getALLsongs(this));
        listView.setAdapter(songAdapter);
        songAdapter.setOnAddClickListener(new SongAdapter.OnAddClickListener() {
            @Override
            public void onItemClick(int position, String actionType) {
                if(actionType.equals("0")){
                    if(mediaPlayer.isPlaying())
                        mediaPlayer.reset();
                    Intent intent=new Intent(songAdapter.getContext(),playerActivity.class);
                    intent.putExtra("position",position);
                    startActivityForResult(intent,0);
                }
                if(actionType.equals("1")){
                    textofsong.setText(songAdapter.getItem(position).getFileName());
                    btn1ofFront.setBackgroundResource(R.drawable.frontstop);
                    flag=1;
                    songAdapter.position1=position;
                    songAdapter.notifyDataSetChanged();
                    boolean IsRunning=isServiceRunning(songAdapter.getContext(),"com.example.musicapplication.MusicService");
                    if(IsRunning==true)
                        stopService(new Intent(songAdapter.getContext(), MusicService.class));
                    song=Song.getALLsongs(songAdapter.getContext()).get(position);
                    try {
                        mediaPlayer.reset();
                        Uri uri1 = Uri.parse(song.getFileUrl());
                        mediaPlayer.setDataSource(songAdapter.getContext(), uri1);
                        mediaPlayer.prepare();
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    thread=new Thread(new MusicThread());
                    thread.start();
                }
            }
        });
        btn1ofFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0)
                {
                    btn1ofFront.setBackgroundResource(R.drawable.frontstop);
                    mediaPlayer.start();
                    flag=1;
                    thread=new Thread(new MusicThread());
                    thread.start();
                }
                else
                {
                    btn1ofFront.setBackgroundResource(R.drawable.frontplay);
                    mediaPlayer.pause();
                    flag=0;
                }
            }
        });
        btn2ofFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.reset();
                    btn1ofFront.setBackgroundResource(R.drawable.frontplay);
                    flag=0;
                }
                songAdapter.position1=-1;
                songAdapter.notifyDataSetChanged();
            }
        });
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent deta){
        if(deta!=null){
            songAdapter=new SongAdapter(this,R.layout.item,Song.getALLsongs(this));
            listView.setAdapter(songAdapter);
            String nummber=deta.getStringExtra("number");
            int positionfromplayer=deta.getIntExtra("position1",-1);
            if(requestCode==0)
            {
                textofsong.setText(nummber);
                songAdapter.position1=positionfromplayer;
                songAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode,resultCode,deta);
    }
    public boolean isServiceRunning(Context context,String classname){
        boolean isRunning=false;
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList=activityManager.getRunningServices(50);
        if(!(((List) serviceInfoList).size()>0)){
            return false;
        }
        for(int i=0;i<serviceInfoList.size();i++)
        {
            String a=serviceInfoList.get(i).service.getClassName();
            if(a.equals(classname)==true){
                isRunning=true;
                break;
            }
        }
        return isRunning;
    }
    class MusicThread implements Runnable{
        Bundle info=new Bundle();
        @Override
        public void run(){
            while(mediaPlayer.isPlaying()){
                try{
                    Message message=new Message();
                    int progress2=mediaPlayer.getDuration();
                    int progress1=mediaPlayer.getCurrentPosition();
                    double progress=(double) progress1/(double) progress2;
                    message.what=0;
                    int pro=(int)(progress*100);
                    Log.i("pregress/total",pro+"/"+progress2);
                    info.putInt("info",pro);
                    message.setData(info);
                    handler.sendMessage(message);
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}