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
import com.example.jayeshplayvideoapp.callbacks.VideoListItemClickCallback;
import com.example.jayeshplayvideoapp.models.VideoListModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterVideoList extends RecyclerView.Adapter<AdapterVideoList.VideoListViewHolder> {

    //Custom Objects
    private Context mContext;
    private VideoListItemClickCallback mVideoListItemClickCallback;

    //Collections
    private List<VideoListModel> mVideoList;

    public void setVideoListItemClickCallback(VideoListItemClickCallback videoListItemClickCallback) {
        mVideoListItemClickCallback = videoListItemClickCallback;
    }

    //-------------------------------------- Constructor -------------------------------------------
    public AdapterVideoList(Context context, List<VideoListModel> videoList) {
        mContext = context;
        mVideoList = videoList;
    }


    //-------------------------------View Layout---------------------------------------------------
    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);

        return new VideoListViewHolder(view);
    }


    //-------------------------------Data Binding-------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {

        holder.txtItemTitle.setText(mVideoList.get(position).getTitle());
        holder.txtItemDescription.setText(mVideoList.get(position).getDescription());
        Glide.with(mContext)
                .load(mVideoList.get(position).getThumb())
                .into(holder.imgItemCover);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mVideoListItemClickCallback!=null)
                mVideoListItemClickCallback.onVideoListItemClickListner(position);
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
        @BindView(R.id.cvItemVideo)
        CardView cvItemVideo;
        @BindView(R.id.llItemVideoContainer)
        LinearLayout llItemVideoContainer;
        View mView;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;

        }
    }
}
