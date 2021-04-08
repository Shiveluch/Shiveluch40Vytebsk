package com.example.user.pdashiveluch;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Toast;

import com.example.user.pdashiveluch.classes.DataPack;
import com.example.user.pdashiveluch.classes.Debug;
import com.example.user.pdashiveluch.classes.DeviceDescriptor;
import com.example.user.pdashiveluch.classes.PDA_AudioEvent;
import com.example.user.pdashiveluch.classes.PDA_AudioManager;
import com.example.user.pdashiveluch.classes.PDA_BlowoutEvent;
import com.example.user.pdashiveluch.classes.PDA_Event;
import com.example.user.pdashiveluch.classes.PDA_MessageEvent;
import com.example.user.pdashiveluch.classes.PlayerCharcteristics;
import com.example.user.pdashiveluch.classes.Places;
import com.example.user.pdashiveluch.classes.PsiSources;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class ShiveluchService extends Service implements LocListenerInterface {
    //public static final int Places_None=0,Places_Radiation=1, Places_Bar=2,Places_Tramplin=3,Places_Electra=4, Places_Jarka=5, Places_Psi=6, Places_Kisel=7,Places_Dolg=8,Places_Freedom=9, Places_Sci=10,Places_Monolit=11,Places_Controller=12,Places_Bandos=13,Places_Doctor=14,Places_Mercs=15,Places_MonolitPiece=16;
    //region Переменные

    public PlayerCharcteristics playerCharcteristics;
    //protected pda activity;
    public PDA_AudioManager myAudioManager;
    LocationManager locationManager;
    protected HashMap<Number, Group> groups;
    protected PDA_Event[] events;
    //region ***** РАБОТА С АНОМАЛИЯМИ И ЛОКАЦИЯМИ
    private Places place = Places.None; //Идентификатор места. Аномалия/локация
    private Long PlaceFoundTimeStamp;
    //endregion
    private MyLocListener myLocListener;
    boolean GpsStatus = false;
    Location location;
    Criteria criteria;
    Context context;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private boolean DiscoveryStarted;
    private int discoveryTimer;//периодичность опроса окружающей местности на предмет наличия блютусов
    int event_state = 0; //счетчик времени для внесения данных о текущей локации в историю (раз в 30 секунд)
    int event_sound = 0;
    private int progressanomaly = 0; //мощность аномалии для отражения на соотвествтующей шкале
    int timer = 5, loctime = 0;
    int rssi;
    private float kff; //Коэффициент для управления эффектом от аномалий в формулах

    private int psi_helm_work, zombi_status_time = 600, timer_osk = 0;//1. Время работы пси-шлма 2. Время зомбирования. 3. Счетчик времени для сброса флага повторного прослушивания
    private int MonPieceCouner = 0;
    private int AtmoSoundStatus = 0;
    private int AtmoSoundsCounter = 0;
    private int monstroboi_count = 10;
    public int localtimer = 3, bolt_work=0;
    public int blowoutflag = 0, controlerflag = 0, BlowoutSoundsTimer = 60, ControlerSoundTimer = 30;


    public int add_health = 0; //счетчик периодического добавления единицы здоровья
    boolean cont_sound = false; //флаг проигрыша звука атаки контролера
    public int mStreamId;
    public boolean stalker_start=false;

    double lat = 0.0, lon = 0.0;
    LatLng position=new LatLng(0,0);

    private final BluetoothAdapter myBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
    private final IBinder binder = new ShiveluchServiceBinder();
    //public String[] bt_base;
    String sbname;//имя скрипта, котоыйр должен отработать в JSON
    protected ShiveluchService service;
    final int REQUEST_ACCESS_COARSE_LOCATION = 1243534;
    float damage;


    //endregion

    //region getters
    public int getProgressanomaly() {
        return progressanomaly;
    }


    public Places getPlace() {
        return place;
    }

    @Override
    public void OnLocationChanged(Location loc) {
        if (location != null) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //   button3.setText("" + latLng.latitude + "," + latLng.longitude);
            lat = latLng.latitude;
            lon = latLng.longitude;
        }
    }
    //endregion


    public class ShiveluchServiceBinder extends Binder {
        ShiveluchService getService() {
            return ShiveluchService.this;
        }
    }

    //ПАРАМЕТРЫ ТАЙМЕРА. ЗАПУСКАЕТСЯ В OnCreate
    private void InitCounter() {
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 1000);

    }

    private void GPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
    }


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }


    };


    private void showLocation(Location location) {
        if (location == null)
            return;

    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {

    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    ;

    private void InitBT() {
        //ПРОВЕРКА НА НАЛИЧИЕ НА ТЕЛЕФОНЕ БЛЮТУСА И ЗАПРОС НА ЕГО ВКЛЮЧЕНИЕ, ЕСЛИ ОН ОТКЛЮЧЕН
        if (myBlueToothAdapter == null)
            SendToastBroadcast("Your device doesnt support Bluetooth");
            //Toast.makeText(getApplicationContext(), , Toast.LENGTH_LONG).show();
        else if (!myBlueToothAdapter.isEnabled()) { //в сервисе так не работает. возможно из он криэйта придётся перенести в он старт
            //Intent BtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(BtIntent, 0);
            //Toast.makeText(getApplicationContext(), "Turning on Bluetooth", Toast.LENGTH_LONG).show();
            myBlueToothAdapter.enable();
        }
        if (myBlueToothAdapter.isDiscovering())
            myBlueToothAdapter.cancelDiscovery();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);

        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.EXTRA_CONNECTION_STATE);
        intentFilter.addAction(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION);
        intentFilter.addAction(BluetoothAdapter.EXTRA_LOCAL_NAME);
        intentFilter.addAction(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE);
        intentFilter.addAction(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE);
        intentFilter.addAction(BluetoothAdapter.EXTRA_PREVIOUS_STATE);
        intentFilter.addAction(BluetoothAdapter.EXTRA_SCAN_MODE);
        intentFilter.addAction(BluetoothAdapter.EXTRA_STATE);
        registerReceiver(FoundReceiver, intentFilter);
        //Log.d("жопонька","инициализирован блютуз");
        Debug.Log("инициализирован блютуз");
    }

    private void InitBroadcastReceiver() {
        registerReceiver(ActivityReceiver, new IntentFilter("ShiveluchActivity.RequestService"));
        registerReceiver(ActivityReceiver, new IntentFilter("ShiveluchActivity.Action"));
    }

    private void SendStringBroadcast(String value) {
        Intent intent = new Intent("ShiveluchService.StringBroadcast");
        intent.putExtra("Message", value);
        sendBroadcast(intent);
    }

    private void SendToastBroadcast(String value) {
        Intent intent = new Intent("ShiveluchService.ToastBroadcast");
        intent.putExtra("Message", value);
        sendBroadcast(intent);
    }

    private void SendDatapackBroadcast(DataPack value, String Event) {
        Intent intent = new Intent("ShiveluchService.DatapackBroadcast");
        intent.putExtra("Datapack", value);
        intent.putExtra("Event", Event);
        sendBroadcast(intent);
    }

    //@Subscribe
    //public void handleSomethingElse(String value) {

    //}


    private final BroadcastReceiver ActivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = "";
            action = intent.getAction();

            Debug.Log("принят бродкаст " + action);
            switch (action) {
                case "ShiveluchActivity.RequestService":
                    Debug.Log("уведомление активити по запросу");
                    //HermesEventBus.getDefault().post();
                    SendDatapackBroadcast(DataPack.GetDataPack(service), "ALL");
                    break;
                case "ShiveluchActivity.Action":
                    String ExtraAction = intent.getStringExtra("ExtraAction");
                    Debug.Log("параметр: " + ExtraAction);
                    int result;
                    switch (ExtraAction) {
                        case "Suicide":
                            Suicide();
                            break;
                        case "KolobokToggle":
                            if (playerCharcteristics.getKolobok().Gived())
                                playerCharcteristics.getKolobok().set_active(!playerCharcteristics.getKolobok().Active());
                            break;
                        case "PuzirToggle":
                            if (playerCharcteristics.getPuzir().Gived())
                                playerCharcteristics.getPuzir().set_active(!playerCharcteristics.getPuzir().Active());
                            break;
                        case "PlenkaToggle":
                            if (playerCharcteristics.getPlenka().Gived())
                                playerCharcteristics.getPlenka().set_active(!playerCharcteristics.getPlenka().Active());
                            break;
                        case "HeartToggle":
                            if (playerCharcteristics.getHeart().Gived())
                                playerCharcteristics.getHeart().set_active(!playerCharcteristics.getHeart().Active());
                            break;
                        case "BatteryToggle":
                            if (playerCharcteristics.getBattery().Gived())
                                playerCharcteristics.getBattery().set_active(!playerCharcteristics.getBattery().Active());
                            break;
                        case "UseMedkit":
                            playerCharcteristics.UseMedkit();
                            break;

                        case "UseMilMedkit":
                            playerCharcteristics.UseMilMedkit();
                            break;

                        case "UseRepairs":
                            playerCharcteristics.UseRepairs();
                            break;


                        case "UseSciMedkit":
                            playerCharcteristics.UseSciMedkit();
                            break;

                        case "UseAntirad":
                            playerCharcteristics.UseAntirad();
                            break;

                        case "GiveBolt":
                            playerCharcteristics.GiveBolt();
                            break;
                        case "GiveBar":
                            playerCharcteristics.setStalker_start(true);
                            Log.d("start",""+playerCharcteristics.getStalkerstart());
                            break;

                        case "UseBolt":
                            playerCharcteristics.UseBolt();
                            bolt_work=15;
                            break;
                        case "SaveData":
                            SaveData();
                            break;
                        case "GiveSuitKurtka":
                            playerCharcteristics.GiveSuitKurt();
                            break;

                        case "setVibro":
                            if (playerCharcteristics.is_v_otklik()) {
                                playerCharcteristics.set_vibro(false);
                                NotifyToast("Виброотклик отключен");
                            } else {
                                playerCharcteristics.set_vibro(true);
                                NotifyToast("Виброотклик включен");

                            }
                            break;

                        case "setVibroOff":
                            playerCharcteristics.set_vibro(false);
                            break;
                        case "GiveSuitBand":
                            playerCharcteristics.GiveSuitBand();
                            break;
                        case "GiveSuitPlas":
                            playerCharcteristics.GiveSuitPlas();
                            break;
                        case "GiveSuitExo":
                            playerCharcteristics.GiveSuitExo();
                            break;
                        case "GiveSuitBron":
                            playerCharcteristics.GiveSuitBron();
                            break;

                        case "GiveSuitBulat":
                            playerCharcteristics.GiveSuitBulat();
                            break;


                        case "GiveSuitDolgKombez":
                            playerCharcteristics.GiveSuitDolgKombez();
                            break;

                        case "GiveSuitDolgBron":
                            playerCharcteristics.GiveSuitDolgBron();
                            break;

                        case "GiveSuitDolgExa":
                            playerCharcteristics.GiveSuitDolgExa();
                            break;

                        case "GiveSuitFreeExa":
                            playerCharcteristics.GiveSuitFreeExa();
                            break;
                        case "GiveSuitMonKombez":
                            playerCharcteristics.GiveSuitMonKombez();
                            break;

                        case "GiveSuitSeva":
                            playerCharcteristics.GiveSuitSeva();
                            break;
                        case "GiveSuitMonBron":
                            playerCharcteristics.GiveSuitMonBron();
                            break;
                        case "GiveSuitMonExa":
                            playerCharcteristics.GiveSuitMonExa();
                            break;

                        case "GiveSuitNaemKombez":
                            playerCharcteristics.GiveSuitNaemKombez();
                            break;
                        case "GiveSuitNaemBron":
                            playerCharcteristics.GiveSuitNaemBron();
                            break;


                        case "GiveSuitCS1":
                            playerCharcteristics.GiveSuitCS1();
                            break;

                        case "GiveSuitCS2":
                            playerCharcteristics.GiveSuitCS2();
                            break;

                        case "GiveSuitCS3":
                            playerCharcteristics.GiveSuitCS3();
                            break;


                        case "GiveSuitHalat":
                            playerCharcteristics.GiveSuitHalat();
                            break;

                        case "GiveSuitEcolog2":
                            playerCharcteristics.GiveSuitEcolog2();
                            break;


                        case "GiveSuitMonster":
                            playerCharcteristics.GiveSuitMonster();
                            break;


                        case "GiveSuitIgrotex":
                            playerCharcteristics.GiveSuitIgrotex();
                            break;


                        case "GiveSuitBolot":
                            playerCharcteristics.GiveSuitBolot();
                            break;

                        case "GiveSuitZaria":
                            playerCharcteristics.GiveSuitZaria();
                            break;
                        case "GiveSuitEcolog":
                            playerCharcteristics.GiveSuitEcolog();
                            break;

                        case "GiveSuitStrazh":
                            playerCharcteristics.GiveSuitStrazh();
                            break;

                        case "GiveSuitVeter":
                            playerCharcteristics.GiveSuitVeter();
                            break;


                        case "GiveKolobok":
                            playerCharcteristics.GiveKolobok();
                            break;
                        case "GivePlenka":
                            playerCharcteristics.GivePlenka();
                            break;

                        case "GivePuzir":
                            Debug.Log("сервис получил бродкаст выдачи пузыря");
                            playerCharcteristics.GivePuzir();
                            break;

                        case "GiveHeart":
                            playerCharcteristics.GiveHeart();
                            break;

                        case "GiveBat":
                            playerCharcteristics.GiveKolobok();
                            break;

                        case "GiveKristall":
                            playerCharcteristics.GivePlenka();
                            break;
                        case "GivePust":
                            playerCharcteristics.GivePuzir();
                            break;
                        case "GiveRybka":
                            playerCharcteristics.GiveHeart();
                            break;
                        case "GiveFlower":
                            playerCharcteristics.GiveBattery();
                            break;

                        case "SuiteRepair":
                            playerCharcteristics.SuitRepair();
                            break;

                        case "GiveAntirad":
                            playerCharcteristics.GiveAntirad();
                            break;

                        case "GiveMedikit":
                            playerCharcteristics.GiveMedkit();
                            break;

                        case "GiveMilMedikit":
                            playerCharcteristics.GiveMilMedikit();
                            break;
                        case "GiveRepair":
                            playerCharcteristics.GiveRepair();
                            break;

                        case "GiveSciMedikit":
                            playerCharcteristics.GiveSciMedikit();
                            break;

                        case "IncreaseExp10":
                            playerCharcteristics.IncreaseExp(10);
                            break;

                        case "IncreaseExp25":
                            playerCharcteristics.IncreaseExp(25);
                            break;
                        case "IncreaseExp50":
                            playerCharcteristics.IncreaseExp(50);
                            break;

                        case "IncreaseFireResist":
                            result=playerCharcteristics.GetFireResist();
                            if (result<61)playerCharcteristics.IncreaseFire_resist(5);
                            break;

                        case "DecreaseFireResist":
                            playerCharcteristics.IncreaseFire_resist(-5);
                            break;


                        case "IncreasePoisonResist":
                            result=playerCharcteristics.GetPoisonResist();
                            if (result<61)playerCharcteristics.IncreasePoison_resist(5);
                            break;

                        case "DecreasePoisonResist":
                            playerCharcteristics.IncreasePoison_resist(-5);
                            break;


                        case "IncreaseElectroResist":
                            result=playerCharcteristics.GetElectroResist();
                            if (result<61) playerCharcteristics.IncreaseElectro_resist(5);
                            break;

                        case "DecreaseElectroResist":
                            playerCharcteristics.IncreaseElectro_resist(-5);
                            break;

                        case "IncreaseGravResist":
                            result=playerCharcteristics.GetGravResist();
                            if (result<61)playerCharcteristics.IncreaseGrav_resist(5);
                            break;

                        case "DecreaseGravResist":
                            playerCharcteristics.IncreaseGrav_resist(-5);
                            break;
                        case "IncreasePsiResist":
                            result=playerCharcteristics.GetPsiResist();
                            if (result<61)playerCharcteristics.IncreasePsi_resist(5);
                            break;

                        case "DecreasePsiResist":
                            playerCharcteristics.IncreasePsi_resist(-5);
                            break;

                        case "IncreaseRadResist":
                            result=playerCharcteristics.GetRadResist();
                            if (result<61) playerCharcteristics.IncreaseRad_resist(5);


                            break;

                        case "DecreaseRadResist":
                            playerCharcteristics.IncreaseRad_resist(-5);
                            break;


                        case "SetNarkTrue":
                            playerCharcteristics.IncreaseHealth(100000);
                            playerCharcteristics.IncreaseExp(-5);
                            playerCharcteristics.set_nark(true);
                            break;
                        case "SetNarkFalse":
                            playerCharcteristics.IncreaseExp(5);
                            playerCharcteristics.set_nark(false);
                            break;
                        case "GivePsiHelm":
                            playerCharcteristics.setPsi_helm(true);
                            break;

                        case "GiveAdept":
                        {
                            playerCharcteristics.setAdept(true);
                            Log.d("Adept",""+playerCharcteristics.isAdept());
                            Toast.makeText(getApplicationContext(), "Ты стал адептом Монолита", Toast.LENGTH_LONG).show();
                            break;
                        }

                        case "GiveHunter":
                        {
                            playerCharcteristics.setHunter(true);
                            break;
                        }

                        case "RemoveAdept":
                        {
                            playerCharcteristics.setAdept(false);
                            Log.d("Adept",""+playerCharcteristics.isAdept());
                            Toast.makeText(getApplicationContext(), "Ты вышел из-под контроля Монолита", Toast.LENGTH_LONG).show();

                        }
                        break;
                        case "GiveRespirator":
                            playerCharcteristics.setRespirator(true);
                            playerCharcteristics.setProtivogas(false);
                            playerCharcteristics.setSZD(false);
                            break;
                        case "GiveProtivogas":
                            playerCharcteristics.setRespirator(false);
                            playerCharcteristics.setProtivogas(true);
                            playerCharcteristics.setSZD(false);
                            break;
                        case "GiveSZD":
                            playerCharcteristics.setRespirator(false);
                            playerCharcteristics.setProtivogas(false);
                            playerCharcteristics.setSZD(true);
                            break;
                        case "GiveTropa":
                            playerCharcteristics.setTropa(true);
                            break;


                        case "GiveNark":
                            playerCharcteristics.set_nark(true);
                            break;

                        case "GiveNarkHeal":
                            playerCharcteristics.set_nark(false);
                            break;

                        case "GiveDoza":
                            playerCharcteristics.set_nark(true);
                            break;

                        case "GiveBolezn":
                            playerCharcteristics.setBolezn(true);
                            break;

                        case "GiveHeal":
                            playerCharcteristics.setBolezn(false);
                            break;

                        case "GiveSuicide":
                            playerCharcteristics.IncreaseHealth(-1000000);
                            playerCharcteristics.getSuit().DecreaseStamina(10000);
                            break;

                        case "GiveOtrava":
                            playerCharcteristics.setOtrava(true);
                            break;

                        case "GiveOtvar":
                            playerCharcteristics.setOtrava(false);
                            break;


                        case "KillQR":
                            playerCharcteristics.setDead(true);
                            break;
                        case "RespawnQR":
                            playerCharcteristics.setZombi(false);
                            playerCharcteristics.setDead(false);
                            playerCharcteristics.set_nark(false);
                            playerCharcteristics.setOtrava(false);
                            playerCharcteristics.setImmun(false);
                            playerCharcteristics.IncreaseHealth(100000);
                            playerCharcteristics.PsiHealing();
                            playerCharcteristics.setMedikits(0);
                            playerCharcteristics.setAntirads(0);
                            playerCharcteristics.setBolt(0);

                            break;

                        case "GiveSuitBerill":
                            playerCharcteristics.GiveSuitBerill();
                            break;
                        case "VibrateCancel":
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.cancel();
                            break;
                        case "SendLog":
                            Debug.sendLog();
                            break;
                        case "ClearLog":
                            Debug.Clear();
                            break;
                        case "ActivateLog":
                            Debug.Activate();
                            break;
                        case "DeactivateLog":
                            Debug.Deactivate();
                            break;
                        default:
                            Debug.Log("Неизвестный нотификатор " + ExtraAction);
                            break;
                    }


                    break;
                default:
                    Debug.Log("Неизвестный action " + action);
                    break;


            }


        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Debug.Log("onStartCommand");
        boolean reset;
        if (intent.hasExtra("ResetPlayer")) {
            Debug.Log("В интент сервиса есть данные о сбросе");
            reset = intent.getBooleanExtra("ResetPlayer", true);
        } else {
            Debug.Log("В интент сервиса нет данных о сбросе, вероятно пбыло падение сервиса");
            reset = false;
        }

        if (!reset) {
            Debug.Log("Данные персонажа найдены, запускаем LoadState");
            LoadState();
            Debug.Log("LoadState завершен");
        } else {
            Debug.Log("Обнаружен флаг сброса");
            InitPlayerCharacteristic(intent.getStringExtra("Name"), intent.getIntExtra("GroupID", 1), 0, 0, 0, 0, false, false, false, false, false, false, false, false, false, false, false, false, false,0);
            Debug.Log("Данные игрока инициализированы");
        }
        service = this;

        if (flags == START_FLAG_REDELIVERY) {
            Debug.Log("Повторный запуск службы");
        } else {
            Debug.Log("Запуск службы");
        }
        InitBroadcastReceiver();
        InitBT();
        InitCounter();
        GPS();
        Debug.Log("Служба запущена");
        SendDatapackBroadcast(DataPack.GetDataPack(service), "ALL");
        Debug.Log("Первичный датапак отправлен в бродкаст");
        return START_STICKY;
    }

    public void onCreate() {
        Debug.Log("onCreate сервиса");
        Debug.Open(this);
        String textInfo = "Версия SDK устройства: " + Build.VERSION.SDK_INT + "\n"
                + "Наименование версии ОС: " + Build.ID + "\n"
                + "Устройство: " + Build.DEVICE + "\n"
                + "Изготовитель: " + Build.MANUFACTURER + "\n"
                + "Модель: " + Build.MODEL + "\n";
        Debug.Log(textInfo);
        Debug.Log("Log инициализирован");
        myLocListener = new MyLocListener();


        myAudioManager = new PDA_AudioManager(this);
        PlaceFoundTimeStamp = new Date().getTime();
        kff = Initializator.GetKFF();
        Initializator.FillDeviceDescriptors();

        //bt_base=Initializator.BT_Macs();
        groups = Initializator.Groups();
        events = Initializator.GetEventsList();
        playerCharcteristics = new PlayerCharcteristics(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

     //   service.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, myLocListener);

        context = getApplicationContext();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("Permisns", "недостаточно разрешений");
            NotifyLog("Недостаточно разрешений");
            //Toast.makeText(context, "Недостаточно разрешений", Toast.LENGTH_LONG).show();
            return;
        }
        criteria = new Criteria();

        Debug.Log("Создаем антиудаляторный нотификатор");
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon);
        Notification notification;
        //if (Build.VERSION.SDK_INT < 16)
        //    notification = builder.getNotification();
        //else

        notification = builder.build();
        startForeground(777, notification);
        Intent hideIntent = new Intent(this, HideNotificationService.class);
        startService(hideIntent);

        Debug.Log("onCreate завершен");
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        //Log.d(LOG_TAG, "MyService onBind");

        return binder;

    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        //Log.d(LOG_TAG, "MyService onRebind");
    }

    public boolean onUnbind(Intent intent) {
        //Log.d(LOG_TAG, "MyService onUnbind");
        //setActivity(null);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        //HermesEventBus.getDefault().destroy();
        Debug.Log("onDestroy сервиса");
        this.unregisterReceiver(FoundReceiver);
        this.unregisterReceiver(ActivityReceiver);
        SaveState();
        Debug.Close();
        super.onDestroy();

        //Log.d(LOG_TAG, "MyService onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Debug.Log("onTaskRemoved сервиса");
        //this.unregisterReceiver(FoundReceiver);
        //this.unregisterReceiver(ActivityReceiver);
        //SaveState();
        Debug.Close();
        super.onTaskRemoved(rootIntent);
        //HermesEventBus.getDefault().unregister(this);

    }

    protected Group GetGroupByID(int ID) {
        return groups.get(ID);
    }


    public void InitPlayerCharacteristic(String Name, int GroupID, int medkits, int mil_medkits, int sci_medkits, int antirads, boolean nark, boolean bolezn, boolean otrava, boolean psi_helm, boolean respirator, boolean protivogas, boolean SZD, boolean tropa, boolean kolobok, boolean plenka, boolean puzir, boolean heart, boolean battery,int bolt) {
        playerCharcteristics.setName(Name);
        playerCharcteristics.setGroup(GetGroupByID(GroupID));
        playerCharcteristics.setMedikits(medkits);
        playerCharcteristics.setMilMedikits(mil_medkits);
        playerCharcteristics.setSciMedikits(sci_medkits);
        playerCharcteristics.setAntirads(antirads);
        playerCharcteristics.setPsi_helm(psi_helm);
        playerCharcteristics.set_nark(nark);
        playerCharcteristics.setBolezn(bolezn);
        playerCharcteristics.setOtrava(otrava);
        playerCharcteristics.setRespirator(respirator);
        playerCharcteristics.setProtivogas(protivogas);
        playerCharcteristics.setSZD(SZD);
        playerCharcteristics.setTropa(tropa);
        playerCharcteristics.setBolt(bolt);


        if (kolobok) playerCharcteristics.GiveKolobok();
        if (plenka) playerCharcteristics.GivePlenka();
        if (puzir) playerCharcteristics.GivePuzir();
        if (heart) playerCharcteristics.GiveHeart();
        if (battery) playerCharcteristics.GiveBattery();
    }

    public void Suicide() {
        playerCharcteristics.setDead(true);
    }

    //ОБРАБОТКА ПОЛУЧЕНИЯ СПИСКА БЛЮТУСОВ
    private final BroadcastReceiver FoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Debug.Log("принят бродкаст");
            String action = "";
            action = intent.getAction();
            Debug.Log("в бродкаст ресивер блютуза пришёл экшен " + action);
            // When discovery finds a new device
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    // Get the BluetoothDevice object from the Intent
                    rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    playerCharcteristics.setDistance(Math.abs(rssi));
                    Debug.Log("Мощность " + rssi);
                    ;

                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String temp;//1. Переменная для хранения имен обнаруженных блютус устройств
                    String mac; // 2. Переменная для мак-адресов обнаруженных блютус-устрйоств

                    String[] realmac =
                            {
                                    "C8:FD:19:94:5A:68",
                                    "64:CF:D9:08:E6:90",
                                    "88:25:83:F0:B0:BE",
                                    "88:25:83:F0:34:38",
                                    "54:4A:16:6F:A9:3C",
                                    "18:93:D7:4A:5F:BC",
                                    "88:25:83:F0:30:50",
                                    "88:25:83:F0:AF:A6",
                                    "50:33:8B:10:00:FC",
                                    "C8:FD:19:A0:56:3D",
                                    "88:25:83:F0:20:2C",
                                    "88:25:83:F0:21:9E",
                                    "88:25:83:F0:22:3B",
                                    "88:25:83:F0:26:5B",
                                    "88:25:83:F0:1F:D8",
                                    "88:25:83:F0:31:83",
                                    "88:25:83:F0:2C:04",
                                    "88:25:83:F0:AE:F4",
                                    "88:25:83:F0:31:B0",
                                    "88:25:83:F0:28:AA",
                                    "88:25:83:F0:2B:BD",
                                    "88:25:83:F0:32:ED",
                                    "88:25:83:F0:30:26",
                                    "88:25:83:F0:B1:AE",
                                    "50:F1:4A:46:67:2D",
                                    "64:CF:D9:2B:37:07",
                                    "88:25:83:F0:27:F6",
                                    "88:25:83:F0:25:7B",
                                    "88:25:83:F0:25:B4",
                                    "88:25:83:F0:2A:70",
                                    "D4:36:39:A1:72:DF",
                                    "58:7A:62:57:E3:65",
                                    "00:15:83:F0:13:18",
                                    "88:25:83:F0:2C:5C",
                                    "88:25:83:F0:25:DA",
                                    "50:8C:B1:47:97:18",
                                    "88:25:83:F0:21:93",
                                    "88:25:83:F0:29:8E",
                                    "88:25:83:F0:25:FF",
                                    "88:25:83:F0:22:75",
                                    "88:25:83:F0:1E:B6",
                                    "7C:01:0A:45:9B:56",
                                    "88:25:83:F0:24:16",
                                    "88:25:83:F0:27:21",
                                    "88:25:83:F0:28:73",
                                    "88:25:83:F0:29:C3",
                                    "88:25:83:F0:A8:9B",
                                    "0C:B2:B7:0F:24:45",
                                    "88:25:83:F0:28:40",
                                    "44:EA:D8:FE:A3:D3",
                                    "64:CF:D9:07:C7:CC",
                                    "50:8C:B1:86:04:DF",
                                    "D4:36:39:8C:2F:10",
                                    "88:25:83:F0:26:67",
                                    "88:25:83:F0:30:91",
                                    "88:25:83:F0:29:22",
                                    "D4:36:39:6C:AE:47",
                                    "C8:FD:19:90:2C:2C",
                                    "88:25:83:F0:29:34",
                                    "88:25:83:F0:27:D4",
                                    "10:CE:A9:24:EE:49",
                                    "88:25:83:F0:30:04",
                                    "D4:36:39:C1:A5:3A",
                                    "88:25:83:F0:B6:BD",
                                    "88:25:83:F0:A8:B3",
                                    "50:F1:4A:51:17:0B",
                                    "88:25:83:F0:33:E3",
                                    "C8:FD:19:71:04:B1",
                                    "9C:1D:58:18:46:E5",
                                    "64:CF:D9:2B:2D:DB",
                                    "88:25:83:F0:A9:86",
                                    "40:BD:32:A6:68:C7",
                                    "88:25:83:F0:33:5C",
                                    "44:44:1B:04:0E:0B",
                                    "44:44:1B:04:0D:AF",
                                    "88:25:83:F0:27:36",
                                    "C8:FD:19:8A:7E:70",
                                    "50:33:8B:10:08:D9",
                                    "40:BD:32:86:D7:CC",
                                    "88:25:83:F0:25:6F",
                                    "00:15:83:F0:0D:74",
                                    "88:25:83:F0:B1:A3",
                                    "00:15:83:F0:0E:23",
                                    "88:25:83:F0:26:25",
                                    "88:25:83:F0:31:1B",
                                    "A15A0203985C",
                                    "88:25:83:F0:B1:EB",
                                    "00:15:83:F0:14:98",
                                    "88:25:83:F0:20:1A",
                                    "A1:5A:02:03:98:5C",
                                    "94:49:18:04:04:66"


                            };

                    String[] fakemac =
                            {
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:01:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "01:02:00:00:00:00",
                                    "02:01:00:00:00:00",
                                    "02:01:00:00:00:00",
                                    "02:01:00:00:00:00",
                                    "02:02:00:00:00:00",
                                    "02:02:00:00:00:00",
                                    "02:03:00:00:00:00",
                                    "03:01:00:00:00:00",
                                    "03:01:00:00:00:00",
                                    "03:01:00:00:00:00",
                                    "03:02:00:00:00:00",
                                    "03:02:00:00:00:00",
                                    "00:01:00:00:00:00",
                                    "00:00:00:00:00:00",
                                    "03:03:00:00:00:00",
                                    "03:04:00:00:00:00",
                                    "03:04:00:00:00:00",
                                    "03:04:00:00:00:00",
                                    "04:01:00:00:00:00",
                                    "04:02:00:00:00:00",
                                    "04:02:00:00:00:00",
                                    "04:04:00:00:00:00",
                                    "04:04:00:00:00:00",
                                    "04:04:00:00:00:00",
                                    "04:04:00:00:00:00",
                                    "04:04:00:00:00:00",
                                    "04:04:00:00:00:00",
                                    "04:09:00:00:00:00",
                                    "05:01:00:00:00:00",
                                    "05:01:00:00:00:00",
                                    "05:02:00:00:00:00",
                                    "05:03:00:00:00:00",
                                    "06:03:00:00:00:00",
                                    "06:01:00:00:00:00",
                                    "06:01:00:00:00:00",
                                    "06:01:00:00:00:00",
                                    "06:01:00:00:00:00",
                                    "06:01:00:00:00:00",
                                    "06:01:00:00:00:00",
                                    "07:04:00:00:00:00",
                                    "07:04:00:00:00:00",
                                    "07:04:00:00:00:00",
                                    "07:04:00:00:00:00",
                                    "07:04:00:00:00:00",
                                    "07:04:00:00:00:00",
                                    "07:03:00:00:00:00",
                                    "07:03:00:00:00:00",
                                    "00:02:00:00:00:00",
                                    "00:03:00:00:00:00",
                                    "00:04:00:00:00:00",
                                    "00:06:00:00:00:00",
                                    "08:06:00:00:00:00",
                                    "00:0B:00:00:00:00",
                                    "00:0E:00:00:00:00",
                                    "00:05:00:00:00:00",
                                    "00:09:00:00:00:00",
                                    "00:08:00:00:00:00",
                                    "00:08:00:00:00:00",
                                    "00:04:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "01:03:00:00:00:00",
                                    "00:00:00:00:00:00"

                            };

                    temp = device.getName();
//                    if (temp.charAt(0)!='0'||temp.charAt(0)!='1'||temp.charAt(0)!='2'||temp.charAt(0)!='3'||temp.charAt(0)!='4'||temp.charAt(0)!='5'||temp.charAt(0)!='6'||temp.charAt(0)!='7'||temp.charAt(0)!='8'||temp.charAt(0)!='9')
//                    {
//                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                        temp = device.getName();
//                    }
                    Debug.Log("найдено блютуз устройство " + temp);
                    mac = "" + device.getAddress();
                    Log.d("mac", "обнаружен макадрес " + mac);
                    for (int i = 0; i < realmac.length; i++) {
                        if (mac.equals(realmac[i])) {
                            mac = fakemac[i];
                            Log.d("mac", "Обнаруженный макадрес заменен на " + mac);
                        }
                    }
                    int scanMode = myBlueToothAdapter.getScanMode();
                    Debug.Log("scan mode " + scanMode);
                    // Debug.Log( "bt:"+temp.charAt(2)+temp.charAt(3));
                    String log = "Найдено устройство блютуз за именем " + temp + " и мак сдресом " + mac + " класс " + device.getBluetoothClass().getMajorDeviceClass();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        log = log + " и типом " + device.getType();
                    }
                    Debug.Log(log);
                    //СРАВНЕНИЕ СО СПИСКОМ РАЗРЕШЕННЫХ БЛЮТУСОВ
                    if (!Initializator.AllowAllMAC()) { // СЕЙЧАС РАЗРЕШЕНЫ ВСЕ БЛЮТУСЫ. ЧТОБЫ ВЕРНУТЬ СПИСОК РАЗРЕШЕННЫХ - метод Initializator.AllowAllMAC() должен вернуть ложь
                        boolean bt_ready = Initializator.isCorrectMAC(mac);
                        if (!bt_ready) {
                            //Debug.Log( "отклонено по мак адресу");
                            return;   // и это тоже
                        }

                    }
                    //if(temp == null)
                    //    return;
                    //if(temp.length() == 0)
                    //    return;
                    int type = 0;
                    int power = 0;
                    int location = 0;
                    int rstype = rssi;
                    boolean AllCorrect = false;


                    if (temp != null || temp != "null") {
                        try {
                            //находим индекс первого вхождения символа сепаратора в подстроке
                            int pos = temp.indexOf(Initializator.GetBeaconNameSeparator());
                            //вычленяем имя атрибута из подстроки
                            String primary = temp.substring(0, pos);
                            //вычленяем значение атрибута
                            String secondary;
                            secondary = temp.substring(pos + 1, temp.length());
                            if (temp.length() > 3) {
                                secondary = "" + temp.charAt(2) + temp.charAt(3);
                                Debug.Log("BT " + secondary);
                            }

                            int primaryInt = Integer.parseInt(primary);
                            int secondaryInt = Integer.parseInt(secondary);
                            if (primaryInt == 0) {
                                location = secondaryInt;
                                type = 0;
                                power = 0;
                            } else {
                                type = primaryInt;
                                power = secondaryInt;
                                location = 0;
                            }
                            Debug.Log("из имени тип " + type + "/мощность " + power + "/локация " + location + "/distance " + rssi);
                            AllCorrect = true;
                        } catch (Exception e) {
                            AllCorrect = false;
                        }
                    }
                    if (!AllCorrect) {
                        int primaryInt = 0;
                        int secondaryInt = 0;
                        DeviceDescriptor deviceDescriptor = DeviceDescriptor.getDeviceDescriptor(mac);
                        if (deviceDescriptor != null) {
                            primaryInt = deviceDescriptor.getPrimaryInt();
                            secondaryInt = deviceDescriptor.getSecondaryInt();
                            Debug.Log("параметры из БД по мак адресу " + mac + "---" + primaryInt + "/" + secondaryInt);
                        } else {


                            String primary = mac.substring(0, 2);
                            //вычленяем значение атрибута
                            String secondary = mac.substring(3, 5);
                            Debug.Log("мак строки" + primary + "/" + secondary);
                            primaryInt = Integer.parseInt(primary, 16);
                            secondaryInt = Integer.parseInt(secondary, 16);
                        }
                        if (primaryInt == 0) {
                            location = secondaryInt;
                            type = 0;
                            power = 0;
                        } else {
                            type = primaryInt;
                            power = secondaryInt;
                            location = 0;
                        }

                        if (mac == "00:15:83:F0:0F:A0") {
                            type = 1;
                            power = 6;
                            location = 0;
                            Debug.Log("Радиация 6 уровня в процессе");

                        }
                        Debug.Log("из мак адреса тип " + type + "/мощность " + power + "/локация " + location);
                        AllCorrect = true;
                    }

                    if (!AllCorrect)
                        Debug.Log("Некорректный парсинг");
                    if (AllCorrect)
                        if (SetPlaceAndAnomalyPower(type, power, location))
                            foundDevices.add(device);
                        else
                            break;
                    else
                        break;

                    int cnt = foundDevices.size();

                    if (cnt >= Initializator.DiscoveryCountLimit()) {
                        DiscoveryStarted = false;
                        discoveryTimer = 0;
                        if (myBlueToothAdapter.isDiscovering()) {
                            myBlueToothAdapter.cancelDiscovery();
                            //NotifyLog(""+Initializator.GetCurrentDF()+"discovery cancel by count");
                        }
                        if (foundDevices.isEmpty()) {
                            //Debug.Log("дискавери оборван");
                            if (place != Places.None) {
                                SetPlaceAndAnomalyPower(0, 0, 7);
                                progressanomaly = 0;
                                playerCharcteristics.setPower(0);
                                playerCharcteristics.setDistance(0);
                                Log.d("PsiHealth", "PSI: " + playerCharcteristics.getPsiHealth());
                                if (playerCharcteristics.getPsiHealth() < 40) {
                                    loc_vibration();
                                    NotifyActivity("VYBROS");

                                } else {
                                    NotifyActivity("INSHELTER");

                                }
                                //NotifyPlaceChange();
                                NotifyActivity("PLACE");
                            }

                        }
                    }

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    //Debug.Log("завершен дискавери естественным образом");
                    DiscoveryStarted = false;
                    discoveryTimer = 0;
                    //NotifyLog(""+Initializator.GetCurrentDF()+"discovery finish");
                    if (foundDevices.isEmpty()) {
                        //Debug.Log("результат дискавери пуст");
                        if (place != Places.None) {
                            //Debug.Log("старое место "+place.toString());
                            SetPlaceAndAnomalyPower(0, 0, 7);
                            progressanomaly = 0;
                            //NotifyPlaceChange();
                            NotifyActivity("PLACE");
                        }

                    }
                    break;

            }


        }
    };

    //int test;
    private boolean SetPlaceAndAnomalyPower(int type, int power, int location) {
        if (power > 9) power = 9;
        progressanomaly = power * 10;
        //playerCharcteristics.setLoctime(5);
        playerCharcteristics.setPower(power);
        //test=power;
        Places oldPlace = place;
        boolean result = true;
        switch (type) {
            case 0: {
                switch (location) {
                    case 7:
                        place = Places.None; // в плейсах нет такого варианта
                        break;
                    case 0:
                        place = Places.Bar;
                        break;
                    case 10:
                        place = Places.Shelter;
                        break;
                    case 1:
                        place = Places.Dolg;
                        break;
                    case 2:
                        place = Places.Freedom;
                        break;
                    case 4:
                        place = Places.Sci;
                        break;
                    case 5:
                        place = Places.ClearSky;
                        break;
                    case 6:
                        place = Places.Military;
                        break;
                    case 8:
                        place = Places.Monolit;
                        break;

                    case 9:
                        place = Places.Mercs;
                        break;
                    case 3:
                        place = Places.Bandos;
                        break;

                    case 11:
                        place = Places.Controller;
                        break;
                    case 12:
                        place = Places.Monstroboi;
                        break;
                    case 13:
                        place = Places.Renegades;
                        break;
                    case 14:
                        place = Places.Village;
                        break;
                    case 15:
                        place = Places.Plague;
                        break;
                    case 16:
                        place = Places.MonolitPiece;
                        break;
                    case 17:
                        place = Places.Doctor;
                        break;
                    case 18:
                        place = Places.Medic;
                        break;
                    case 19:
                        place = Places.Tec;
                        break;


                    default:
                        result = false;
                        break;
                }
            }
            break;
            case 1:
                place = Places.Radiation;
                break;
            case 2:
                place = Places.Tramplin;
                break;
            case 3:
                place = Places.Electra;
                break;
            case 4:
                place = Places.Psi;
                break;
            case 5:
                place = Places.Jarka;
                break;
            case 6:
                place = Places.Kisel;
                break;
            case 7:
                place = Places.Mine;
                break;
            case 8:
                place = Places.Buerer;
                break;
            default:
                place = Places.None;
                result = false;
                break;
        }
        if (oldPlace != place) {
            //Debug.Log("новое место "+place.toString());
            PlaceFoundTimeStamp = new Date().getTime();
            NotifyActivity("PLACE");
        }
        //NotifyPlaceChange();

        return result;
    }

    private void RadiationProceed() {
        float rad = playerCharcteristics.getRad();
        int koef = 0;
        if (rad >= 5000) {
            koef = 0;
        } else if (rad >= 4000) {
            koef = 10000;
        } else if (rad >= 3000) {
            koef = 4000;
        } else if (rad >= 2000) {
            koef = 1000;
        }

        playerCharcteristics.Damage(koef);

        if (rad >= 5000) {
            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());

            String addict = Initializator.GetCurrentDF() + ".  " + "Смерть от радиации";
            NotifyLog(addict);
        }

        if (playerCharcteristics.getHeal_big() <= 0) {
            String addict = Initializator.GetCurrentDF() + ".  " + "Смерть от истощения здоровья в результате радиационного воздействия";
            NotifyLog(addict);
        }
    }

    private void startDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    NotifyLog("Нет разрешения ACCESS_COARSE_LOCATION");
                    //ActivityCompat.requestPermissions(this,
                    //        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    //        REQUEST_ACCESS_COARSE_LOCATION);

                    break;
                case PackageManager.PERMISSION_GRANTED:
                    if (!myBlueToothAdapter.isDiscovering()) {
                        Debug.Log("старт дискавери");
                        myBlueToothAdapter.startDiscovery();
                        DiscoveryStarted = true;
                    } else
                        Debug.Log("Попытка двойного дискавери");
                    break;
            }

            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        } else {
            if (!myBlueToothAdapter.isDiscovering()) {

                myBlueToothAdapter.startDiscovery();
                DiscoveryStarted = true;
                Debug.Log("старт дискавери " + DiscoveryStarted);
                foundDevices.clear();
            } else
                Debug.Log("Попытка двойного дискавери");
        }


    }

    private void EventsProceed() {
        Debug.Log("переборка эвентов");

        for (PDA_Event event :
                events) {
            if (event instanceof PDA_AudioEvent) {
                Debug.Log("эвент аудио");
                if (((PDA_AudioEvent) event).getEndDateTime().before(new Date())) {//выброс уже закончился
                    Debug.Log("не актуален - уже должен был проиграться");
                    continue;
                }

                if (event.getStartDateTime().before(new Date())) { //начался
                    Debug.Log("Проигрывается файл");
                    myAudioManager.PlaySound(((PDA_AudioEvent) event).getSound());

                    Debug.Log("Файл проигран");
                    //playerCharcteristics.PsiDamage(Initializator.GetBlowoutPsiDamage(), PsiSources.Blowout);  //пси урон если проверка не прошла
                }


            }


            if (event instanceof PDA_MessageEvent) {
                Debug.Log("эвент месседж");
                if (event.isCompleted()) {
                    Debug.Log("помечен завершенным, переходим к следующему");
                    continue;
                }

                if (event.getStartDateTime().after(new Date())) {
                    Debug.Log("его время не наступило");
                    continue;
                }

                Debug.Log("публикуем");
                NotifyLog(((PDA_MessageEvent) event).getMessage());
                event.Complete();
                Debug.Log("переходим к следующему");
                continue;
            }

            if (event instanceof PDA_BlowoutEvent) {
                Debug.Log("эвент выброса");
                if (((PDA_BlowoutEvent) event).getEndDateTime().before(new Date())) {//выброс уже закончился
                    Debug.Log("не актуален - уже должен был закончиться");
                    blowoutflag = 0;
                    continue;
                }

                if (((PDA_BlowoutEvent) event).getAlarmTime().after(new Date()) && !event.isCompleted()) { //скоро начнется и мы ещё не предупреждали
                    Debug.Log("предупреждаем голосом о скором выбросе");

                    event.Complete();
                }

                if (event.getStartDateTime().before(new Date())) { //начался
                    Debug.Log("выброс в процессе");
                    blowoutflag = 1;
                    if (((PDA_BlowoutEvent) event).isVault(place)) {//проверка на убежище
                        Debug.Log("но мы в домике");
                        playerCharcteristics.PsiHealing();
                        Log.d("Убежище", ""+playerCharcteristics.getPsiHealth());
                        continue;
                    }
                    Debug.Log("пси урон "+playerCharcteristics.getPsiHealth());
                    playerCharcteristics.PsiDamage(Initializator.GetBlowoutPsiDamage(), PsiSources.Blowout);  //пси урон если проверка не прошла
                }


            }


        }
    }

    //region расширения базовых классов
    class MyTimerTask extends TimerTask {


        @Override
        public void run() {
            //ТАЙМЕРЫ. ПРИБАВЛЕНИЕ РАЗ В СЕКУНДУ
            //ЗАПУСК ОБНАРУЖЕНИЯ БЛЮТУСОВ
            //Debug.Log("сработал ежесекундный таймер");
            SaveState();
            if (myBlueToothAdapter == null)
                InitBT();

            String bt_name = myBlueToothAdapter.getName().toString();
            playerCharcteristics.setLoctime(playerCharcteristics.getLoctime() - 1);
            Log.d("spla", "In service: " + playerCharcteristics.getLoctime());


            //ПРОВЕРКА НА ВКЛЮЧЕННОСТЬ БЛЮТУСА. пРИ ОТКЛЮЧЕНИИ - СМЕРТЬ
            if (!myBlueToothAdapter.isEnabled()) {
                playerCharcteristics.setDead(true);
                return;
            }

            boolean gps_enabled=false;
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            if (!gps_enabled) playerCharcteristics.setDead(true);

            if (!DiscoveryStarted) {
                Debug.Log("классический старт дискавери");
                startDiscovery();
                Log.d("Таймер Дискавери", "Всего таймер: " + discoveryTimer);
                //ПЕРЕИМЕНОВАНИЕ ТЕЛЕФОНА В ТО ИМЯ, КОТОРЕ ВЫБРАЛ ИГРОК ПРИ СТАРТЕ
                if (!bt_name.equals(playerCharcteristics.getName())) {
                    myBlueToothAdapter.setName(playerCharcteristics.getName());

                }
            }
            //Debug.Log("продолжаем дискавери");

            if (DiscoveryStarted) {
                discoveryTimer++;
                Log.d("Таймер Дискавери", "Всего таймер: " + discoveryTimer);
                if (discoveryTimer >= Initializator.DiscoveryTimeLimit()) {
                    Log.d("Таймер Дискавери", discoveryTimer + " предел времени дискаверинга достигнут");

                    if (myBlueToothAdapter.isDiscovering()) {
                        //DiscoveryStarted=false;
                        discoveryTimer = 0;
                        Debug.Log("останавливаем дискавери по таймеру");
                        myBlueToothAdapter.cancelDiscovery();


                    }
//                    if (foundDevices.isEmpty()) {
//                        //Debug.Log("пустой результат дискавери");
//                        if (place != Places.None) {
//                            SetPlaceAndAnomalyPower(0,0,7);
//                            progressanomaly = 0;
//                            NotifyActivity("PLACE");
//                        }
//
//                    } else {
//                        Debug.Log("что то находилось и отвалится само по таймеру потом");
//                    }
                }
            }

            CheckGpsStatus();
            if (GpsStatus)
            {
                Log.d("GPS", "Checked");
                Log.d("Coord",""+lat+", "+lon);
//                statlan=lat;
//                statlon=lon;
             //   Log.d("Coordmaps",""+ShiveluchService.statlan+", "+ShiveluchService.statlon);
                gpsLoc();

            }


            Long currentTime = new Date().getTime();
            // Log.d("жопонька","Время начала: "+currentTime);
            if ((currentTime - PlaceFoundTimeStamp > 3000)) {
                if (place != Places.None) {
                    Debug.Log("");
                    SetPlaceAndAnomalyPower(0, 0, 7);// сброс плейса
                    place = Places.None;
                    //progressanomaly = 0;
                    // NotifyPlaceChange();
                    NotifyActivity("PLACE");
                }
            }

            if (place==Places.Bar
                    || place==Places.Bandos
                    || place==Places.Bar
                    || place==Places.Doctor
                    || place==Places.Dolg
                    || place==Places.Freedom
                    || place==Places.Mercs
                    || place==Places.Monolit
                    || place==Places.Sci
                    || place==Places.Shelter
                    || place==Places.ClearSky) {playerCharcteristics.PsiHealing(); Log.d("Убежище",""+playerCharcteristics.getPsiHealth());}

            if (playerCharcteristics.getGroup().isBasePlace(place)) {
                int group=playerCharcteristics.getGroup().getID();
                if (group==1 && !playerCharcteristics.getStalkerstart() && place==Places.Bar)
                {Log.d ("start", "Нельяз лечиться здесь"+", "+group+", "+playerCharcteristics.getStalkerstart()+", "+place);

                    return;
                }
                if (playerCharcteristics.getStalkerstart())
                {Log.d ("start", "Now you can"+", "+group+", "+playerCharcteristics.getStalkerstart()+", "+place);


                }
                playerCharcteristics.IncreaseHealth(Initializator.ValueHealingOnBase());


            }

            if (place == Places.Monstroboi) {
                myAudioManager.PlaySound(PDA_AudioManager.AppSounds.Monstroboi);
                monstroboi_count++;
                Log.d("Жопонька", "Монстробой сработал");


                if (playerCharcteristics.getSuit().getId() == 27 || playerCharcteristics.getGroup().getID() == 12) {
                    if (monstroboi_count > 20) {
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.MONSTROBOI_DEATH);
                        playerCharcteristics.setDead(true);
                        Log.d("Жопонька", "Монстр убит");
                        monstroboi_count = 10;
                    }

                }

            }

            if (!playerCharcteristics.isDead()) {
                event_state++;
                event_sound++;
                playerCharcteristics.setExpcount(playerCharcteristics.getExpcount() + 1);
                RadiationProceed();
                EventsProceed();
                NotifyActivity("POWER");
                //NotifyActivity("LOCTIME");

                //ПРОВЕРКА НАЛИЧИЯ РАБОЧЕГО ПСИ-ШЛЕМА И ПРЕБЫВАНИЯ В ЗОНЕ ДЕЙСТВИЯ ОСКОЛКА МОНОЛИТА
                if (playerCharcteristics.isPsi_helm() && place == Places.MonolitPiece) {
                    psi_helm_work++;
                    if (psi_helm_work > 60) {
                        playerCharcteristics.setPsi_helm(false);
                    }
                }

                if (bolt_work>0) {
                    bolt_work--;
                    NotifyActivity("ISBOLT");
                }


                if (playerCharcteristics.isZombi()) {
                    ControlerSoundTimer++;
                    Log.d("CONTSOUND", "" + ControlerSoundTimer);
                    if (ControlerSoundTimer > 24) {
                        Log.d("CONTSOUND", "PLAY");
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.UNDERCONTROL);
                        ControlerSoundTimer = 0;
                    }

                    zombi_status_time--;
                    Log.d("Зомбирование: ", "" + zombi_status_time);
                    if (zombi_status_time < 1) {
                        playerCharcteristics.setZombi(false);
                        NotifyActivity("noZombi");
                        zombi_status_time = 600;
                        playerCharcteristics.PsiHealing();
                    }

                }

                if (playerCharcteristics.getGroup().getID() == 1 && place == Places.Village) {
                    playerCharcteristics.IncreaseHealth(300);
                    if (playerCharcteristics.getHeal_big() > 100000) {
                        playerCharcteristics.setDead(false);
                    }

                }


                if (playerCharcteristics.isRespirator()) {

                }

                //ПРОВЕРКА НА НАЛИЧИЕ НАРКОЗАВИСИМОСТИ
                if (playerCharcteristics.is_nark()) {
                    playerCharcteristics.IncreaseNarkCount();
                }

                if (playerCharcteristics.isBolezn()) {
                    if (playerCharcteristics.isImmun())
                        return;
                    playerCharcteristics.IncreaseBoleznCount();


                }

                if (playerCharcteristics.isOtrava()) {
                    playerCharcteristics.setOtravaEffect();

                }

                // playerCharcteristics.IncreaseHealth(1); //самолечение
                playerCharcteristics.ArtifactsEffects(); //эффект от артефактов


                //ПРИБАВЛЕНИЕ ЗДОРОВЬЯ ПО 0.1 РАЗ В ПОЛМИНУТЫ
                if (add_health > 30) {
                    playerCharcteristics.IncreaseHealth(100);
                    add_health = 0;
                }

                //УВЕЛИЧЕНИЕ ОПЫТА НА ЕДИНИЦУ РАЗ В 10 МИНУТ
                if (playerCharcteristics.getExpcount() > 600) {
                    playerCharcteristics.IncreaseExp(1);
                    playerCharcteristics.setExpcount(0);
                }

                if (AtmoSoundStatus == 0) {
                    if (AtmoSoundsCounter < 1) {
                        Random randoms = new Random();
                        int rnd;
                        rnd = randoms.nextInt(2500) + 1;
                        if (rnd < 1800)
                            rnd = 1800;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.ATMO);
                        AtmoSoundsCounter = rnd;
                        Log.d("ATMO", "Play");

                    }
                    if (AtmoSoundsCounter > 0) AtmoSoundsCounter--;
                    Log.d("ATMO", "Counter: " + AtmoSoundsCounter);

                    if (blowoutflag == 1) {
                        BlowoutSoundsTimer++;
                        if (BlowoutSoundsTimer > 20) {
                            Log.d("OUT", "" + BlowoutSoundsTimer);
                            myAudioManager.PlaySound(PDA_AudioManager.AppSounds.BLOWOUT);
                            Log.d("BLOWOUT", "Play");
                            BlowoutSoundsTimer = 0;

                        }

                    }


                }


                if (playerCharcteristics.isMonvoice()) {
                    MonPieceCouner++;
                    if (MonPieceCouner >= 60) {
                        MonPieceCouner = 0;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.MONOLIT);
                    }
                }

                playerCharcteristics.RankRecalc();


                if (place == Places.Doctor) {
                    if (playerCharcteristics.is_nark())
                        playerCharcteristics.set_nark(false);
                    if (playerCharcteristics.isMonvoice())
                        playerCharcteristics.setMonvoice(false);
                    playerCharcteristics.IncreaseHealth(Initializator.ValueHealingOnBase());
                }


                float resist;
                int poweran = progressanomaly / 10;
                playerCharcteristics.setPower(poweran);


                String date = Initializator.GetCurrentDF();
                switch (place) {
                    case Medic:
                        playerCharcteristics.IncreaseHealth(Initializator.ValueHealingOnBase());
                    break;

                    case Tec:
                        playerCharcteristics.getSuit().IncreaseStamina(5);
                    break;

                     case Monstroboi:
                        loc_vibration();
                        Log.d("Жопонька","Монстробой сработал");
                      //  if (playerCharcteristics.getSuit().getId() == 27)
                        if (!playerCharcteristics.getHunter())
                        {playerCharcteristics.isDead();
                            Log.d("Жопонька","Монстр убит");}
                        break;

                    case Controller:
                        loc_vibration();
                        if (!cont_sound) {
                            myAudioManager.PlaySound(PDA_AudioManager.AppSounds.CONTOLLER);
                            cont_sound = true;
                        }
                        if (!playerCharcteristics.isPsi_helm()) {
                            playerCharcteristics.PsiDamage(17,PsiSources.Controller);
                        }
                        break;

                    case Plague:
                        playerCharcteristics.setBolezn(true);
                        break;

                    case Tramplin://расчет гравитационной аномалии
                        loc_vibration();
                        loctime=timer;

                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.GRAV);
                        if (event_sound>10)
                        {
                            event_sound=0;
                        }

                        if (event_state > 10) {
                            String addict = date + ".  " + "Аномалия: Гравиконцентрат  " + poweran + "-го уровня";
                            NotifyLog(addict);
                            event_state = 0;
                        }
                        resist=playerCharcteristics.GetGravResist();
                        if ( resist== 0) {
                            String addict = date + ".  " + "Смерть в гравитационной аномалии - нулевая защита";
                            NotifyLog(addict);
                            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());

                        } else {
                            if (resist<100)
                            {
                                int rstype=Math.abs(rssi);
                                if (rstype<88)
                                {
                                    int anomaly_koefficient=1;
                                    playerCharcteristics.setDistance(1);
                                    if (rstype<88 && rstype>=78) {anomaly_koefficient=1;playerCharcteristics.setDistance(1);}
                                    if (rstype<78 && rstype>=60) {anomaly_koefficient=3; playerCharcteristics.setDistance(2);}
                                    if (rstype<60) {anomaly_koefficient=5; playerCharcteristics.setDistance(3);}
//                                    if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
//                                        poweran=10-poweran;
//                                    }
                                    if (bolt_work<1)
                                    {damage = 3*anomaly_koefficient*(poweran*(1-resist/100));
                                        playerCharcteristics.DamageSuit(damage);
                                        anomalyHealDamage();
                                    }}
                                else
                                {if (event_state>5)
                                {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Гравиконцентрат";
                                    NotifyLog(addict);
                                    event_state=0;
                                }

                                }

                            }
                        }
                        break;
                    case Electra://расчет электры в будущем надо разделить
                        loc_vibration();
                        loctime=timer;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.ELECTRA);

                        if (event_state > 10) {
                            String addict = date + ".  " + "Аномалия: Электра  " + poweran + "-го уровня";
                            NotifyLog(addict);
                            event_state = 0;
                        }
                        resist=playerCharcteristics.GetElectroResist();

                        if ( resist== 0) {
                            String addict = date + ".  " + "Смерть в электроаномалии - нулевая защита";
                            NotifyLog(addict);
                            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());
                        } else {
                            if (resist<100)
                            {
                                int rstype=Math.abs(rssi);
                                if (rstype<88)
                                {
                                    int anomaly_koefficient=1;
                                    playerCharcteristics.setDistance(1);
                                    if (rstype<88 && rstype>=78) {anomaly_koefficient=1;playerCharcteristics.setDistance(1);}
                                    if (rstype<78 && rstype>=60) {anomaly_koefficient=3; playerCharcteristics.setDistance(2);}
                                    if (rstype<60) {anomaly_koefficient=5; playerCharcteristics.setDistance(3);}
//                                    if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
//                                        poweran=10-poweran;
//                                    }
                                    if (bolt_work<1)
                                    { damage = 3*anomaly_koefficient*(poweran*(1-resist/100));
                                        playerCharcteristics.DamageSuit(damage);
                                        anomalyHealDamage();
                                    }}
                                else
                                {if (event_state>5)
                                {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Электра";
                                    NotifyLog(addict);
                                    event_state=0;
                                }

                                }


                            }
                        }
                        break;
                    case Jarka:
                        loc_vibration();
                        loctime=timer;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.FIRE);

                        if (event_state > 10) {
                            String addict = date + ".  " + "Аномалия: Жарка  " + poweran + "-го уровня";
                            NotifyLog(addict);
                            event_state = 0;
                        }
                        resist=playerCharcteristics.GetFireResist();
                        if ( resist== 0) {
                            String addict = date + ".  " + "Смерть в тепловой аномалии - нулевая защита";
                            NotifyLog(addict);
                            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());
                        } else {
                            if (resist<100)
                            {
                                int rstype=Math.abs(rssi);
                                if (rstype<88)
                                {
                                    int anomaly_koefficient=1;
                                    playerCharcteristics.setDistance(1);
                                    if (rstype<88 && rstype>=78) {anomaly_koefficient=1;playerCharcteristics.setDistance(1);}
                                    if (rstype<78 && rstype>=60) {anomaly_koefficient=3; playerCharcteristics.setDistance(2);}
                                    if (rstype<60) {anomaly_koefficient=5; playerCharcteristics.setDistance(3);}
//                                    if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
//                                        poweran=10-poweran;
//                                    }
                                    if (bolt_work<1)
                                    { damage = 3*anomaly_koefficient*(poweran*(1-resist/100));
                                        playerCharcteristics.DamageSuit(damage);
                                        anomalyHealDamage();
                                    }}
                                else
                                {if (event_state>5)
                                {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Жарка";
                                    NotifyLog(addict);
                                    event_state=0;
                                }

                                }

                            }
                        }
                        break;
                    case Kisel:
                        loc_vibration();
                        loctime=timer;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.KISEL);

                        if (event_state > 10) {
                            String addict = date + ".  " + "Аномалия: Кислотный туман " + poweran + "-го уровня";
                            NotifyLog(addict);
                            event_state = 0;
                        }
                        resist=playerCharcteristics.GetPoisonResist();
                        if ( resist== 0) {
                            String addict = date + ".  " + "Смерть в ядовитой аномалии - нулевая защита";
                            NotifyLog(addict);
                            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());
                        } else {
//                            if (resist<100)
//                            {
//
//                                if (rstype<80)
//                                {damage = ((Math.abs((poweran * (100 - resist)) / kff))*1300)/10;
//                                    playerCharcteristics.Damage(damage * 4);}
//                                else
//                                {if (event_state>5)
//                                {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Пси-поле. Безопасное расстояние";
//                                    NotifyLog(addict);}
//
//                                }
//
//                            }


                            if (resist<100)
                            {
                                int rstype=Math.abs(rssi);
                                if (rstype<88)
                                {
                                    int anomaly_koefficient=1;
                                    playerCharcteristics.setDistance(1);
                                    if (rstype<88 && rstype>=78) {anomaly_koefficient=1;playerCharcteristics.setDistance(1);}
                                    if (rstype<78 && rstype>=60) {anomaly_koefficient=3; playerCharcteristics.setDistance(2);}
                                    if (rstype<60) {anomaly_koefficient=5; playerCharcteristics.setDistance(3);}
//                                    if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
//                                        poweran=10-poweran;
//                                    }
                                    if (bolt_work<1)
                                    {damage = 3*anomaly_koefficient*(poweran*(1-resist/100));
                                        playerCharcteristics.DamageSuit(damage);
                                        anomalyHealDamage();
                                    }}
                                else
                                {if (event_state>5)
                                {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Кислотный туман";
                                    NotifyLog(addict);
                                    event_state=0;
                                }

                                }

                            }


                        }
                        break;

                    case Mine:
                        loc_vibration();
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.MINE);

                        if (event_state > 10) {
                            String addict = date + ".  " + "Минное поле";
                            NotifyLog(addict);
                            event_state = 0;
                        }
                        resist=1;
                        if ( resist== 0) {
                            String addict = date + ".  " + "Смерть в минном поле";
                            NotifyLog(addict);
                            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());
                        } else {
                            if (resist<100) {
                                int rstype = Math.abs(rssi);

                                if (rstype < 80) {
                                    playerCharcteristics.DamageSuit(100);
                                    playerCharcteristics.Damage(10000);
                                    //Log.d("pois", "dam "+damage+ " Heal "+playerCharcteristics.getSuitStamina());

                                }
                            }
                        }
                        break;

                    case Buerer:
                        loc_vibration();
                        loctime=timer;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.BUERER);



                        if (event_state > 10) {
                            String addict = date + ".  " + "Атака бюрера";
                            NotifyLog(addict);
                            event_state = 0;
                        }
                        resist=1;
                        if ( resist== 0) {
                            String addict = date + ".  " + "Смерть от атаки бюрера - нулевая защита";
                            NotifyLog(addict);
                            playerCharcteristics.Damage(playerCharcteristics.getHeal_big());
                        } else {
                            damage =((Math.abs((poweran * (100 - resist)) / kff))*13)/10;
                            playerCharcteristics.DamageSuit(damage * 4);
                            anomalyHealDamage();
                        }
                        break;
                    case Psi:
                        timer=5;
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.PSIH);


                        if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
                            playerCharcteristics.IncreaseHealth(3000);

                        } else {
                            if (event_state > 10) {
                                String addict = date + ".  " + "Аномалия: ПСИ-Поле  " + poweran + "-го уровня";
                                NotifyLog(addict);
                                event_state = 0;
                            }
                            if(!playerCharcteristics.isPsi_helm()) {

                                if (poweran>=9 && !playerCharcteristics.getGroup().isBasePlace(Places.Monolit)&&playerCharcteristics.GetPsiResist()<100)
                                {playerCharcteristics.Damage(playerCharcteristics.getHeal_big());

                                    String addict = Initializator.GetCurrentDF() + ".  " + "Убит смертельным пси-излучением";
                                    NotifyLog(addict);}
                                //int rstype=Math.abs(rssi);
                                resist = playerCharcteristics.GetPsiResist();
                                if (resist == 0) {
                                    String addict = date + ".  " + "Смерть в ПСИ аномалии - нулевая защита";
                                    NotifyLog(addict);
                                    playerCharcteristics.Damage(playerCharcteristics.getHeal_big());
                                } else {
                                    if (resist<100)
                                    {
                                        int rstype=Math.abs(rssi);
                                        if (rstype<80)
                                        {
                                            int anomaly_koefficient=1;
                                            playerCharcteristics.setDistance(1);
                                            if (rstype<80 && rstype>=75) {anomaly_koefficient=1;playerCharcteristics.setDistance(1);}
                                            if (rstype<75 && rstype>=60) {anomaly_koefficient=3; playerCharcteristics.setDistance(2);}
                                            if (rstype<60) {anomaly_koefficient=5; playerCharcteristics.setDistance(3);}
//                                    if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
//                                        poweran=10-poweran;
//                                    }

                                            damage = 300*anomaly_koefficient*(poweran*(1-resist/100));
                                            if (!playerCharcteristics.isAdept()) playerCharcteristics.Damage(damage);

                                        }
                                        else
                                        {if (event_state>5)
                                        {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Пси-поле";
                                            NotifyLog(addict);
                                            event_state=0;
                                        }

                                        }

                                    }
                                }
                            }
                        }

                        break;
                    case MonolitPiece:
                        loctime=timer;
                        if(!playerCharcteristics.isPsi_helm() || !playerCharcteristics.getGroup().isBasePlace(Places.Monolit)||!playerCharcteristics.getGroup().isBasePlace(Places.Dolg)) {
                            if(!playerCharcteristics.isMonvoice()) {
                                myAudioManager.PlaySound(PDA_AudioManager.AppSounds.MONOLIT);
                                playerCharcteristics.setMonvoice(true);
                                MonPieceCouner=0;
                            }
                            playerCharcteristics.Damage(3000);

                        }
                        break;
                    case Radiation:
                        loc_vibration();
                        playerCharcteristics.setLoctime(localtimer);
//                        radiation_sound();
                        //loctime=timer;

                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.RADIATION);
                        if (event_state > 30) {
                            String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Радиация " + poweran + "-го уровня";
                            NotifyLog(addict);
                            //activity.NotifyLog(addict);
                            event_state = 0;

                        }

                        int rstype=Math.abs(rssi);

                        if (rstype<88)
                        {
                            int anomaly_koefficient=1;
                            playerCharcteristics.setDistance(1);
                            if (rstype<88 && rstype>=78) {anomaly_koefficient=1;playerCharcteristics.setDistance(1);}
                            if (rstype<78 && rstype>=60) {anomaly_koefficient=3; playerCharcteristics.setDistance(2);}
                            if (rstype<60) {anomaly_koefficient=5; playerCharcteristics.setDistance(3);}
//                                    if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit)) {
//                                        poweran=10-poweran;
//                                    }

                            int rad_effect = (Math.abs((poweran *anomaly_koefficient* (100 - playerCharcteristics.GetRadResist())) / 10));

                            playerCharcteristics.IncreaseRad(rad_effect);}
                        else
                        {if (event_state>5)
                        {String addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Радиация";
                            NotifyLog(addict);
                            event_state=0;
                        }

                        }
                        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.RADIATION);

                        break;
                    case None:

                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.cancel();
                        //mStreamId = 0;
                        if(playerCharcteristics.getGroup().isBasePlace(Places.Monolit))
                            playerCharcteristics.Damage(10);


                        break;
                }
            }

        }
    }

    private void anomalyHealDamage() {
        float damagean = 35*damage;
        playerCharcteristics.Damage(damagean);

    }

    //region Notify
    public void NotifyLog(String addict) {
        //HermesEventBus.getDefault().post(new LogEvent(addict));
        SendStringBroadcast(addict);
    }

    public void NotifyPower(String power) {
        SendStringBroadcast(power);
    }

    public void NotifyToast(String value) {
        //HermesEventBus.getDefault().post(new ToastEvent(value));
        SendToastBroadcast(value);
    }

    public void NotifyActivity(String Event) {
        if (playerCharcteristics != null) {
            DataPack dp = DataPack.GetDataPack(this);
            SendDatapackBroadcast(dp, Event);
            NotifySystem(dp);
        }
    }

    public void NotifySystem(DataPack dataPack) {
        Intent notificationIntent = new Intent(this, pda.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String status = "";
        if (dataPack.dead) {
            status = status + "Мертвый ";
        } else {
            status = status + "Живой ";
        }
        status = status + "Здоровье: " + (dataPack.heal_big) / 1000 + ", Костюм: " + (dataPack.suit_stam_big) / 10;
        Debug.Log(" системная нотификация обновлена " + status);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ShiveluchService.this, Initializator.CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Состояние")
                        .setContentText(status)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(contentIntent);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(ShiveluchService.this);
        notificationManager.notify(Initializator.NOTIFY_ID, builder.build());
    }

    //endregion

    //region Работа с БД

    //ЗАГРУЗКА ПАРАМЕТРОВ ПРОФИЛЯ ИЗ БД

    //ЗАПРОС К БД
    public void SaveData() {
        //new MyAsyncTask().execute("" + playerCharcteristics.getExp(), "" + playerCharcteristics.getSuit().getId(), password, "" + playerCharcteristics.getMedikits(), "" + playerCharcteristics.getAntirads(), "" +
        new MyAsyncTask().execute("" + playerCharcteristics.getExp(), "" + playerCharcteristics.getSuit().getId(), "sssss", "" + playerCharcteristics.getMedikits(), "" + playerCharcteristics.getAntirads(), "" + //временно подменен пароль
                playerCharcteristics.getKolobok().Gived(), "" + playerCharcteristics.getPlenka().Gived(), "" + playerCharcteristics.getPuzir().Gived(), "" + playerCharcteristics.getHeart().Gived(), "" +
                playerCharcteristics.getBattery().Gived());
        NotifyToast("Попытка сохранения...");
        getJSON(sbname);

        //if (service.playerCharcteristics.getExp() > expas) {
        //    NotifyToast("Сохранение не удалось");
        //} else {
        //    NotifyToast("Сохранение удачное");
        //}
    }

    private void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    try {


                        if (urlWebService == sbname) {
                            loadstalker(s);
                            //       Toast.makeText(getApplicationContext(), player.p_name+" 2", Toast.LENGTH_LONG).show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected String doInBackground(Void... voids) {


                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }


        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadstalker(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);


        String[] callsign_s = new String[jsonArray.length()];
        String[] group_id_s = new String[jsonArray.length()];
        String[] expa_s = new String[jsonArray.length()];
        String[] suit_s = new String[jsonArray.length()];
        //     String [] pen_sum = new String[jsonArray.length()];
        //  Toast.makeText(getApplicationContext(), ""+jsonArray.length(), Toast.LENGTH_LONG).show();

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                callsign_s[i] = obj.getString("callsign");
                group_id_s[i] = obj.getString("group_id");
                expa_s[i] = obj.getString("exp");
                suit_s[i] = obj.getString("suit");
                //   pen_serg_s[i] = obj.getString("serg");
                //   pen_sum[i]="НАРУШЕНИЕ: "+pen_type_s[i]+"\n"+"ДАТА: "+pen_date_s[i]+"\n"+"Автор жалобы: "+pen_serg_s[i];

            }

            int expas = Integer.parseInt(expa_s[0]);

            if (playerCharcteristics.getExp() > expas) {
                NotifyToast("Сохранение не удалось");
            } else {
                NotifyToast("Сохранение удачное");
            }

        } else {
            NotifyToast("Не найден идентификатор");
        }


    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9]);
            return null;
        }

        protected void onPostExecute(Double result) {
            //pb.setVisibility(View.GONE);
            //String com=checkS+" " + checkA+ " "+ checkG+ " " +checkC;
            // Toast.makeText(getApplicationContext(), com, Toast.LENGTH_LONG).show();
            //  teamname.setText(""); //reset the message text field title_param, msg_param;

        }

        protected void onProgressUpdate(Integer... progress) {
            //pb.setProgress(progress[0]);
        }

        public void postData(String expToSend, String suitToSend, String passtoSend, String medToSent, String radToSent, String art1toSend, String art2toSend, String art3toSend, String art4toSend, String art5toSend) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/stalker/update_stalkers.php");

            try {
                // Add your data


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("exp", expToSend));

                nameValuePairs.add(new BasicNameValuePair("suit", suitToSend));
                nameValuePairs.add(new BasicNameValuePair("pass", passtoSend));
                nameValuePairs.add(new BasicNameValuePair("medikit", medToSent));
                nameValuePairs.add(new BasicNameValuePair("antirad", radToSent));
                nameValuePairs.add(new BasicNameValuePair("art1", art1toSend));
                nameValuePairs.add(new BasicNameValuePair("art2", art2toSend));
                nameValuePairs.add(new BasicNameValuePair("art3", art3toSend));
                nameValuePairs.add(new BasicNameValuePair("art4", art4toSend));
                nameValuePairs.add(new BasicNameValuePair("art5", art5toSend));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                //    Toast.makeText(getApplicationContext(), "Сохранено...", Toast.LENGTH_SHORT).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                //Toast.makeText(getBaseContext(),"Client Protocol Exception",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Toast.makeText(getBaseContext(),"IO Exception",Toast.LENGTH_SHORT).show();
            }

        }
    }
    //endregion

    //region SharedPreferences
    SharedPreferences servicePreferences;

    private void SaveState() {
        //Debug.Log("начало сохранения состояния сервиса");
        servicePreferences = getSharedPreferences("datapack", MODE_PRIVATE);
        Editor ed = servicePreferences.edit();
        ed.putBoolean("ResetPlayer", false);
        ed.putBoolean("isDead", playerCharcteristics.isDead());
        ed.putFloat("Health", playerCharcteristics.getHeal_big());
        //Debug.Log("значение здоровья "+playerCharcteristics.getHeal_big());
        ed.putInt("Bolt",playerCharcteristics.getBolt());
        ed.putInt("rankmod", playerCharcteristics.get_rankmod());
        ed.putBoolean("Group", playerCharcteristics.is_group());
        ed.putBoolean("Bolezn", playerCharcteristics.isBolezn());
        ed.putBoolean("Otrava", playerCharcteristics.isOtrava());
        ed.putBoolean("Nark", playerCharcteristics.is_nark());
        ed.putInt("NarkCount", playerCharcteristics.getNark_count());
        ed.putBoolean("Immun", playerCharcteristics.isImmun());
        ed.putInt("GroupID", playerCharcteristics.getGroup().getID());
        ed.putInt("SuitID", playerCharcteristics.getSuit().getId());
        ed.putFloat("SuitStamina", playerCharcteristics.getSuitStamina());
        ed.putBoolean("Zombi", playerCharcteristics.isZombi());
        ed.putBoolean("PsiHelm", playerCharcteristics.isPsi_helm());
        ed.putBoolean("Respirator", playerCharcteristics.isRespirator());
        ed.putBoolean("Protivogas", playerCharcteristics.isProtivogas());
        ed.putBoolean("SZD", playerCharcteristics.isSZD());
        ed.putBoolean("Tropa", playerCharcteristics.isTropa());
        ed.putFloat("Rad", playerCharcteristics.getRad());
        ed.putInt("Medkits", playerCharcteristics.getMedikits());
        ed.putInt("MilMedkits", playerCharcteristics.getMilMedikits());
        ed.putInt("SciMedkits", playerCharcteristics.getSciMedikits());
        ed.putInt("Antirads", playerCharcteristics.getAntirads());
        ed.putInt("Exp", playerCharcteristics.getExp());
        ed.putInt("ExpCount", playerCharcteristics.getExpcount());
        ed.putBoolean("MonVoice", playerCharcteristics.isMonvoice());
        ed.putString("Name", playerCharcteristics.getName());
        ed.putBoolean("Kolobok", playerCharcteristics.getKolobok().Gived());
        ed.putBoolean("Puzir", playerCharcteristics.getPuzir().Gived());
        ed.putBoolean("Plenka", playerCharcteristics.getPlenka().Gived());
        ed.putInt("psi_helm_work", psi_helm_work);
        ed.putInt("zombi_status_time", zombi_status_time);
        ed.putInt("timer_osk", timer_osk);
        ed.putInt("MonPieceCouner", MonPieceCouner);
        ed.putInt("add_health", add_health);
        ed.putBoolean("stalker_start", playerCharcteristics.getStalkerstart());
        ed.putBoolean("adept",playerCharcteristics.isAdept());


        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        ed.putString("TimeStamp", timeText);
        Log.d("saved",""+playerCharcteristics.getStalkerstart());


        int oldQuantity = servicePreferences.getInt("EventsQuantity", 0);


        if (oldQuantity > events.length) { //удаление лишних записей если вдруг в прошлый раз эвентов было больше чем сейчас
            for (int counter = events.length; counter < oldQuantity - events.length; counter++) {
                ed.remove("Event_completed" + counter);
            }

        }
        ed.putInt("EventsQuantity", events.length);
        int counter = 0;
        for (PDA_Event event : events
        ) {
            ed.putBoolean("Event_completed" + counter, event.isCompleted());
            counter++;

        }


        ed.apply();
        //Debug.Log("конец сохранения состояния сервиса");
    }

    private void LoadState() {
        Debug.Log("начало загрузки состояния сервиса");
        servicePreferences = getSharedPreferences("datapack", MODE_PRIVATE);
        boolean ResetPlayer = servicePreferences.getBoolean("ResetPlayer", true);
        if (!ResetPlayer) {
            playerCharcteristics = new PlayerCharcteristics(this);
            playerCharcteristics.setDead(servicePreferences.getBoolean("isDead", true));
            float changeHealth = servicePreferences.getFloat("Health", 0) - playerCharcteristics.getHeal_big();
            playerCharcteristics.setBolt(servicePreferences.getInt("Bolt",0));
            playerCharcteristics.IncreaseHealth(changeHealth);
            playerCharcteristics.set_rankmod(servicePreferences.getInt("rankmod", 0));
            playerCharcteristics.set_group(servicePreferences.getBoolean("Group", true));
            playerCharcteristics.set_nark(servicePreferences.getBoolean("Nark", false));
            playerCharcteristics.setNark_count(servicePreferences.getInt("NarkCount", 0));
            playerCharcteristics.setGroup(groups.get(servicePreferences.getInt("GroupID", 1)));
            playerCharcteristics.SetSuitByID(servicePreferences.getInt("SuitID", 1));
            float changeSuitStamina = Math.max(0, servicePreferences.getFloat("SuitStamina", 1000) - playerCharcteristics.getSuitStamina());
            playerCharcteristics.getSuit().Repair(changeSuitStamina);
            playerCharcteristics.setZombi(servicePreferences.getBoolean("Zombi", false));
            playerCharcteristics.setPsi_helm(servicePreferences.getBoolean("PsiHelm", false));
            playerCharcteristics.setRespirator(servicePreferences.getBoolean("Respirator", false));
            playerCharcteristics.setProtivogas(servicePreferences.getBoolean("Protivogas", false));
            playerCharcteristics.setSZD(servicePreferences.getBoolean("SZD", false));
            playerCharcteristics.setTropa(servicePreferences.getBoolean("Tropa", false));
            float rad = servicePreferences.getFloat("Rad", 150) - playerCharcteristics.getRad();
            playerCharcteristics.IncreaseRad(rad);
            playerCharcteristics.setMedikits(servicePreferences.getInt("Medkits", 0));
            playerCharcteristics.setMilMedikits(servicePreferences.getInt("MilMedkits", 0));
            playerCharcteristics.setSciMedikits(servicePreferences.getInt("SciMedkits", 0));
            playerCharcteristics.setAntirads(servicePreferences.getInt("Antirads", 0));
            playerCharcteristics.setExp(servicePreferences.getInt("Exp", 0));
            playerCharcteristics.setExpcount(servicePreferences.getInt("ExpCount", 0));
            playerCharcteristics.setMonvoice(servicePreferences.getBoolean("MonVoice", false));
            playerCharcteristics.setName(servicePreferences.getString("Name", "Ошибка природы"));
            playerCharcteristics.setStalker_start(servicePreferences.getBoolean("stalker_start",false));
            if (servicePreferences.getBoolean("Kolobok", false))
                playerCharcteristics.GiveKolobok();
            if (servicePreferences.getBoolean("Puzir", false))
                playerCharcteristics.GivePuzir();
            if (servicePreferences.getBoolean("Plenka", false))
                playerCharcteristics.GivePlenka();
            if (servicePreferences.getBoolean("Heart", false))
                playerCharcteristics.GiveHeart();
            if (servicePreferences.getBoolean("Battery", false))
                playerCharcteristics.GiveBattery();
            playerCharcteristics.setAdept(servicePreferences.getBoolean("adept",false));

            psi_helm_work = servicePreferences.getInt("psi_helm_work", 0);
            timer_osk = servicePreferences.getInt("timer_osk", 0);
            MonPieceCouner = servicePreferences.getInt("MonPieceCouner", 0);
            add_health = servicePreferences.getInt("add_health", 0);

            //проверка времени с последнего сохранения состояния
            String timeText = servicePreferences.getString("TimeStamp", "00:00:00");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Long storedTime = 0l;
            try {
                storedTime = dateFormat.parse(timeText).getTime();
            } catch (ParseException e) {
                Debug.Log("Исключение при парсинге тайм стампа " + e.getMessage());
            }
            Long currentTime = new Date().getTime();
            if ((currentTime - Initializator.GetMaxTimeBeforeDie() > storedTime) || (currentTime - Initializator.GetMaxTimeBeforeDie() < 0)) {
                Debug.Log("Слишком большой пропуск в тайм стампах - умираем");
                Suicide();
            }

            int oldQuantity = events.length;

            for (int counter = 0; counter < oldQuantity; counter++) {
                boolean completed = servicePreferences.getBoolean("Event_completed" + counter, false);
                events[counter].Complete(completed);
            }

        } else
            Debug.Log("Ошибка, настройки игрока сброшены");

        Debug.Log("окончание загрузки состояния сервиса");
    }
    //endregion

    public void loc_vibration() {
        if (playerCharcteristics.is_v_otklik()) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                long[] pattern = {0, 500, 1000};
                vibrator.vibrate(pattern, 0);
            }
            //vibrator.cancel();
        }

    }

    public void radiation_sound() {

        SoundPool mSoundPool;
        int mSoundId = 1;

        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        mSoundPool.load(this, R.raw.file1, 1);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        int priority = 1;
        int no_loop = 0;
        float normal_playback_rate = 1f;
        mStreamId = mSoundPool.play(mSoundId, leftVolume, rightVolume, priority, no_loop,
                normal_playback_rate);

    }

    public void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private double distanceInKmBetweenEarthCoordinates(double lat1, double lon1, double lat2, double lon2) {
        int earthRadiusKm = 6371;
        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public void gpsLoc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {


            double cLat = location.getLatitude();
            double cLon = location.getLongitude();
            lat=cLat;
            lon=cLon;
            playerCharcteristics.setLongitude(lon);
            playerCharcteristics.setLatitude(lat);
            GPSanomaly();
        }
    }

    private void GPSanomaly() {
        String[] AnomaliesList = Initializator.GPSanomalies();
        LatLng curAnomalyPosition;

        if (location!=null)
            position = new LatLng(lat, lon);
        // playerCharcteristics.setPosition(position);
        Log.d("COORD", "" + position);
        for (int i = 0; i < AnomaliesList.length; i++) {
            String[] currentPos = AnomaliesList[i].split(",");
            Log.d("ArrayAnomalies", currentPos[0] + ", " + currentPos[1]);
            double anomalyLat = Double.parseDouble(currentPos[0]);
            double anomalyLon = Double.parseDouble(currentPos[1]);
            int anomalyRadius = Integer.parseInt(currentPos[2]);
            int anomalyPower = Integer.parseInt(currentPos[3]);
            curAnomalyPosition = new LatLng(anomalyLat, anomalyLon);
            int danger = (int) (1000 * (distanceInKmBetweenEarthCoordinates(lat, lon, curAnomalyPosition.latitude, curAnomalyPosition.longitude)));
            //String addict = Initializator.GetCurrentDF() + " Дистанция: " + danger + "Опасный радиус: " + anomalyRadius;
          //  NotifyLog(addict);
            Log.d("AnDist", "Позиция: "+i+", " + danger+", "+lat+", "+lon);
            if (danger < anomalyRadius) {
                loc_vibration();
                playerCharcteristics.setDistance(80);
                playerCharcteristics.setLoctime(localtimer);
                myAudioManager.PlaySound(PDA_AudioManager.AppSounds.RADIATION);
                if (event_state > 30) {
                    //addict = Initializator.GetCurrentDF() + ".  " + "Аномалия: Радиация " + "5-го уровня";
                   // NotifyLog(addict);
                    event_state = 0;
                }
                int rad_effect = (Math.abs((anomalyPower * (100 - playerCharcteristics.GetRadResist())) / 10));
                if (playerCharcteristics.isDead() == false) {
                    playerCharcteristics.IncreaseRad(rad_effect);
                }
                myAudioManager.PlaySound(PDA_AudioManager.AppSounds.RADIATION);
                place = Places.Radiation;
                NotifyActivity("PLACE");
                Log.d("Anomaly", "RAD");
            }

            if (danger > anomalyRadius && danger < (anomalyRadius+5)) {
                loc_vibration();
                playerCharcteristics.setDistance(80);
                playerCharcteristics.setLoctime(localtimer);
                myAudioManager.PlaySound(PDA_AudioManager.AppSounds.RADIATION);
                playerCharcteristics.IncreaseRad(0);
                myAudioManager.PlaySound(PDA_AudioManager.AppSounds.RADIATION);
                place = Places.Radiation;
                NotifyActivity("PLACE");
                Log.d("Anomaly", "RAD");
            }
        }
    }





}
