package com.example.user.pdashiveluch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.user.pdashiveluch.classes.DataPack;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class gps_activity extends AppCompatActivity implements OnMapReadyCallback, LocListenerInterface {
    private GoogleMap mMap;
    ImageView showme, mapcenter, markergeo,mapcancel, arrow;
    EditText title;
    Location location;
    LocationManager locationManager;
    DataPack dataPack;
    MyLocListener myLocListener;
    boolean setMarkers=false;
    Criteria criteria;
    Context context;
    double lat, lon;
    Marker stalker = null;
    Marker currentMarker;
    public LatLngBounds bounds;
    public LatLng center;
    float rotate = -26.47934913635254f;
    ArrayList markers;
    ArrayList markersname;
    Marker[] marker=new Marker[10];
    String[] MarkersList=new String[10];
    public static final String APP_PREFERENCES = "markers";
    public static final String APP_PREFERENCES_MARKER1 = "MARKER1";
    public static final String APP_PREFERENCES_MARKER2 = "MARKER2";
    public static final String APP_PREFERENCES_MARKER3 = "MARKER3";
    public static final String APP_PREFERENCES_MARKER4 = "MARKER4";
    public static final String APP_PREFERENCES_MARKER5 = "MARKER5";
    public static final String APP_PREFERENCES_MARKER6 = "MARKER6";
    public static final String APP_PREFERENCES_MARKER7 = "MARKER7";
    public static final String APP_PREFERENCES_MARKER8 = "MARKER8";
    public static final String APP_PREFERENCES_MARKER9 = "MARKER10";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        showme = findViewById(R.id.showme);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        criteria = new Criteria();
        context = getApplicationContext();
        mapcenter=findViewById(R.id.mapcenter);
        markergeo=findViewById(R.id.markerGeo);
        markergeo.setImageResource(R.drawable.marker_white);
        mapcancel=findViewById(R.id.mapcancel);
        markers=new ArrayList();
        markersname=new ArrayList();

        title=findViewById(R.id.markerTitle);
        title.setVisibility(View.GONE);

        mapcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag="snippetSteps";
                Log.d(tag,"start snipping "+ currentMarker);
                ArrayList convert=new ArrayList();
                int size=markers.size();

                for (int i=0;i<size;i++)
                {
                    String[] currentmarker= ((String) markers.get(i)).split(":");
                    String outer=currentmarker[0]+":"+currentmarker[1]+":"+currentmarker[2]+":Point "+(i+1);
                    convert.add(outer);
                    Log.d ("previousMarker", currentmarker[3]);
                    Log.d ("newMarker", ""+convert.get(i));

                }
                markers.clear();
                for (int i=0;i<size;i++)
                {
                    String outer=(String)convert.get(i);

                    markers.add(outer);
                }

                if (currentMarker!=null) {
                    String snip=currentMarker.getSnippet();
                    Log.d(tag,"Find snippet: "+ snip);
                    for (int i=0;i<markers.size();i++)
                    {
                        String markersnip= (String) markers.get(i);
                        if (markersnip.contains(snip)) {markers.remove(i);
                        Log.d(tag,"Deleting mark "+markersnip + ", position: "+i+", snippet: "+currentMarker.getSnippet());}
                    }
                    String [] temparray=new String[markers.size()];
                    for (int i=0;i<markers.size();i++)
                        temparray[i]= (String) markers.get(i);
                   Log.d("markersSize",""+markers.size());
                   saveArray(temparray,"sharedmarkers",getApplicationContext());

                }

                mMap.clear();
                initOverlay();
                getmarkers();
            }
        });

        markergeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkers=!setMarkers;
                if (setMarkers) {markergeo.setImageResource(R.drawable.marker_green); title.setVisibility(View.VISIBLE);}
                if (!setMarkers) {markergeo.setImageResource(R.drawable.marker_white); title.setVisibility(View.GONE);}
            }
        });
        mapcenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                initOverlay();
                LatLng position=new LatLng(60.674103, 29.167156);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(position)
                        .bearing(rotate)
                        .zoom(15)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(cameraUpdate);

                getmarkers();

            }
        });



        showme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
Log.d("PERMIS","no permissions");
                    return;
                }
                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                if (location != null) {

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //   button3.setText("" + latLng.latitude + "," + latLng.longitude);
                    lat = latLng.latitude;
                    lon = latLng.longitude;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

                    if (stalker!=null) {
                        stalker.remove();
                        stalker=null;
                    }

                    if (stalker==null) {
                        stalker = mMap.addMarker(new MarkerOptions().position(latLng).
                                icon(getBitmapHighDescriptor(R.drawable.stalkermarker)));
                    }

                }

              // double lat=dataPack.latitude;
                Log.d("maps",""+lat+", "+lon);

            }
        });
    }

    private BitmapDescriptor getBitmapHighDescriptor(int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), id);
        vectorDrawable.setBounds(0, 0, 50, 90);
        Bitmap bm = Bitmap.createBitmap(50, 90, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    private BitmapDescriptor getBitmapBigDescriptor(int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), id);
        vectorDrawable.setBounds(0, 0, 50, 90);
        Bitmap bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng swLatLng = new LatLng(55.3717489, 36.77321);
        LatLng neLatLng = new LatLng(55.3758195, 36.78338);
        initOverlay();
        getmarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker=marker;
                Log.d("snipping","OnMarkerSelect "+marker.getSnippet());
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (setMarkers) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.snippet("Point "+(markers.size()+1));
                    markerOptions.title(title.getText().toString());
                    int mLength = markers.size();
                    String addict = title.getText().toString() + ":" + latLng.latitude + ":" + latLng.longitude + ":"+"Point "+markers.size();
                    markers.add(addict);
                    String [] temparray=new String[markers.size()];
                    for (int i=0;i<markers.size();i++)
                        temparray[i]= (String) markers.get(i);
                    mMap.addMarker(markerOptions);
                    Log.d("markersSize",""+markers.size());
                    saveArray(temparray,"sharedmarkers",getApplicationContext());
                    setMarkers=false;
                    markergeo.setImageResource(R.drawable.marker_white);
                    title.setText("");
                    title.setVisibility(View.GONE);
                }
//                Marker [] marker=new Marker[markers.size()];
//                for (int i=0;i<markers.size();i++)
//                {
//                    String mark= (String) markers.get(i);
//                    String[] actualMarkers = mark.split(":");
//                    Log.d("markers","MarkerName: "+actualMarkers[0]);
//                    Log.d("markers","MarkerLat: "+actualMarkers[1]);
//                    Log.d("markers","MarkerLon: "+actualMarkers[2]);
//                    mMap.addMarker(markerOptions);
//
//                }




            }
        });


//        bounds= new LatLngBounds(swMapCoord, neMapCoord);
//        center = bounds.getCenter();
//
//        BitmapDescriptor bitmapDescriptor;
//
//
//        GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
//                .image(R.drawable.map_rad_topo)
//                .positionFromBounds(bounds)
//                .transparency(0.5f);
//        // Updated
//        mGroundOverlayOptions = groundOverlay;
//        mMap.addGroundOverlay(groundOverlay);

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void OnLocationChanged(Location loc) {

    }

    private void initOverlay() {
double north = 60.69108846344146;
		double south = 60.65948017203308;
		double east = 29.18230118538045;
		double west = 29.15043820106731;


        LatLng swMapCoord=new LatLng(south,west);
        LatLng neMapCoord=new LatLng(north,east);
        LatLngBounds bounds=new LatLngBounds(swMapCoord, neMapCoord);


        GroundOverlayOptions poligon = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map_rad_topo))
                .positionFromBounds(bounds)
                .bearing(rotate);
        if (poligon!=null)
        mMap.addGroundOverlay(poligon);
    }

    public boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("markers", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public String[] loadMarkers(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("markers", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    private void getmarkers()
    {
        String [] temparray=new String[loadMarkers("sharedmarkers",getApplicationContext()).length];
        markers.clear();
        for (int i=0;i< loadMarkers("sharedmarkers",getApplicationContext()).length;i++)
        {
            temparray[i]=loadMarkers("sharedmarkers",getApplicationContext())[i];

            markers.add(temparray[i]);
            Log.d("Getmarkers",temparray[i]);
            String[] actualMarkers = temparray[i].split(":");
            Log.d("markersSplit","MarkerName: "+actualMarkers[0]);
            Log.d("markersSplit","MarkerLat: "+actualMarkers[1]);
            Log.d("markersSplit","MarkerLon: "+actualMarkers[2]);
            LatLng latLng=new LatLng(Double.parseDouble(actualMarkers[1]),Double.parseDouble(actualMarkers[2]));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(actualMarkers[0]);
            markerOptions.snippet("Point "+markers.size());
            mMap.addMarker(markerOptions);

        }

    }
}


