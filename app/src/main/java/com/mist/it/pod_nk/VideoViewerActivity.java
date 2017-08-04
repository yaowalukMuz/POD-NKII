package com.mist.it.pod_nk;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoViewerActivity extends AppCompatActivity {


    @BindView(R.id.videoView)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);
        ButterKnife.bind(this);

        Uri videoLink = Uri.parse("http://service.eternity.co.th/dms_nk/app/centerservice/video/Video_2017-08-04_154919.wmv");
        videoView.setVideoURI(videoLink);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });

    }
}
