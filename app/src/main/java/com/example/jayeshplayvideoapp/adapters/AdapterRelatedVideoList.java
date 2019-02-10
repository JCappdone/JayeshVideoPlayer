package com.example.jayeshplayvideoapp.adapters;

/**
 * ------------This AdapterAnnouncementRoleList Use to display roles in view details screen---------
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jayeshplayvideoapp.R;
import com.example.jayeshplayvideoapp.activities.VideoDetailActivity;
import com.example.jayeshplayvideoapp.models.VideoListModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_ID;
import static com.example.jayeshplayvideoapp.utils.Constants.SELECTED_VIDEO_POS;
import static com.example.jayeshplayvideoapp.utils.Constants.VIDEO_LIST;

public class AdapterRelatedVideoList extends RecyclerView.Adapter<AdapterRelatedVideoList.VideoListViewHolder> {

    //Custom Objects
    private Context mContext;

    //Collections
    private List<VideoListModel> mVideoList;

    //dataType and variables
    private String mSelectedVideoId = "";

    //-------------------------------------- Constructor -------------------------------------------
    public AdapterRelatedVideoList(Context context, List<VideoListModel> videoList, String selectedVideoId) {
        mContext = context;
        mVideoList = videoList;
        mSelectedVideoId = selectedVideoId;
    }


    //-------------------------------View Layout---------------------------------------------------
    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_related_video, parent, false);

        return new VideoListViewHolder(view);
    }


    public void updateSelectedVideo(String selectedVideoId){
        mSelectedVideoId = selectedVideoId;
    }

    //-------------------------------Data Binding-------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {

        if (mVideoList.get(position).getId().equals(mSelectedVideoId)) {
            holder.llItemVideoContainer.setVisibility(View.GONE);
        } else {
            holder.llItemVideoContainer.setVisibility(View.VISIBLE);
        }
        holder.txtItemTitle.setText(mVideoList.get(position).getTitle());
        holder.txtItemDescription.setText(mVideoList.get(position).getDescription());
        Glide.with(mContext)
                .load(mVideoList.get(position).getThumb())
                .into(holder.imgItemCover);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoVideoDetailActivity(mContext,
                        (ArrayList<VideoListModel>) mVideoList,
                        mVideoList.get(position).getId(),position);
            }
        });

    }

    //-----------------------------Item Counts-----------------------------------------------------
    @Override
    public int getItemCount() {
        return mVideoList.size();
    }


    //---------------------------------------View Holder--------------------------------------------
    public class VideoListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgItemCover)
        ImageView imgItemCover;
        @BindView(R.id.txtItemTitle)
        TextView txtItemTitle;
        @BindView(R.id.txtItemDescription)
        TextView txtItemDescription;
        @BindView(R.id.llTitleContainer)
        LinearLayout llTitleContainer;
        @BindView(R.id.llItemVideoContainer)
        LinearLayout llItemVideoContainer;
        View mView;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;

        }
    }

    private void gotoVideoDetailActivity(Context context,
                                         ArrayList<VideoListModel> videoList,
                                         String selectedVideoId,int pos) {
        Intent videoDetail = new Intent(context, VideoDetailActivity.class);
        videoDetail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        videoDetail.putParcelableArrayListExtra(VIDEO_LIST, videoList);
        videoDetail.putExtra(SELECTED_VIDEO_ID, selectedVideoId);
        videoDetail.putExtra(SELECTED_VIDEO_POS, pos);
        context.startActivity(videoDetail);
        ((Activity) context).finish();
    }
}
