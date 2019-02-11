package com.example.jayeshplayvideoapp.videoDetail;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.jayeshplayvideoapp.utils.CommonUtil.getPercentage;

public class VideoDetailPresentor {

    //custom objects
    private VideoDetailView mView;

    //fireBase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    //collections
    private ArrayList<VideoListModel> mVideoList = new ArrayList<>();

    //dataType and variables
    String mSelectedVideoID;
    int mSelectedVideoPos;

    //constructor
    public VideoDetailPresentor(VideoDetailView videoListView) {
        mView = videoListView;
    }


    public void initFireBaceConfig(FirebaseAuth auth, FirebaseFirestore firestore){
        mAuth = auth;
        mFirestore = firestore;
    }

    public void updateSelectedVideoId(String videoId){
        mSelectedVideoID = videoId;
    }
    public void updateVideoPos(int pos){
        mSelectedVideoPos = pos;
    }

    public void onFullScreenButtonClick() {
        mView.changeScreenOrientation();
    }

    public void restorePlayBackPostion(int videoPosition) {
        mFirestore.collection("Users")
            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
            .collection("Videos").document(String.valueOf(mVideoList.get(videoPosition).getId()))
            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long playbackPosition =  (documentSnapshot.getLong("playbackPosition") == null) ? 0 : documentSnapshot.getLong("playbackPosition");
                //if(player!=null)
                  //  player.seekTo(videoPosition, playbackPosition);
                mView.seekPlayerVideo(videoPosition,playbackPosition);
            }
        });
    }

    public void saveCurrentVideoDetail(int currentVideoWindow, long playbackVideoPosition,long videoDuration) {
        final String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Log.d(TAG, "saveCurrentVideoDetail: == " + videoDuration);
        Log.d(TAG, "saveCurrentVideoDetail: == playbackVideoPosition " + playbackVideoPosition);
        if(getPercentage(playbackVideoPosition,videoDuration) > 90 ){
            playbackVideoPosition = 0;
        }
        Map<String, Object> userMap = new HashMap<>();
        //  userMap.put("selected_video_position", currentVideoWindow);
        userMap.put("playbackPosition", playbackVideoPosition);

        mFirestore.collection("Users").document(user_id)
            .collection("Videos").document(String.valueOf(mVideoList.get(currentVideoWindow).getId()))
            .set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(VideoDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initData(String selectedVideoID, int selectedVideoPOS,
        ArrayList<VideoListModel> videoList) {
        mSelectedVideoID = selectedVideoID;
        mSelectedVideoPos = selectedVideoPOS;
        mVideoList = videoList;

        mView.refreshVideoTitleDescription(mVideoList.get(mSelectedVideoPos).getTitle(),mVideoList.get(mSelectedVideoPos).getDescription());
        mView.initVideoListRecyclerView(mVideoList,mSelectedVideoID);
        mView.updateRelatedList(mSelectedVideoID);
    }

    public void onTrackChange(int currentWindowIndex) {

        mSelectedVideoID = mVideoList.get(mSelectedVideoPos).getId();
        mSelectedVideoPos = currentWindowIndex;

        mView.updateRelatedList(mSelectedVideoID);
        restorePlayBackPostion(currentWindowIndex);
        mView.refreshVideoTitleDescription(mVideoList.get(currentWindowIndex).getTitle(),mVideoList.get(currentWindowIndex).getDescription());
    }
    public void initTrackChange() {
        onTrackChange(mSelectedVideoPos);
    }

    public void initPlayerTracks(
        Factory dataSourceFactory) {
        MediaSource[] mediaSources = new MediaSource[mVideoList.size()];
        for (int i = 0; i < mVideoList.size(); i++) {
            mediaSources[i] = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mVideoList.get(i).getUrl()));
        }
        MediaSource videoSource =
            mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
        mView.prepareExoPlayer(videoSource,mSelectedVideoPos);
    }
}
