package com.example.jayeshplayvideoapp.activities;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jayeshplayvideoapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {


    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.player_view)
    PlayerView playerView;
    ImageView fullscreenIcon;


    //custom objects
    private SimpleExoPlayer player;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    //dataType & Variables
    private String status = "";
    private boolean playWhenReady = false;
    private int currentWindow = 0;
    private long playbackPosition = 0;

 /*   private List<String> mVideoUrls;
    private String videoUrl;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        View controlView = playerView.findViewById(R.id.exo_controller);
        fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        controlView.findViewById(R.id.exo_fullscreen_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeScreenOrientation();
                    }
                });

    }


    private void changeScreenOrientation() {
        int orientation = TestActivity.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(Build.VERSION.SDK_INT < 9 ?
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_enter);
        } else {
            setRequestedOrientation(Build.VERSION.SDK_INT < 9 ?
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_exit);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }


    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }


    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            // Bind the player to the view.
            playerView.setPlayer(player);
            playerView.setShowBuffering(true);
            //playerView.setShowMultiWindowTimeBar(true);
            //playerView.setShowShuffleButton(true);
            //playerView.setControllerAutoShow(true);
            //playerView.setRepeatToggleModes(2);

            player.setPlayWhenReady(true);
            player.seekTo(currentWindow, playbackPosition);
            //player.setRepeatMode(Player.REPEAT_MODE_ALL);
            //player.setShuffleModeEnabled(true);

            List<String> urls = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                urls.add("https://www.sample-videos.com/video123/mp4/240/big_buck_bunny_240p_1mb.mp4");
            }

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, getResources().getString(R.string.app_name)));
            // This is the MediaSource representing the media to be played.
          /*  MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse("https://www.sample-videos.com/video123/mp4/240/big_buck_bunny_240p_1mb.mp4"));
          */  // Prepare the player with the source.
            //player.prepare(videoSource);

            MediaSource[] mediaSources = new MediaSource[urls.size()];
            for (int i = 0; i < urls.size(); i++) {
                mediaSources[i] = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(urls.get(i)));

            }
            MediaSource videoSource =
                    mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
            player.prepare(videoSource);


        }
    }


    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


}
