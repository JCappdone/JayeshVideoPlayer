package com.example.jayeshplayvideoapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayeshplayvideoapp.R;
import com.example.jayeshplayvideoapp.adapters.AdapterVideoList;
import com.example.jayeshplayvideoapp.callbacks.VideoListItemClickCallback;
import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.example.jayeshplayvideoapp.retofitClasses.ApiController;
import com.example.jayeshplayvideoapp.utils.CommonUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.jayeshplayvideoapp.utils.Constants.DETAIL_REQUEST_CODE;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_ID;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_POS;
import static com.example.jayeshplayvideoapp.utils.Constants.VIDEO_LIST;

public class VideoListActivity extends BaseActivity {

    //declaration
    private static final String TAG = VideoListActivity.class.getSimpleName();

    //views
    @BindView(R.id.txtVideoNotFound)
    TextView txtVideoNotFound;
    @BindView(R.id.rvVideoList)
    RecyclerView rvVideoList;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    //Custom Objects
    private AdapterVideoList mAdapterVideoList;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    //Collections
    private ArrayList<VideoListModel> mVideoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        initialization();
    }

    private void initialization() {
        ButterKnife.bind(this);
        initSignOut();
        initToolbar();
        initVideoListRecyclerView();
        initSwipeRefresh();
        callWebserviceVideoList();


    }

    //region SwipeToRefresh
    private void initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callWebserviceVideoList();
            }
        });
    }
    //endregion

    //region InitSignOut
    private void initSignOut() {
        mAuth = FirebaseAuth.getInstance();
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]}
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    //endregion

    //region InitToolbar
    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Video List");
    }
    //endregion

    //region initMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSignOut:
                signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Init VideoList RecyclerView
    private void initVideoListRecyclerView() {
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        mAdapterVideoList = new AdapterVideoList(this, mVideoList);
        mAdapterVideoList.setVideoListItemClickCallback(new VideoListItemClickCallback() {
            @Override
            public void onVideoListItemClickListner(int position) {
                gotoVideoDetailActivity(mVideoList.get(position).getId(),position);
            }
        });
        rvVideoList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        rvVideoList.setAdapter(mAdapterVideoList);

    }
    //endregion

    //region CallWebservices
    private void callWebserviceVideoList() {

        if (!CommonUtil.isInternetAvailabel(mainLayout, this)) {
            return;
        }

        showWebServiceProgressDialog();

        Call<List<VideoListModel>> videoListModelCall = ApiController
                .getAPIInterface()
                .getVideoList();

        videoListModelCall.enqueue(new Callback<List<VideoListModel>>() {
            @Override
            public void onResponse(Call<List<VideoListModel>> call, Response<List<VideoListModel>> response) {

                hideWebServiceProgressDialog();

                if (response.body() != null) {
                    mVideoList.clear();
                    mVideoList.addAll(response.body());
                }

                showNoVideoFoundTag(mVideoList);
                mAdapterVideoList.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<VideoListModel>> call, Throwable t) {
                hideWebServiceProgressDialog();
                t.printStackTrace();
            }
        });
    }
    //endregion

    //region show web-service progress dialog
    private void showWebServiceProgressDialog() {
        if (swipeRefresh.isRefreshing()) {
            hideProgressDialog();
        } else {
            showProgressDialog();
        }
    }
    //endregion

    // region hide web-service progress dialog
    private void hideWebServiceProgressDialog() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            hideProgressDialog();
        }
    }
    //endregion

    //region show no video found tag
    private void showNoVideoFoundTag(List<VideoListModel> videoList) {
        if (videoList.isEmpty()) {
            txtVideoNotFound.setVisibility(View.VISIBLE);
            rvVideoList.setVisibility(View.GONE);
        } else {
            txtVideoNotFound.setVisibility(View.GONE);
            rvVideoList.setVisibility(View.VISIBLE);
        }
    }
    //endregion

    //region signOut
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gotoGoogleSignInActivity();
                    }
                });
    }
    //end region

    private void gotoGoogleSignInActivity() {
        Intent videoList = new Intent(this, GoogleSignInActivity.class);
        videoList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(videoList);
        finish();
    }

    private void gotoVideoDetailActivity(String selectedVideoId,int position) {
        Intent videoDetail = new Intent(this, VideoDetailActivity.class);
        videoDetail.putParcelableArrayListExtra(VIDEO_LIST, mVideoList);
        videoDetail.putExtra(SELECTED_VIDEO_ID, selectedVideoId);
        videoDetail.putExtra(SELECTED_VIDEO_POS, position);
        startActivityForResult(videoDetail,DETAIL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case DETAIL_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {

                }
                break;
            }
        }

    }
}
