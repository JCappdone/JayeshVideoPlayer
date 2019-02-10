package com.example.jayeshplayvideoapp.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.example.jayeshplayvideoapp.R;
import com.google.android.material.snackbar.Snackbar;

public class CommonUtil {

    public static boolean isInternetAvailabel(View view,Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        Snackbar.make(view,context.getResources().getString(R.string.internet_not_available), Snackbar.LENGTH_SHORT).show();

        Toast.makeText(context, context.getResources().getString(R.string.internet_not_available), Toast.LENGTH_SHORT).show();
        return false;
    }

}