package com.example.jayeshplayvideoapp.videoList;

import static com.example.jayeshplayvideoapp.utils.Constants.DETAIL_REQUEST_CODE;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_ID;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_POS;
import static com.example.jayeshplayvideoapp.utils.Constants.VIDEO_LIST;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.jayeshplayvideoapp.R;
import com.example.jayeshplayvideoapp.activities.BaseActivity;
import com.example.jayeshplayvideoapp.activities.GoogleSignInActivity;
import com.example.jayeshplayvideoapp.adapters.AdapterVideoList;
import com.example.jayeshplayvideoapp.callbacks.VideoListItemClickCallback;
import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.example.jayeshplayvideoapp.videoDetail.VideoDetailActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Objects;

public class VideoListActivity extends BaseActivity implements VideoListView{

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

    //Collections
    private ArrayList<VideoListModel> mVideoList = new ArrayList<>();

    private VideoListPresentor mPresentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        ButterKnife.bind(this);

        mPresentor = new VideoListPresentor(this);
        initFireBaseConfig();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Video List");

        initVideoListRecyclerView();

        swipeRefresh.setOnRefreshListener(() -> {
            mPresentor.fetchVideoList();
        });
        mPresentor.fetchVideoList();

    }

    private void initFireBaseConfig() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mPresentor.initFireBaseConfig(mAuth,mGoogleSignInClient );
    }

    //region Init VideoList RecyclerView
    private void initVideoListRecyclerView() {
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        mAdapterVideoList = new AdapterVideoList(this, mVideoList);
        mAdapterVideoList.setVideoListItemClickCallback(new VideoListItemClickCallback() {
            @Override
            public void onVideoListItemClickListner(int position) {
                mPresentor.onVideoItemClicked(position,mVideoList.get(position));
            }
        });
        rvVideoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvVideoList.setAdapter(mAdapterVideoList);
    }

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
                mPresentor.makeSignOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showProgress() {
        if (swipeRefresh.isRefreshing()) {
            hideProgressDialog();
        } else {
            showProgressDialog();
        }
    }

    @Override
    public void hideProgress() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            hideProgressDialog();
        }
    }

    @Override
    public void showError(String some_error_occured) {

    }
    @Override
    public void showNOVideoFoundError() {
        txtVideoNotFound.setVisibility(View.VISIBLE);
        rvVideoList.setVisibility(View.GONE);
    }

    @Override
    public void showList(ArrayList<VideoListModel> videoList) {
        txtVideoNotFound.setVisibility(View.GONE);
        rvVideoList.setVisibility(View.VISIBLE);

        mVideoList.clear();
        mVideoList.addAll(videoList);
        mAdapterVideoList.notifyDataSetChanged();
    }

    @Override
    public void goToLoginActivity() {
        Intent videoList = new Intent(this, GoogleSignInActivity.class);
        videoList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(videoList);
        finish();
    }

    @Override
    public void goToVideoActivity(int position, VideoListModel videoListModel) {
        Intent videoDetail = new Intent(this, VideoDetailActivity.class);
        videoDetail.putParcelableArrayListExtra(VIDEO_LIST, mVideoList);
        videoDetail.putExtra(SELECTED_VIDEO_ID, videoListModel.getId());
        videoDetail.putExtra(SELECTED_VIDEO_POS, position);
        startActivityForResult(videoDetail,DETAIL_REQUEST_CODE);
    }
}
