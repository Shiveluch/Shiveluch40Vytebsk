package com.example.user.pdashiveluch.classes;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.example.user.pdashiveluch.pda;

public class ServiceConnectionExtender implements ServiceConnection {
    public pda activity;

    public ServiceConnectionExtender(pda activity){
        this.activity=activity;
    }


    public void onServiceConnected(ComponentName name, IBinder service) {

    }


    public void onServiceDisconnected(ComponentName name) {

    }
}
