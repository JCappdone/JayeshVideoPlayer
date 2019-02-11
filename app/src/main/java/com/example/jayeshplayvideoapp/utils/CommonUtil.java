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

import java.net.InetAddress;

public class CommonUtil {

    public static float getPercentage(long n, long total) {
        float proportion = ((float) n) / ((float) total);
        return proportion * 100;
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}