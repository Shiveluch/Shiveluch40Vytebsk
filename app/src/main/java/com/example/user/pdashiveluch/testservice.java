package com.example.user.pdashiveluch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class testservice extends Service {
    public testservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
