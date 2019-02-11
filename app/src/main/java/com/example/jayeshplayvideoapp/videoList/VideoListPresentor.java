package com.example.jayeshplayvideoapp.videoList;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.example.jayeshplayvideoapp.retofitClasses.ApiController;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListPresentor {

    private static final String TAG = "VideoListPresentor";
    private VideoListView mView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    public VideoListPresentor(VideoListView videoListView) {
        mView = videoListView;
    }

    public void initFireBaseConfig(FirebaseAuth auth,GoogleSignInClient googleSignInClient){
        mAuth = auth;
        mGoogleSignInClient = googleSignInClient;
    }


    public void fetchVideoList(){

        mView.showProgress();

        Call<List<VideoListModel>> videoListModelCall = ApiController
            .getAPIInterface()
            .getVideoList();

        videoListModelCall.enqueue(new Callback<List<VideoListModel>>() {
            @Override
            public void onResponse(Call<List<VideoListModel>> call, Response<List<VideoListModel>> response) {
                Log.d(TAG, "==== onResponse: ");
                mView.hideProgress();
                ArrayList<VideoListModel> mVideoList = new ArrayList<>();
                if (response.body() != null) {
                    mVideoList.clear();
                    mVideoList.addAll(response.body());
                    mView.showList(mVideoList);
                }else{
                    mView.showNOVideoFoundError();
                }
            }

            @Override
            public void onFailure(Call<List<VideoListModel>> call, Throwable t) {
                mView.hideProgress();
                Log.d(TAG, "==== onFailure: ");
                mView.showError("Some error occurred");
                t.printStackTrace();
            }
        });
    }

    public void onVideoItemClicked(int position,
        VideoListModel videoListModel) {
        mView.goToVideoActivity(position,videoListModel);
    }

    public void makeSignOut() {
        if (mAuth == null) {
            return;
        }
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener((Activity) mView,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mView.goToLoginActivity();
                }
            });
    }
}
