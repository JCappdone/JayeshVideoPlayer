package com.example.jayeshplayvideoapp.retofitClasses;


import com.example.jayeshplayvideoapp.models.VideoListModel;
import com.example.jayeshplayvideoapp.utils.APIClass;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET(APIClass.VIDEO_LIST)
    Call<List<VideoListModel>> getVideoList();

}
