package com.example.jayeshplayvideoapp.retofitClasses;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ApiController {

    private static ApiInterface mApiInterface = null;

    private ApiController() {
    }

    /**
     * Initialize Retrofit Call
     */

    public static ApiInterface getAPIInterface() {

        if (mApiInterface == null) {
            mApiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
        }

        return mApiInterface;
    }

    public static RequestBody getRequestBody(String data) {
        return RequestBody.create((MediaType.parse("text/plain")), data);
    }

}
