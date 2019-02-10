package com.example.jayeshplayvideoapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoListModel implements Parcelable {

    /**
     * description : Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.

     Licensed under the Creative Commons Attribution license
     http://www.bigbuckbunny.org
     * id : 1
     * thumb : http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg
     * title : Big Buck Bunny
     * url : http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
     */

    private String description;
    private String id;
    private String thumb;
    private String title;
    private String url;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.id);
        dest.writeString(this.thumb);
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public VideoListModel() {
    }

    protected VideoListModel(Parcel in) {
        this.description = in.readString();
        this.id = in.readString();
        this.thumb = in.readString();
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<VideoListModel> CREATOR = new Parcelable.Creator<VideoListModel>() {
        @Override
        public VideoListModel createFromParcel(Parcel source) {
            return new VideoListModel(source);
        }

        @Override
        public VideoListModel[] newArray(int size) {
            return new VideoListModel[size];
        }
    };
}
