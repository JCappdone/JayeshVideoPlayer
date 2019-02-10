package com.example.jayeshplayvideoapp.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jayeshplayvideoapp.R;
import com.example.jayeshplayvideoapp.adapters.AdapterRelatedVideoList;
import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.jayeshplayvideoapp.utils.Constants.EXO_NEXT_CLICK;
import static com.example.jayeshplayvideoapp.utils.Constants.EXO_PREVIOUS_CLICK;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_ID;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_POS;
import static com.example.jayeshplayvideoapp.utils.Constants.VIDEO_LIST;

public class VideoDetailActivity extends AppCompatActivity {

    private static final String TAG = VideoDetailActivity.class.getSimpleName();

    //views
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.txtItemTitle)
    TextView txtItemTitle;
    @BindView(R.id.txtItemDescription)
    TextView txtItemDescription;
    @BindView(R.id.rvVideoList)
    RecyclerView rvVideoList;
    ImageView fullscreenIcon;

    ImageButton exoNext;
    ImageButton exoPrevious;

    //custom objects
    private VideoListModel mSelectedVideoListModel;
    private AdapterRelatedVideoList mAdapterRelatedVideoList;
    private SimpleExoPlayer player;

    //collection
    private List<VideoListModel> mVideoList = new ArrayList<>();
    private List<String> mVideoListUrls = new ArrayList<>();

    //dataType and variables
    private String mSelectedVideoID = "";
    private boolean playWhenReady = false;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private int videoPosition = 0;
    private int mSelectedVideoPOS = 0;

    //fireBase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_video_detail);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        setAttributes();
        initialization();


    }

    private void setAttributes() {
        if (getIntent().hasExtra(VIDEO_LIST) && getIntent().hasExtra(SELECTED_VIDEO_ID)) {
            mSelectedVideoID = getIntent().getStringExtra(SELECTED_VIDEO_ID);
            mSelectedVideoPOS = getIntent().getIntExtra(SELECTED_VIDEO_POS, 0);
            mVideoList = getIntent().getExtras().getParcelableArrayList(VIDEO_LIST);
            for (VideoListModel videoListModel : mVideoList) {
                mVideoListUrls.add(videoListModel.getUrl());
            }
        }

    }

    private void initialization() {
        ButterKnife.bind(this);
        initVideoListRecyclerView();
        refreshRelatedList(mSelectedVideoPOS);


        View controlView = playerView.findViewById(R.id.exo_controller);
        fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_enter);
        exoNext = controlView.findViewById(R.id.exo_next);
        exoPrevious = controlView.findViewById(R.id.exo_prev);
        controlView.findViewById(R.id.exo_fullscreen_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeScreenOrientation();
                    }
                });

        exoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "======== onClick: NEXT");
                storeDataInFireStore(EXO_NEXT_CLICK, player.getCurrentWindowIndex(), player.getCurrentPosition());
            }
        });

        exoPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "======== onClick: exoPrevious");
                storeDataInFireStore(EXO_PREVIOUS_CLICK, player.getCurrentWindowIndex(), player.getCurrentPosition());
                playerView.getPlayer().previous();
            }
        });


        /*
        exoPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                --videoPosition;
                refreshRelatedList();

           *//*     mVideoListUrls.clear();
                mSelectedVideoListModel = mVideoList.get(videoPosition);
                mSelectedVideoID = mSelectedVideoListModel.getId();
                for (VideoListModel videoListModel : mVideoList) {
                    if (videoListModel.getId().equals(mSelectedVideoID)) {
                        mSelectedVideoListModel = videoListModel;
                    } else {
                        mVideoListUrls.add(videoListModel.getUrl());
                    }
                }
                mAdapterRelatedVideoList.notifyDataSetChanged();
                refreshVideoTitleDescription();
                mVideoListUrls.add(0, mSelectedVideoListModel.getUrl());*//*

            }
        });*/

    }

    private void refreshRelatedList(int videoPosition) {
        mSelectedVideoListModel = mVideoList.get(videoPosition);
        mSelectedVideoID = mSelectedVideoListModel.getId();
        mAdapterRelatedVideoList.updateSelectedVideo(mSelectedVideoID);
        mAdapterRelatedVideoList.notifyDataSetChanged();
        refreshVideoTitleDescription();
    }

    //endregion

    //region Init VideoList RecyclerView
    private void initVideoListRecyclerView() {
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        mAdapterRelatedVideoList = new AdapterRelatedVideoList(this, mVideoList, mSelectedVideoID);
        rvVideoList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        rvVideoList.setAdapter(mAdapterRelatedVideoList);
    }
    //endregion

    //region Init Video Container
    private void refreshVideoTitleDescription() {
        txtItemTitle.setText(mSelectedVideoListModel.getTitle());
        txtItemDescription.setText(mSelectedVideoListModel.getDescription());
    }
    //endregion


    @Override
    public void onBackPressed() {
        gotoVideoListActivity();
    }

    private void gotoVideoListActivity() {

        setResult(RESULT_OK);
        finish();
       /* Intent videoList = new Intent(this, VideoListActivity.class);
        videoList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(videoList);
        finish();*/
    }

    //region Init ExoPlayer

    private void changeScreenOrientation() {
        int orientation = VideoDetailActivity.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_enter);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

            Log.d(TAG, "initializePlayer: mSelectedVideoPOS == " + mSelectedVideoPOS);

            player = ExoPlayerFactory.newSimpleInstance(this,
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            playerView.setPlayer(player);
            playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
            player.setPlayWhenReady(false);
            //       player.seekTo(3, playbackPosition);
            // player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.addListener(new Player.DefaultEventListener() {
                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    super.onTracksChanged(trackGroups, trackSelections);
                    //   Log.d(TAG, "onTracksChanged: == getCurrentWindowIndex() == " + player.getCurrentWindowIndex());
                    //int windowIndex = mSelectedVideoPOS
                    restorePlayBackPostion(player.getCurrentWindowIndex());
                    refreshRelatedList(player.getCurrentWindowIndex());
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_READY) {
                        playerView.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
                    }
                }
            });

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getResources().getString(R.string.app_name)));
     /*       SimpleCache mSimpleCache = new SimpleCache(getCacheFolder(this, "exo1"), new NoOpCacheEvictor());
            CacheDataSourceFactory cacheFactory = new CacheDataSourceFactory(mSimpleCache, new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, getResources().getString(R.string.app_name))));*/

            MediaSource[] mediaSources = new MediaSource[mVideoListUrls.size()];
            for (int i = 0; i < mVideoListUrls.size(); i++) {
                mediaSources[i] = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(mVideoListUrls.get(i)));
            }

            MediaSource videoSource =
                    mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
            player.prepare(videoSource);
            player.seekTo(mSelectedVideoPOS, playbackPosition);
            player.setPlayWhenReady(true);

        }
    }

    File getCacheFolder(Context context, String name) {
        //return File(context.getExternalFilesDir(null), name);//ExoDownloads
        return new File(context.getCacheDir(), name);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            storeDataInFireStore(0, currentWindow, playbackPosition);
            player.release();
            player = null;
        }
    }
    //endregion


    public void storeDataInFireStore(int callFrom, int currentVideoWindow, long playbackVideoPosition) {

        final String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Map<String, Object> userMap = new HashMap<>();
        //  userMap.put("selected_video_position", currentVideoWindow);
        userMap.put("playbackPosition", playbackVideoPosition);

        mFirestore.collection("Users").document(user_id)
                .collection("Videos").document(String.valueOf(currentVideoWindow))
                .set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (callFrom) {
                    case EXO_NEXT_CLICK:
                        playerView.getPlayer().next();
                        break;
                    case EXO_PREVIOUS_CLICK:
                        playerView.getPlayer().previous();
                        break;
                    default:
                        break;
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VideoDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void restorePlayBackPostion(int videoPosition) {
        mFirestore.collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("Videos").document(String.valueOf(videoPosition))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                playbackPosition =  (documentSnapshot.getLong("playbackPosition") == null) ? 0 : documentSnapshot.getLong("playbackPosition");
                if(player!=null)
                player.seekTo(videoPosition, playbackPosition);
            }
        });
    }


}
