package com.example.musicapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    private static OnAddClickListener onItemAddClick;
    public  static int position1=-1;
    public  static int progress=0;
    public SongAdapter(Context context, int resource, ArrayList<Song> objects){
        super(context,resource,objects);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Song song=getItem(position);
        final View oneSongView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        RoundProgress roundProgress=oneSongView.findViewById(R.id.rp);
        final ImageView imageView2=oneSongView.findViewById(R.id.imageView2);
        TextView textView=oneSongView.findViewById(R.id.textView);
        TextView textView2=oneSongView.findViewById(R.id.textView2);
        roundProgress.setVisibility(View.GONE);
        if(position==position1) {
            roundProgress.setProgress(progress);
            roundProgress.postInvalidate();
            roundProgress.setVisibility(View.VISIBLE);
        }
        imageView2.setImageResource(R.drawable.fuck1);
        textView.setText(song.getFileName());
        textView2.setText(song.getSinger()+"-"+song.getAlbum());
        oneSongView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemAddClick.onItemClick(position,"1");
            }
        });
        oneSongView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemAddClick.onItemClick(position,"0");
                return false;
            }
        });
        return oneSongView;
    }
    public interface OnAddClickListener{ public void onItemClick(int position,String actionType);}
    public void setOnAddClickListener(OnAddClickListener onItemAddCliclk){this.onItemAddClick=onItemAddCliclk;}
};
