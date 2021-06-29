package com.example.cinehub.connectivitymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {


    private NetworkListener listener;

    public void setupListener(NetworkListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            listener.networkChange();
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}
