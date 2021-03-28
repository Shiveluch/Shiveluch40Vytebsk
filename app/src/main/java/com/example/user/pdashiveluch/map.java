package com.example.user.pdashiveluch;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class map extends AppCompatActivity implements SensorEventListener {
    Button close_but, sattopo, memory,add_mem;
    ListView mem_list;
    EditText entermem;
int marker=0;
    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;
    ArrayAdapter<String> adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.map_rad);
        entermem=findViewById(R.id.enter_mem);
        memory=findViewById(R.id.memories);
        add_mem=findViewById(R.id.add_mem);
        mem_list=findViewById(R.id.listmem);
        close_but=findViewById(R.id.close_map);
        sattopo=findViewById(R.id.view);
        image=findViewById(R.id.compass);

        adapter = new ArrayAdapter<String>(this, R.layout.mem_element, R.id.mem_el_text,pda.memories);
        // Привяжем массив через адаптер к ListView
        mem_list.setAdapter(adapter);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mem_list.getVisibility()==View.GONE)
                {
                    mem_list.setVisibility(View.VISIBLE);

                }
                else
                {
                    mem_list.setVisibility(View.GONE);
                }
            }
        });

add_mem.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (entermem.getVisibility()==View.GONE)
        {
            entermem.setVisibility(View.VISIBLE);

        }
        else
        {
            if (entermem.getText().toString()!=null)
            {
                pda.memories.add(entermem.getText().toString());
                entermem.setText("");
                adapter.notifyDataSetChanged();
            }
            entermem.setVisibility(View.GONE);

        }

    }
});


        close_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sattopo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               switch (marker)
               {
                   case 1:
                       sattopo.setText("Спутниковый вид");
                       photoView.setImageResource(R.drawable.map_rad_topo);
                       marker=0;
                       break;
                   case 0:
                       sattopo.setText("Топографический вид");
                       photoView.setImageResource(R.drawable.map_rad);
                       marker=1;
                       break;

               }


            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

          // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
