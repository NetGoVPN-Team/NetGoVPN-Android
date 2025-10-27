package com.netgovpn.freedom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities nc = cm.getNetworkCapabilities(network);
            if (nc == null) return false;

            boolean hasTransport = nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

            boolean hasInternetCapability = nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            boolean isValidated = nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

            return hasTransport && hasInternetCapability && isValidated;
        } else {
            try {
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            } catch (Exception e) {
                return false;
            }
        }
    }
}
