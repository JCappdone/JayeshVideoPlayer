package com.example.jayeshplayvideoapp.videoDetail;

import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.google.android.exoplayer2.source.MediaSource;
import java.util.ArrayList;

public interface VideoDetailView {

    void updateRelatedList(String selectedVideoID);

    void initVideoListRecyclerView(ArrayList<VideoListModel> videoList,
        String mSelectedVideoID);

    void changeScreenOrientation();

    void seekPlayerVideo(int videoPosition, long playbackPosition);

    void refreshVideoTitleDescription(String title, String description);

    void prepareExoPlayer(MediaSource videoSource, int mSelectedVideoPos);
}
