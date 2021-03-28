package com.example.user.pdashiveluch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class scan extends AppCompatActivity {
    Button button,close;
    ListView scanlist;
    ArrayList<String> sal=new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    char symb;
    BluetoothAdapter BT=BluetoothAdapter.getDefaultAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        button = findViewById(R.id.scan);
        close=findViewById(R.id.close);
        scanlist=findViewById(R.id.scallist);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.clear();
                sal.clear();
                BT.startDiscovery();

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(BR,intentFilter);

        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,sal);
        scanlist.setAdapter(arrayAdapter);


    }


    BroadcastReceiver BR;

    {
        BR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String Action = intent.getAction();
                arrayAdapter.clear();
                sal.clear();
                if (BluetoothDevice.ACTION_FOUND.equals(Action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String pars=device.getName();
                    if (pars.length()>1) {
                        symb = pars.charAt(0);
                        String typo = "" + symb;
                        if (typo.equals("0")||typo.equals("1")||typo.equals("3") || typo.equals("4")|| typo.equals("5")||typo.equals("6") )
                        {}
                        else  sal.add("Сталкер "+ device.getName());
                    }
                    scanlist.setAdapter(arrayAdapter);

                }
            }
        };
    }
}