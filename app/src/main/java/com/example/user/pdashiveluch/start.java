package com.example.user.pdashiveluch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.pdashiveluch.classes.PDA_AudioManager;
import com.example.user.pdashiveluch.classes.PlayerCharcteristics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class start extends AppCompatActivity {
    private String t="0";
    private int orient=0;
    private int zivert=0;
    ImageButton Dolg;
    ImageButton Stalker;
    ImageButton Band;
    ImageButton Free;
    ImageButton Sci;
    ImageButton Monolit;
    ImageView dolg1,freed1,sci1,mon1,voln1,band1, mil1,loadinfo,naem,nebo,logo,voenstal,renegade,monster;
    EditText NameSt;
    TextView TT, topTT;
    public int mStreamId;

    private int group=1;
    private String Name;
    private int expa=1;
    private int suit=1, medikits=2, antirads=2;
    private boolean art1=false, art2=false,art3=false,art4=false,art5=false;
    private String passcode;
    private String passid;

    String sbname;


    public PDA_AudioManager myAudioManager;






    public boolean NeedStartService(){

        boolean tStartService = true;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
        String ServiceName=ShiveluchService.class.getName();
        for (int i=0; i<rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            if(ServiceName.equalsIgnoreCase(rsi.service.getClassName())){
                tStartService = false;
                break;
            }
        }
        return tStartService;

    }


    @Override
    protected void onDestroy() {
        //HermesEventBus.getDefault().destroy();
        Log.d("жопонька","onDestroy активности start");

        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("жопонька","onCreate активности start");
        start_sound();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        myAudioManager=new PDA_AudioManager(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dolg1=findViewById(R.id.dolg1);
        freed1=findViewById(R.id.freed1);
        sci1=findViewById(R.id.sci1);
        mon1=findViewById(R.id.mon1);
        voln1=findViewById(R.id.voln1);
        band1=findViewById(R.id.band1);
        mil1=findViewById(R.id.mil1);
        loadinfo=findViewById(R.id.loadinfo);
        naem=findViewById(R.id.naem);
        nebo=findViewById(R.id.cs);
        logo=findViewById(R.id.imageView);
        voenstal=findViewById(R.id.voenstal);
        renegade=findViewById(R.id.renegades);
        monster=findViewById(R.id.monster);

        topTT=findViewById(R.id.textView3);


        NameSt=findViewById(R.id.editText2);
        TT=findViewById(R.id.TT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

       /* logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orient==0)
                {
                    orient=1;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }
                else
                {
                    orient=0;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

            //    Toast.makeText(getApplicationContext(), ""+orient, Toast.LENGTH_SHORT).show();

            }
        });
*/ //смена ориентации

        loadinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbname=Initializator.GetAddress()+NameSt.getText().toString();
                passid=NameSt.getText().toString();
                passcode=NameSt.getText().toString();
                Toast.makeText(getApplicationContext(), "Загрузка...", Toast.LENGTH_LONG).show();
                getJSON(sbname);
                if(CheckFillField())
                    got(true);

                            }
        });

        nebo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = NameSt.getText().toString();
                TT.setText(" Чистое небо");
                t = TT.getText().toString();
                group=9;
                if(CheckFillField())
                    got(true);
            }
        });
        voenstal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = NameSt.getText().toString();
                TT.setText(" Военный сталкер");
                t = TT.getText().toString();
                group=10;
                if(CheckFillField())
                    got(true);
            }
        });

        renegade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = NameSt.getText().toString();
                TT.setText(" Ренегаты");
                t = TT.getText().toString();
                group=11;
                if(CheckFillField())
                    got(true);
            }
        });
        monster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = NameSt.getText().toString();
                TT.setText(" Чистое небо");
                t = TT.getText().toString();
                group=12;

                if(CheckFillField())
                    got(true);
            }
        });

        voln1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("Вольный сталкер");
                t = TT.getText().toString();
                group=1;
                if(CheckFillField())
                    got(true);

            }
        });
        dolg1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("ДОЛГ");
                t = TT.getText().toString();
                group=2;
                if(CheckFillField())
                    got(true);
            }
        });
        band1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("БАНДИТЫ");
                t = TT.getText().toString();
                group=3;
                if(CheckFillField())
                    got(true);
            }
        });
        freed1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("СВОБОДА");
                t = TT.getText().toString();
                group=4;
                if(CheckFillField())
                    got(true);
            }
        });
        sci1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("УЧЕНЫЕ");
                t = TT.getText().toString();
                group=5;
                if(CheckFillField())
                    got(true);
            }
        });
        mon1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("МОНОЛИТ");
                t = TT.getText().toString();
                group=6;
                if(CheckFillField())
                    got(true);
            }
        });

        mil1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Обработка нажатия
                Name = NameSt.getText().toString();
                TT.setText("Военные");
                t = TT.getText().toString();
                group=7;
                if(CheckFillField())
                    got(true);
            }
        });
        naem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = NameSt.getText().toString();
                TT.setText("НАЕМНИКИ");
                t = TT.getText().toString();
                group=8;
                if(CheckFillField())
                    got(true);
            }
        });

       // myAudioManager=new PDA_AudioManager(this);


    }

    private boolean ResetPlayer(){
        Log.d("жопонька","начало загрузки состояния сервиса");
        SharedPreferences servicePreferences=getSharedPreferences("datapack",MODE_PRIVATE);
        boolean result=servicePreferences.getBoolean("ResetPlayer",true);
        if(!result){
            group=servicePreferences.getInt("GroupID",1);
            Name=servicePreferences.getString("Name","Ошибка");
            //expa=servicePreferences.getInt("Exp",1);
            //suit=servicePreferences.getInt("SuitID",1);
            //medikits=servicePreferences.getInt("Medkits",2);
            //antirads=servicePreferences.getInt("Antirads",2);


            //private String passcode;
            //private String passid;
        }
        return result;





    }

    @Override
    protected void onStart() {
        Log.d("жопонька","onStart активности start");

        super.onStart();

        myAudioManager.PlaySound(PDA_AudioManager.AppSounds.MENU);
        Toast.makeText(getApplicationContext(), ""+PDA_AudioManager.AppSounds.MENU, Toast.LENGTH_LONG).show();


       // start_sound();
        boolean reset=ResetPlayer();

        if(!reset){
            got(false);
        } else
            myAudioManager.PlaySound(PDA_AudioManager.AppSounds.MENU);




    }

    private boolean CheckFillField(){
        if (NameSt.getText().toString().length()<1)
        {
            Toast.makeText(getApplicationContext(), "Не введено имя сталкера/идентификатор", Toast.LENGTH_LONG).show();
            return false;
        } else{
            return true;
        }
    }
    public void got(boolean Reset){
        boolean needStartService=NeedStartService();
        if(needStartService){
            Log.d("жопонька","стартуем сервис");
            Intent intent2 = new Intent(this, ShiveluchService.class);
            intent2.setPackage("com.example.user.pdashiveluch");
            intent2.putExtra("ResetPlayer",Reset);
            intent2.putExtra("Name",Name);
            intent2.putExtra("GroupID",group);
            intent2.putExtra("Exp",expa);
            startService(intent2);
        }

        Log.d("жопонька","стартуем активность ПДА");
        Intent intent=new Intent(getApplicationContext(), pda.class);
        intent.putExtra("Group",group);
        intent.putExtra("Name",Name);
        intent.putExtra("ResetPlayer",Reset);
        intent.putExtra("PassCode",passcode);
        intent.putExtra("PassID",passid);
        startActivity(intent);


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
                if (s!=null) {
                    try {


                        if (urlWebService == sbname) {
                            loadstalker(s);
                            //Log.d("JSON","Load JSON from "+Initializator.GetAddress());
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


        Log.d("JSON","Сталкер загружен");
        String [] callsign_s = new String[jsonArray.length()];
        String [] group_id_s = new String[jsonArray.length()];
       String [] expa_s = new String[jsonArray.length()];
       // String [] suit_s = new String[jsonArray.length()];
       // String [] medikit_s = new String[jsonArray.length()];
      //  String [] antirad_s = new String[jsonArray.length()];
      //  String [] art1_s = new String[jsonArray.length()];
      //  String [] art2_s = new String[jsonArray.length()];
      //  String [] art3_s = new String[jsonArray.length()];
      //  String [] art4_s = new String[jsonArray.length()];
      //  String [] art5_s = new String[jsonArray.length()];


        //     String [] pen_sum = new String[jsonArray.length()];
        //  Toast.makeText(getApplicationContext(), ""+jsonArray.length(), Toast.LENGTH_LONG).show();

        if (jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                callsign_s[i] = obj.getString("callsign");
                group_id_s[i] = obj.getString("group_id");
                expa_s[i] = obj.getString("exp");
              //  suit_s[i] = obj.getString("suit");
              //  medikit_s[i] = obj.getString("medikit");
              //  antirad_s[i] = obj.getString("antirad");
             //   art1_s[i] = obj.getString("art1");
             //   art2_s[i] = obj.getString("art2");
             //   art3_s[i] = obj.getString("art3");
             //   art4_s[i] = obj.getString("art4");
             //   art5_s[i] = obj.getString("art5");
                //   pen_serg_s[i] = obj.getString("serg");
                //   pen_sum[i]="НАРУШЕНИЕ: "+pen_type_s[i]+"\n"+"ДАТА: "+pen_date_s[i]+"\n"+"Автор жалобы: "+pen_serg_s[i];

            }
            Name=callsign_s[0];
            group=Integer.parseInt(group_id_s[0]);
            expa=Integer.parseInt(expa_s[0]);
            Log.d("JSON","Is name "+Name+" is group_id "+group + " and expa "+expa);
//            suit=Integer.parseInt(suit_s[0]);
//            medikits=Integer.parseInt(medikit_s[0]);
//            antirads=Integer.parseInt(antirad_s[0]);
//            art1=(Integer.parseInt(art1_s[0])!=0);
//            art2=(Integer.parseInt(art2_s[0])!=0);
//            art3=(Integer.parseInt(art3_s[0])!=0);
//            art4=(Integer.parseInt(art4_s[0])!=0);
//            art5=(Integer.parseInt(art5_s[0])!=0);

            got(false);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Не найден идентификатор", Toast.LENGTH_LONG).show();
        }


    }

    public void start_sound()
    {

        SoundPool mSoundPool;
        int mSoundId = 1;

        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        mSoundPool.load(this, R.raw.menu, 1);

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
}
