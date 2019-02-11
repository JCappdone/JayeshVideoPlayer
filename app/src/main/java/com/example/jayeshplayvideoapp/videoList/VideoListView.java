package com.example.jayeshplayvideoapp.videoList;

import com.example.jayeshplayvideoapp.models.VideoListModel;
import java.util.ArrayList;

public interface VideoListView {

    void showProgress() ;

    void hideProgress() ;

    void showError(String some_error_occured);

    void showNOVideoFoundError();

    void showList(ArrayList<VideoListModel> mVideoList);

    void goToLoginActivity();

    void goToVideoActivity(int position,
        VideoListModel videoListModel);
}
