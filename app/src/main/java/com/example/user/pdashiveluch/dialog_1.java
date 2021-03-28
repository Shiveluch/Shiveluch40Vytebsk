package com.example.user.pdashiveluch;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class dialog_1 extends DialogFragment implements OnClickListener {



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.dialog1, null);
        return v;
    }

    public void onClick(View v) {


    }

    public void onDismiss(DialogInterface dialog) {


    }

    public void onCancel(DialogInterface dialog) {


    }
}