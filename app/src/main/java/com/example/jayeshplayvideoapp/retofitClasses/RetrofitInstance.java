package com.example.jayeshplayvideoapp.retofitClasses;

import com.example.jayeshplayvideoapp.utils.APIClass;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {


    private static Retrofit mRetrofit = null;
    private static OkHttpClient mHttpClient = null;
    private static HttpLoggingInterceptor mHttpLoggingInterceptor = null;

    private RetrofitInstance() {
    }

    /**
     * This method will create retrofit singleton object
     *
     * @return
     */
    public static Retrofit getRetrofitInstance() {

        if (mRetrofit == null) {

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(APIClass.BASE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return mRetrofit;
    }

    private static OkHttpClient getHttpClient() {

        if (mHttpClient == null) {

            mHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(getHttpLoggingInterceptor()).build();
        }

        return mHttpClient;
    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {

        if (mHttpLoggingInterceptor == null) {

            mHttpLoggingInterceptor = new HttpLoggingInterceptor();
            mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        }

        return mHttpLoggingInterceptor;
    }


}
