package com.example.jayeshplayvideoapp.videoDetail;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jayeshplayvideoapp.R;
import com.example.jayeshplayvideoapp.adapters.AdapterRelatedVideoList;
import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.example.jayeshplayvideoapp.utils.CommonUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_ID;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_POS;
import static com.example.jayeshplayvideoapp.utils.Constants.VIDEO_LIST;

public class VideoDetailActivity extends AppCompatActivity implements VideoDetailView {

    //declaration
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


    ImageButton exoNext;
    ImageButton exoPrevious;
    ImageView fullscreenIcon;

    //custom objects
    private AdapterRelatedVideoList mAdapterRelatedVideoList;
    private SimpleExoPlayer player;
    VideoDetailPresentor mPresentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_detail);

      /*  if (!CommonUtil.isInternetAvailable()) {
            Snackbar.make(findViewById(R.id.mainContainer), getString(R.string.internet_not_available), Snackbar.LENGTH_SHORT).show();
            return;
        }*/

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        ButterKnife.bind(this);
        mPresentor = new VideoDetailPresentor(this);
        mPresentor.initFireBaceConfig(auth, firestore);

        initialization();
        setAttributes();
    }

    private void initialization() {
        View controlView = playerView.findViewById(R.id.exo_controller);
        fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_enter);
        exoNext = controlView.findViewById(R.id.exo_next);
        exoPrevious = controlView.findViewById(R.id.exo_prev);
        controlView.findViewById(R.id.exo_fullscreen_button)
                .setOnClickListener(v -> mPresentor.onFullScreenButtonClick());

        exoNext.setOnClickListener(view -> {
            Log.d(TAG, "======== onClick: NEXT");


            mPresentor.saveCurrentVideoDetail(player.getCurrentWindowIndex(), player.getCurrentPosition(), player.getDuration());
            if (player.getCurrentWindowIndex() < mAdapterRelatedVideoList.getItemCount() - 1) {
                mPresentor.onTrackChange(player.getNextWindowIndex());
                playerView.getPlayer().next();
            }

        });

        exoPrevious.setOnClickListener(view -> {
            Log.d(TAG, "======== onClick: exoPrevious");
            mPresentor.saveCurrentVideoDetail(player.getCurrentWindowIndex(), player.getCurrentPosition(), player.getDuration());
            if (player.getCurrentWindowIndex() > 0) {
                mPresentor.onTrackChange(player.getPreviousWindowIndex());
                playerView.getPlayer().previous();
            }

        });
    }

    private void setAttributes() {
        if (getIntent().hasExtra(VIDEO_LIST) && getIntent().hasExtra(SELECTED_VIDEO_ID)) {
            String mSelectedVideoID = getIntent().getStringExtra(SELECTED_VIDEO_ID);
            int mSelectedVideoPOS = getIntent().getIntExtra(SELECTED_VIDEO_POS, 0);
            ArrayList<VideoListModel> mVideoList = getIntent().getExtras().getParcelableArrayList(VIDEO_LIST);
            mPresentor.initData(mSelectedVideoID, mSelectedVideoPOS, mVideoList);
        }
    }

    @Override
    public void updateRelatedList(String selectedVideoID) {
        mAdapterRelatedVideoList.updateSelectedVideo(selectedVideoID);
        mAdapterRelatedVideoList.notifyDataSetChanged();
    }

    @Override
    public void refreshVideoTitleDescription(String title, String description) {
        txtItemTitle.setText(title);
        txtItemDescription.setText(description);
    }

    //region Init VideoList RecyclerView
    @Override
    public void initVideoListRecyclerView(ArrayList<VideoListModel> videoList, String mSelectedVideoID) {
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        mAdapterRelatedVideoList = new AdapterRelatedVideoList(this, videoList, mSelectedVideoID);
        rvVideoList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        rvVideoList.setAdapter(mAdapterRelatedVideoList);
    }
    //endregion

    @Override
    public void changeScreenOrientation() {
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
    public void onBackPressed() {
        int orientation = VideoDetailActivity.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_enter);
        } else {
            super.onBackPressed();
        }
    }


    //region exoplayer
    @Override
    public void seekPlayerVideo(int videoPosition, long playbackPosition) {
        if(player!=null)
        player.seekTo(videoPosition, playbackPosition);
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
            playerView.setPlayer(player);
            playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
            player.setPlayWhenReady(false);
            //       player.seekTo(3, playbackPosition);
            // player.setRepeatMode(Player.REPEAT_MODE_ALL);
            mPresentor.initTrackChange();
            player.addListener(new Player.DefaultEventListener() {
                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    super.onTracksChanged(trackGroups, trackSelections);
                    //   Log.d(TAG, "onTracksChanged: == getCurrentWindowIndex() == " + player.getCurrentWindowIndex());
                    //int windowIndex = mSelectedVideoPOS
                    mPresentor.onTrackChange(player.getCurrentWindowIndex());
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

            mPresentor.initPlayerTracks(dataSourceFactory);

        }
    }

    private void releasePlayer() {
        if (player != null) {
            mPresentor.saveCurrentVideoDetail(player.getCurrentWindowIndex(), player.getCurrentPosition(), player.getDuration());
            player.release();
            player = null;
        }
    }

    @Override
    public void prepareExoPlayer(MediaSource videoSource, int mSelectedVideoPos) {
        player.prepare(videoSource);
        player.seekTo(mSelectedVideoPos, 0);
        player.setPlayWhenReady(true);
    }
    //endregion

}
