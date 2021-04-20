package com.example.user.pdashiveluch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.os.Handler;

import com.example.user.pdashiveluch.classes.DataPack;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class pda extends AppCompatActivity {


    //region *****ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ
    String Name;
    int group;
    boolean resetPlayer;
    String passcode;
    String passid;
    String psi_helm_stat, resp_stat, prot_stat, szd_stat, tropa_stat, hunter_stat;

    String nark_stat, bolezn_stat, otrava_stat, inv_desc="";
    final ArrayList<String> eventsdata = new ArrayList<>();//история
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME = "Nickname";
    public static String APP_PREFERENCES_DEAD = "Dead";
    private GestureDetectorCompat lSwipeDetector;
    //public static int power_s; //мощность сигнала
    // требуется для передачи в класс RаdarView
    public static int qr_result = 0; //переменная для хранения результата сканирования QR-кода дя передачи из класса BARCODE
    public static int rnd1, rnd2; //случайное число для позиции отрисовки точки на радаре при нахождении в аномалии/локации
    //endregion



    //region *****ВСПОМОГАТЕЛЬНЫЕ ПЕРЕМЕННЫЕ
    private int permissionCheck; //проверка на наличие необходимых разрешений
    public Chronometer chronometer; //счетчик длительнсоти нажатия на кнопку суицида
    public String password; //ID игрока, котоырй он вводит на стартовой активити
    String sbname;//имя скрипта, котоыйр должен отработать в JSON
    public int inv_index=0;
    public int distance=0;
    String infoqr_new; //переменная для хранения описания предмета врезультате сканирования QR-кода, берется из массива results_descr[i]

    String codename; //1. Переменная для переименования блютуса телефона на имя,
    // введенное в стартовом окне 2. Переменная для хранения результатов сканирования для модуля ZXING 3. Назание текущего ранга
    DateFormat vybr;//Переменные типа Дата
    //endregion

    //region ***** ЭЛЕМЕНТЫ ИНТЕРФЕЙСА
    RelativeLayout RL3, RL4, RL5;
    //ImageButton qrscaner;
    ExpandableListView item_list;
    RadarView mRadarView = null;
    Button slot1, slot2, slot3, slot4, ev_close, rules_close;
    ProgressBar pbHorizontal, pb_rad, pr_ano, pb_suit;
    TextView tvProgressHorizontal, tv_rad, callsign,
            team_name, anomaly, inv_t, s_info, tv_suit, bolt_amount, eventtext, rank, callsign_2, faction, ranknew,
            healtext_new, radbar_new, suittext_new, inventory_new, map_new, suitname, inv_new, inventor, res_tex1, res_tex2, res_tex3,
            res_tex4, res_tex5, fire_res_new, rad_res_new, poison_res_new, psi_res_new, electro_res_new, grav_res_new, closeinv, antirad_amount, medikit_amount,
            slot1txt, slot2txt, slot3txt, slot4txt, slot5txt, artseffects, savebut, h_status, history_new, c_timer, invtext, sci_med_amount, mil_med_amount, repairs_amount;

    ImageView art_but, rules_but, master_but, OK_but, artslot1, bolt_image, log_menu,  effectlogo, vibra,vibras,
            artslot2, artslot3, artslot4, antirad1, antirad2, medikit1, medikit2,rotate, backpack,mapicon,suicide,
            history, map_but, f_but, b_but, suitface, medikit_new, antirad_new, deadpic, d_but, qrscaner, mil_med, sci_med, repairs_image;
    RelativeLayout layer2, artlayer, bottom_lay;
    ConstraintLayout cons_art;
    public static RelativeLayout artslot;
    RelativeLayout deadlayout, RL6,RL67;

    EditText code_text;
    ImageView im13, l2_img, suit_img, saver;
    ListView l2_list, artslist_list, events;
    public static int place_radar;
    int power;
    int loctime;



    public static ArrayList<String> memories = new ArrayList<>();//заметки
    //endregion



    //region связь с сервисом
    Intent intent;
    DataPack dataPack;

    private Handler handle;
    public Handler GetHandler(){
        return handle;
    }

    private void SendRequestService(){
        Log.d("жопонька","отсылаем запрос в сервис на получение первичного датапака");
        Intent InnerIntent = new Intent("ShiveluchActivity.RequestService");
        sendBroadcast(InnerIntent);
    }

    private void SendActionBroadcast(String action){
        Intent InnerIntent = new Intent("ShiveluchActivity.Action");
        InnerIntent.putExtra("ExtraAction", action);
        sendBroadcast(InnerIntent);
    }

    private void InitBroadcatReceiver(){
        registerReceiver(ServiceReceiver, new IntentFilter("ShiveluchService.StringBroadcast"));
        registerReceiver(ServiceReceiver, new IntentFilter("ShiveluchService.ToastBroadcast"));
        registerReceiver(ServiceReceiver, new IntentFilter("ShiveluchService.DatapackBroadcast"));
    }

    private final BroadcastReceiver ServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent InnerIntent) {
            //Log.d("жопонька","принят бродкаст");
            String action = "";
            action = InnerIntent.getAction();

            // When discovery finds a new device
            switch (action) {
                case "ShiveluchService.StringBroadcast":
                    Log.d("жопонька","в стартовое активити пришла строка:"+InnerIntent.getStringExtra("Message"));
                    NotifyLog(InnerIntent.getStringExtra("Message"));
                    break;
                case "ShiveluchService.ToastBroadcast":
                    NotifyToast(InnerIntent.getStringExtra("Message"));
                    break;
                case "ShiveluchService.DatapackBroadcast":
                    dataPack=(DataPack)InnerIntent.getSerializableExtra("Datapack");
                    NotifySelect(InnerIntent.getStringExtra("Event"));
                    break;
            }
        }
    };

    int r, top, left,right,bottom, qrwidth, qrheight, qrmargin, RL3top,RL3left,RL3right,RL3bottom,RL5top,RL5left,RL5right,RL5bottom;


    public void disp_count_portrait()
    {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        int dim_width=metricsB.widthPixels;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi,2 );
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        double temp_height=metricsB.heightPixels/x;


/*Log.d("Высотк",""+temp_height);
//        if (x>14)
//        {
//            top=10;
//            left=50;
//            right=70;
//            bottom=0;
//            qrwidth=90;
//            qrheight=90;
//            qrmargin=60;
//            RL3top=10;
//            RL3left=70;
//            RL3right=50;
//            RL3bottom=0;
//            RL5top=10;
//            RL5left=50;
//            RL5right=70;
//            RL5bottom=0;
//            healtext_new.setTextSize(TypedValue.COMPLEX_UNIT_PT, 4);
//            radbar_new.setTextSize(TypedValue.COMPLEX_UNIT_PT, 4);
//            suittext_new.setTextSize(TypedValue.COMPLEX_UNIT_PT, 4);
//
//            RelativeLayout.LayoutParams ParamsLeft = new RelativeLayout.LayoutParams((metricsB.widthPixels / 2 - RL3left),
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//            ParamsLeft.setMargins(RL3left, RL3top, RL3right, RL3bottom);
//            ParamsLeft.addRule(RelativeLayout.BELOW, R.id.QRscan);
//            ParamsLeft.addRule(RelativeLayout.ABOVE, R.id.log_menu);
//            RL3.setLayoutParams(ParamsLeft);
//
//
//            RelativeLayout.LayoutParams paramsQR = new RelativeLayout.LayoutParams(qrwidth, qrheight);
//            paramsQR.setMargins(qrmargin, 0, 0, 0);
//            qrscaner.setLayoutParams(paramsQR);
//
//            RelativeLayout.LayoutParams ParamsRight = new RelativeLayout.LayoutParams((metricsB.widthPixels / 2) - 70, RelativeLayout.LayoutParams.MATCH_PARENT);
//
//            ParamsRight.setMargins(metricsB.widthPixels / 2, top, right, bottom);
//            ParamsRight.addRule(RelativeLayout.BELOW, R.id.QRscan);
//            ParamsRight.addRule(RelativeLayout.ABOVE, R.id.log_menu);
//            ParamsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            RL4.setLayoutParams(ParamsRight);
//
//
//            RelativeLayout.LayoutParams ParamsInv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//
//            ParamsInv.setMargins(RL5left, RL5top, RL5right, RL5bottom);
//            ParamsInv.addRule(RelativeLayout.BELOW, R.id.QRscan);
//            ParamsInv.addRule(RelativeLayout.ABOVE, R.id.log_menu);
//
//            RL5.setLayoutParams(ParamsInv);
//            RelativeLayout.LayoutParams Paramsrad = new RelativeLayout.LayoutParams(80, 80);
//            mRadarView.setLayoutParams(Paramsrad);
//
//            RelativeLayout.LayoutParams Paramsmedrad = new RelativeLayout.LayoutParams(30,30);
//            medikit_new.setLayoutParams(Paramsmedrad);
//            antirad_new.setLayoutParams(Paramsmedrad);
//            mil_med.setLayoutParams(Paramsmedrad);
//            sci_med.setLayoutParams(Paramsmedrad);
//
//
//
//        }
//
//*/

            if (dim_width <= 600) {
            top = 10; left = 50; right = 70; bottom = 0; qrwidth = 90; qrheight = 90;qrmargin = 60;
                RL3top = 10; RL3left = 70; RL3right = 50; RL3bottom = 0; RL5top = 10; RL5left = 50;
                RL5right = 70; RL5bottom = 0;
            }

            if (dim_width > 600 && dim_width <= 800) {
                Log.d("Ширина", "Получилось: " + metricsB.widthPixels);
                //Toast.makeText(getApplicationContext(), "Б600",Toast.LENGTH_SHORT);
                top = 30;
                left = 80;
                right = 90;
                bottom = 0;
                qrwidth = 90;
                qrheight = 90;
                qrmargin = 60;
                RL3top = 30;
                RL3left = 100;
                RL3right = 50;
                RL3bottom = 0;
                RL5top = 50;
                RL5left = 140;
                RL5right = 100;
                RL5bottom = 0;


            }

            if (dim_width > 800 && dim_width <= 1000) {
                Log.d("Ширина", "Получилось: " + metricsB.widthPixels);
                //Toast.makeText(getApplicationContext(), "Б600",Toast.LENGTH_SHORT);
                top = 30;
                left = 80;
                right = 90;
                bottom = 0;
                qrwidth = 90;
                qrheight = 90;
                qrmargin = 60;
                RL3top = 30;
                RL3left = 100;
                RL3right = 50;
                RL3bottom = 0;
                RL5top = 50;
                RL5left = 140;
                RL5right = 100;
                RL5bottom = 0;

            }

            if (dim_width > 1000) {
                Log.d("Ширина", "Получилось: " + metricsB.widthPixels);
                //Toast.makeText(getApplicationContext(), "Б600",Toast.LENGTH_SHORT);
                top = 140;
                left = 50;
                right = 140;
                bottom = 0;
                qrwidth = 140;
                qrheight = 140;
                qrmargin = 160;
                RL3top = 120;
                RL3left = 140;
                RL3right = 100;
                RL3bottom = 0;
                RL5top = 120;
                RL5left = 140;
                RL5right = 100;
                RL5bottom = 0;



            }


            //faction.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
          /*  Toast.makeText(getApplicationContext(), "[Используя ресурсы] \n" +
                    "Ширина: " + displaymetrics.widthPixels + "\n" +
                    "Высота: " + displaymetrics.heightPixels + "\n"
                    + "\n" +
                    "[Используя Display] \n" +
                    "Ширина: " + metricsB.widthPixels + "\n" +
                    "Высота: " + metricsB.heightPixels + "\n", Toast.LENGTH_LONG).show();
*/

            RelativeLayout.LayoutParams ParamsLeft = new RelativeLayout.LayoutParams((metricsB.widthPixels / 2 - RL3left),
            RelativeLayout.LayoutParams.MATCH_PARENT);
            ParamsLeft.setMargins(RL3left, RL3top, RL3right, RL3bottom);
            ParamsLeft.addRule(RelativeLayout.BELOW, R.id.QRscan);
            ParamsLeft.addRule(RelativeLayout.ABOVE, R.id.log_menu);
            RL3.setLayoutParams(ParamsLeft);

            RelativeLayout.LayoutParams paramsQR = new RelativeLayout.LayoutParams(qrwidth, qrheight);
            paramsQR.setMargins(qrmargin, 0, 0, 0);
            qrscaner.setLayoutParams(paramsQR);

            RelativeLayout.LayoutParams ParamsRight = new RelativeLayout.LayoutParams((metricsB.widthPixels / 2) - 70,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            ParamsRight.setMargins(metricsB.widthPixels / 2, top, right, bottom);
            ParamsRight.addRule(RelativeLayout.BELOW, R.id.QRscan);
            ParamsRight.addRule(RelativeLayout.ABOVE, R.id.log_menu);
            ParamsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


            RelativeLayout.LayoutParams ParamsInv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            ParamsInv.setMargins(RL5left, RL5top, RL5right, RL5bottom);
            ParamsInv.addRule(RelativeLayout.BELOW, R.id.QRscan);
            ParamsInv.addRule(RelativeLayout.ABOVE, R.id.log_menu);
            RL5.setLayoutParams(ParamsInv);







    }


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

    //endregion

    private String[] BackpackGroups = new String[] { "Артефакты", "Снаряжение", "Инвентарь"};

    private String[] ArtefactGroup = new String[]{"Артефакт 1","Артефакт 2","Артефакт 3",};
    private String[] KitGroup = new String[]{"Снаряга 1", "Снаряга 2", "Снаряга 3"};
    private String[] InventoryGroup = new String[] {"Аптечки: х20", "Антирад: x20"};

    //region Описание событий при считывании кодов
    String[] results_descr = {"Сталкерская куртка", "Кольчужная куртка бандитов", "Броня \"Долга\"", "Броня \"Свободы\"", "Комбинезон  \"Монолита\"",
            "Халат  ученых", "Комбинезон \"СЕВА\"", "Комбинезон \"Заря\"", "Комбинезон \"ЭКОЛОГ\"",
            "Броня военных", "Артефакт КОЛОБОК", "Артефакт Капля",
            "Артефакт Каменный цветок", "Артефакт Колючка", "Артефакт Вспышка", "Ремонт костюма", "Восстановление антирада", "Восстановление аптечек",
            "Опыт: 10 ед.", "Опыт: 50 ед.", "Наркозависимоть", "Излечение от наркозависимости", "Экзоскелет \"Свободы\"", "ПСИ-шлем", "Экзоскелет \"Долга\"",
            "Экзоскелет \"Монолита\"", "Смерть", "Оживление", "Комбинезон наемников", "Комбинезон Берилл"};
    //endregion

    //region ***** РУДИМЕНТЫ ПРОШЛЫХ ВЕРСИЙ

    //DialogFragment dlgdead;
    //int[] img = {R.drawable.fire2, R.drawable.grav, R.drawable.pois2, R.drawable.rad2,
   //         R.drawable.psi2};

    ArrayAdapter<String> adapter = null;

    private final String TAG = this.getClass().getSimpleName();

    private static final int SWIPE_MIN_DISTANCE = 130;
    private static final int SWIPE_MAX_DISTANCE = 300;
    private static final int SWIPE_MIN_VELOCITY = 200;
    public static String mess = " ";


    public String s_inform;
    //endregion



    //region notify

    private void NotifyZombi()
    {
        deadlayout.setVisibility(View.VISIBLE);
        deadpic.setImageResource(R.drawable.zomb);
        RelativeLayout.LayoutParams pic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        pic.addRule(RelativeLayout.CENTER_IN_PARENT);
        deadpic.setLayoutParams(pic);
        deadpic.setAlpha((float)1);

    }

    private void NotifyZombi04()
    {
        deadlayout.setVisibility(View.VISIBLE);
        deadpic.setImageResource(R.drawable.zomb);
        RelativeLayout.LayoutParams pic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        pic.addRule(RelativeLayout.CENTER_IN_PARENT);
        deadpic.setLayoutParams(pic);
        deadpic.setAlpha((float)0.4);

    }

    private void NotifyZombi07()
    {
        deadlayout.setVisibility(View.VISIBLE);
        deadpic.setImageResource(R.drawable.zomb);
        RelativeLayout.LayoutParams pic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        pic.addRule(RelativeLayout.CENTER_IN_PARENT);
        deadpic.setLayoutParams(pic);
        deadpic.setAlpha((float)0.7);

    }

    private void NotifyVybros()
    {
        deadlayout.setVisibility(View.VISIBLE);
        deadpic.setImageResource(R.drawable.blow);
        RelativeLayout.LayoutParams pic = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        pic.addRule(RelativeLayout.CENTER_IN_PARENT);
        deadpic.setLayoutParams(pic);
        deadpic.setAlpha((float)0.5);

    }

    private void NotifyNoZombi()
    {
        deadlayout.setVisibility(View.GONE);
    }

    private void NotifyDead() { //сдох савсэм
        //Vibro();
        if (dataPack.dead) {
            if (dataPack.zombi) {
                NotifyZombi();

            } else {
                callsign_2.setText(dataPack.name); //отображение имени без надписи DEAD в случае смети
                faction.setText(dataPack.group_name);
                deadlayout.setVisibility(View.VISIBLE);
                deadpic.setAlpha((float)1);
                deadpic.setImageResource(R.drawable.dead);
                RelativeLayout.LayoutParams pic = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                pic.addRule(RelativeLayout.CENTER_IN_PARENT);
                deadpic.setLayoutParams(pic);
            }
            //if(!dlgdead.isVisible())
//                dlgdead.show(getFragmentManager(), "dlgdead");
        } else {
            callsign_2.setText(dataPack.name); //отображение имени без надписи DEAD в случае смети
            deadlayout.setVisibility(View.GONE);
            faction.setText(dataPack.group_name);

        }

    }

    private void NotifyPlaceChange() { //изменилось место нахождения, надо обновить надписи на экранах

        switch (dataPack.place) {

            case Controller:
                anomaly.setText("АТАКА КОНТРОЛЕРА \n Устойчивость: " + dataPack.PsiHealth);
                break;
            case Bar:
                anomaly.setText("Бар \"План Б\"");
                break;
            case MonolitPiece:
                anomaly.setText("ОСКОЛОК МОНОЛИТА");
                break;
            case ClearSky:
                anomaly.setText("База \"Чистое небо\"");
                break;
            case Monstroboi:
                anomaly.setText("Выстрел монстробоя");
                break;

            case Renegades:
                anomaly.setText("База Ренегатов");
                break;

            case Village:
                anomaly.setText("Деревня одиночек");
                break;

            case Dolg:
                anomaly.setText("Место: \nБаза \"ДОЛГ\"");
                break;
            case Military:
                anomaly.setText("Место: КПП военных");
                break;

            case Doctor:
                anomaly.setText("Место: ДОМ НА БОЛОТЕ");
                break;
            case Shelter:
                anomaly.setText("УКРЫТИЕ");
                break;
            case Mercs:
                anomaly.setText("Место: БАЗА НАЕМНИКОВ");
                break;
            case Bandos:
                anomaly.setText("Место: База БАНДИТОВ.");
                break;
            case Freedom:
                anomaly.setText("Место: База \"СВОБОДА\"");
                break;
            case Sci:
                anomaly.setText("Место: БАЗА УЧЕНЫХ");
                break;
            case Monolit:
                anomaly.setText("Место: База \"МОНОЛИТ\"");
                break;
            case Gipnos:
                anomaly.setText("Гипноз");
                RL6.setVisibility(View.VISIBLE);
                Log.d("GipnosSetByCase",""+dataPack.gipnos);

                break;

            case None:

                {anomaly.setText("Поиск...");

                if (dataPack.gipnos) {
                    Log.d("Gipnos",""+dataPack.gipnos);
                    RL6.setVisibility(View.VISIBLE);
            //        RL67.setVisibility(View.VISIBLE);
                }
                if (!dataPack.gipnos)
                {Log.d("Gipnos",""+dataPack.gipnos);
                    RL6.setVisibility(View.GONE);
              //      RL67.setVisibility(View.GONE);
                }
                if (dataPack.PsiHealth<40)
                {
                    effectlogo.setVisibility(View.VISIBLE);
                    effectlogo.setImageResource(R.drawable.brain2);
                    mRadarView.setAlpha((float)0.3);

                }

                effectlogo.setVisibility(View.INVISIBLE);
                mRadarView.setAlpha(1);}
                break;
            case Radiation:
                anomaly.setText("Аномалия: РАДИАЦИЯ");
                // power=dataPack.power;

                Log.d("spla", "in Activity"+loctime);

                if (distance>=78) effectlogo.setImageResource(R.drawable.rad1);
                if (distance<78 && distance>=60) effectlogo.setImageResource(R.drawable.rad);
                if (distance<60) effectlogo.setImageResource(R.drawable.rad2);
//                if (power>=1 && power<=3) effectlogo.setImageResource(R.drawable.rad1);
//                if (power>=4 && power<=6) effectlogo.setImageResource(R.drawable.rad);
//                if (power>=7 && power<=9) effectlogo.setImageResource(R.drawable.rad2);


                mRadarView.setAlpha((float) 0.4);
                effectlogo.setVisibility(View.VISIBLE);
                break;
            case Tramplin:
                anomaly.setText("Аномалия: \n ГРАВИКОНЦЕНТРАТ");
                if (distance>=78) effectlogo.setImageResource(R.drawable.grav1);
                if (distance<78 && distance>=60) effectlogo.setImageResource(R.drawable.grav);
                if (distance<60) effectlogo.setImageResource(R.drawable.grav2);
//                if (power>=1 && power<=3) effectlogo.setImageResource(R.drawable.grav1);
//                if (power>=4 && power<=6) effectlogo.setImageResource(R.drawable.grav);
//                if (power>=7 && power<=9) effectlogo.setImageResource(R.drawable.grav2);
                mRadarView.setAlpha((float) 0.3);
                effectlogo.setVisibility(View.VISIBLE);
                break;
            case Electra:

                if (distance>=78) effectlogo.setImageResource(R.drawable.electro1level);
                if (distance<78 && distance>=60) effectlogo.setImageResource(R.drawable.electro);
                if (distance<60) effectlogo.setImageResource(R.drawable.electro3);
                anomaly.setText("Аномалия: ЭЛЕКТРА, дистанция "+distance);
                Log.d("dist",""+distance);

//                if (power>=1 && power<=3) effectlogo.setImageResource(R.drawable.electro1level);
//                if (power>=4 && power<=6) effectlogo.setImageResource(R.drawable.electro);
//                if (power>=7 && power<=9) effectlogo.setImageResource(R.drawable.electro3);
                mRadarView.setAlpha((float) 0.3);
                effectlogo.setVisibility(View.VISIBLE);
                break;
            case Jarka:
                anomaly.setText("Аномалия: ЖАРКА");
                if (distance>=78) effectlogo.setImageResource(R.drawable.fire1);
                if (distance<78 && distance>=60) effectlogo.setImageResource(R.drawable.fire);
                if (distance<60) effectlogo.setImageResource(R.drawable.fire2);
//                if (power>=1 && power<=3) effectlogo.setImageResource(R.drawable.fire1);
//                if (power>=4 && power<=6) effectlogo.setImageResource(R.drawable.fire);
//                if (power>=7 && power<=9) effectlogo.setImageResource(R.drawable.fire2);
                mRadarView.setAlpha((float) 0.3);
                effectlogo.setVisibility(View.VISIBLE);
                break;
            case Psi:
                anomaly.setText("Аномалия: ПСИ-поле");
                if (distance>=78)effectlogo.setImageResource(R.drawable.brain1);
                if (distance<78 && distance>=60) effectlogo.setImageResource(R.drawable.brain);
                if (distance<60) effectlogo.setImageResource(R.drawable.brain2);
//                if (power>=1 && power<=3) effectlogo.setImageResource(R.drawable.brain1);
//                if (power>=4 && power<=6) effectlogo.setImageResource(R.drawable.brain);
//                if (power>=7 && power<=9) effectlogo.setImageResource(R.drawable.brain2);
                mRadarView.setAlpha((float) 0.3);
                effectlogo.setVisibility(View.VISIBLE);
                break;
            case Kisel:
                anomaly.setText("Аномалия: КИСЛОТНЫЙ ТУМАН");
                if (distance>=78)effectlogo.setImageResource(R.drawable.poison1);
                if (distance<78 && distance>=60) effectlogo.setImageResource(R.drawable.poison);
                if (distance<60) effectlogo.setImageResource(R.drawable.poison2);
//                if (power>=1 && power<=3) effectlogo.setImageResource(R.drawable.poison1);
//                if (power>=4 && power<=6) effectlogo.setImageResource(R.drawable.poison);
//                if (power>=7 && power<=9) effectlogo.setImageResource(R.drawable.poison2);
                mRadarView.setAlpha((float) 0.3);
                effectlogo.setVisibility(View.VISIBLE);
                break;
            case Mine:
                anomaly.setText("Минное поле");
                effectlogo.setImageResource(R.drawable.mine);
                mRadarView.setAlpha((float) 0.3);
                effectlogo.setVisibility(View.VISIBLE);

                break;
            case Buerer:
                anomaly.setText("Атака бюрера");
                break;
            default:
                anomaly.setText("");
                break;
        }

    }

    private void NotifyArtifactChange() {
        String description = "";

        slot1txt.setText("СЛОТ 1: " + dataPack.KolobokStatus);
        description = description + dataPack.KolobokDescription;
        slot2txt.setText("СЛОТ 2: " + dataPack.PlenkaStatus);
        description = description + dataPack.PlenkaDescription;
        slot3txt.setText("СЛОТ 3: " + dataPack.PuzirStatus);
        description = description + dataPack.PuzirDescription;
        slot4txt.setText("СЛОТ 4: " + dataPack.HeartStatus);
        description = description + dataPack.HeartDescription;
        slot5txt.setText("СЛОТ 5: " + dataPack.BatteryStatus);
        description = description + dataPack.BatteryDescription;

        artseffects.setText(description);

    }

    private void NotifyParameters() {
        radbar_new.setText("Накоплено радиации: " + (dataPack.rad/10)); //ОТОБРАЖЕНИЕ УРОВНЯ РАДИАЦИИ В ТЕКСТОВОМ ВИДЕ
        suittext_new.setText("Прочность костюма: " + Math.round(dataPack.suit_stam_big/10));//ОТОБРАЖЕНИЕ УРОВНЯ КОСТЮМА В ТЕКСТОВОМ ВИДЕ
        healtext_new.setText("Здоровье:" + Math.round(dataPack.heal_big/1000));//ОТОБРАЖЕНИЕ УРОВНЯ ЗДОРОВЬЯ В ТЕКТОВОМ ВИДЕ
        pb_rad.setProgress(dataPack.rad/10); //УСТАНОВКА НАПОЛНЕНИЯ ШКАЛЫ РАДИАЦИИ
        pbHorizontal.setProgress(Math.round(dataPack.heal_big/1000));//УСТАНОВКА НАПОЛНЕНИЯ ШКАЛЫ ЗДОРОВЬЯ
        pb_suit.setProgress(Math.round(dataPack.suit_stam_big/10)); //УСТАНОВКА НАПОЛНЕНИЯ ШКАЛЫ ПРОЧНОСТИ КОСТЮМА
        NotifyRank();

        fire_res_new.setText("" + dataPack.FireResist+ "%"); //ОТОБРАЖЕНИЕ УРОВНЯ ЗАЩИТЫ ОТ ОГНЯ
        rad_res_new.setText("" + dataPack.RadResist + "%");//ОТОБРАЖЕНИЕ УРОВНЯ ЗАЩИТЫ ОТ РАДИАЦИИ
        poison_res_new.setText("" + dataPack.PoisonResist + "%");//ОТОБРАЖЕНИЕ УРОВНЯ ЗАЩИТЫ ОТ ЯДА
        grav_res_new.setText("" + dataPack.GravResist + "%");//ОТОБРАЖЕНИЕ УРОВНЯ ЗАЩИТЫ ОТ ГРАВИТАЦИИ
        psi_res_new.setText("" + dataPack.PsiResist + "%");//ОТОБРАЖЕНИЕ УРОВНЯ ЗАЩИТЫ ОТ ПСИ-ИЗЛУЧЕНИЯ
        electro_res_new.setText("" + dataPack.ElectroResist + "%");//ОТОБРАЖЕНИЕ УРОВНЯ ЗАЩИТЫ ОТ ЭЛЕКТРИЧЕСТВА

        //pda.place_radar = place;  загадочная хуита

        //  barcode();

    }





    private void NotifySuitChange() {
        String suitName = dataPack.suitName;
        NotifyInventory();
        suitName = suitName + psi_helm_stat+ resp_stat+ prot_stat+ szd_stat+ tropa_stat+hunter_stat;
        suitname.setText(suitName);


        String addict = Initializator.GetCurrentDF() + ".  " + "Надет костюм \"" + suitName + "\"";
      //  eventsdata.add(addict);
        adapter.notifyDataSetChanged();
    }


    private void NotifyInventory()
    {

        if (dataPack.psi_helm) psi_helm_stat="+Пси-шлем"; else psi_helm_stat="";
        if (dataPack.respirator) resp_stat="+Респиратор"; else resp_stat="";
        if (dataPack.protivogas) prot_stat="+Противогаз"; else prot_stat="";
        if (dataPack.SZD) szd_stat="+Система замкнутого дыхания"; else szd_stat="";
        if (dataPack.tropa) tropa_stat="+Тропа Чистого неба"; else tropa_stat="";
        if (dataPack.hunter) hunter_stat="\nОХОТНИК"; else hunter_stat="";
    }

    private void NotifyAdept()
    {

    }

    private void NotifyOsob()
    {

    }

    private void NotifyPower()
    {
       power=dataPack.power;
       loctime=dataPack.loctime;
       distance=dataPack.distance;
       Log.d("power", "Power: "+power);
        Log.d("power", "loctime: "+loctime);
    }

    private void NotifyGipnos()
    {

        if (dataPack.gipnos) RL6.setVisibility(View.VISIBLE);
        if (!dataPack.gipnos) RL6.setVisibility(View.GONE);
    }

    private void NotifyLoctime()
    {
        loctime=dataPack.loctime;
    }

    private void NotifyEffcts()
    {
        if (dataPack.nark) nark_stat="НАРКОЗАВИСИМОСТЬ \n"; else nark_stat="";
        if (dataPack.bolezn) bolezn_stat="НЕИЗВЕСТНАЯ БОЛЕЗНЬ \n"; else bolezn_stat="";
        if (dataPack.otrava) otrava_stat="ОТРАВЛЕНИЕ \n"; else otrava_stat="";

        invtext.setText(inv_desc+"\n"+nark_stat + bolezn_stat+otrava_stat);

    }

    private void NotifyLog(String event) {

        eventsdata.add(event);
        //eventsdata.add(" "+NeedStartService());
        adapter.notifyDataSetChanged();
    }

    private void NotifyToast(String value) {
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
    }

    private void NotifyRank() {
        ranknew.setText("Ранг: " + dataPack.RankText + ". Опыт: " + dataPack.exp);
    }

    private void NotifyNark() {

        if (dataPack.nark) {
        //    h_status.setText("Наркозависимость");

        } else {

       //     h_status.setText("Здоров");


        }


    }

    private void NotifyMedkit() {
        medikit_amount.setText("x" + dataPack.medikits);


    }

    private void NotifyMilMedkit() {
        mil_med_amount.setText("x" + dataPack.mil_medikits);

    }

    private void NotifyRepairs() {
       repairs_amount.setText("x" + dataPack.repairs);

    }

    private void NotifySciMedkit() {
        sci_med_amount.setText("x" + dataPack.sci_medikits);

    }
    public static double statlan,statlon;
private void NotifyGPS()
{
    statlan=dataPack.latitude;
    statlon=dataPack.longitude;
}


    private void NotifyAntirad() {
        antirad_amount.setText("x" + dataPack.antirads);
    }

    public void NotifySelect(String value){
        //this.GetHandler().sendMessage(this.GetHandler().obtainMessage(5,ev.service));
        Log.d("жопонька","NotifySelect "+value);

        switch(value){
            case "SKIN":
               // NotifySkin1();
                break;


            case "DEAD":
                NotifyDead();
                break;
            case "ZOMBI":
                    NotifyZombi();
                    break;

            case "noZombi":
                NotifyNoZombi();
                break;
            case "PLACE":
                NotifyPower();
                NotifyLoctime();
                NotifyPlaceChange();
                NotifyGipnos();
                Log.d("pla",""+power);
                break;
            case "GIPNOS":
                NotifyGipnos();
                break;
            case "CONTROLER":
                NotifyZombi();
            break;

            case "FIRSTCONTROLER":
                NotifyZombi04();
                break;

            case "SECONDCONTROLER":
                NotifyZombi07();
                break;

            case "NOCONTROLER":
                NotifyNoZombi();
                break;

            case "VYBROS":
                effectlogo.setImageResource(R.drawable.brain2);
                effectlogo.setVisibility(View.VISIBLE);
                mRadarView.setAlpha((float)0.3);
                break;
            case "INSHELTER":
            effectlogo.setVisibility(View.GONE);
            mRadarView.setAlpha(1);
            break;

            case "ART":
                NotifyArtifactChange();
                break;
            case "MEDKIT":
                NotifyMedkit();
                break;

            case "MIL_MEDKIT":
                NotifyMilMedkit();
                break;

            case "REPAIRS":
                NotifyRepairs();
                break;


            case "SCI_MEDKIT":
                NotifySciMedkit();
                break;


            case "ANTIRAD":
                NotifyAntirad();
                break;
            case "SUIT":
                NotifySuitChange();
                break;
            case "INVENTORY":
                    NotifyInventory();
                    break;
            case "BOLT":
                NotifyBolt();
                break;
            case "ISBOLT":
                    NotifyIsbolt();
                    break;
            case "ADEPT":
                NotifyAdept();
                break;
            case "OSOB":
                NotifyOsob();
                break;
            case "POWER":
                NotifyPower();
                break;

            case "EFFECTS":
                NotifyEffcts();
                break;
            case "PARAMETERS":
                NotifyParameters();
                break;
            case "NARK":
                NotifyNark();
                break;
            case "ALL":
                NotifyDead();
                NotifyPlaceChange();
                NotifyArtifactChange();
                NotifyMedkit();
                NotifyBolt();
                NotifyAntirad();
                NotifyMilMedkit();
                NotifyRepairs();
                NotifySciMedkit();
                NotifySuitChange();
                NotifyParameters();
                NotifyNark();
                NotifyInventory();
                NotifyEffcts();
                NotifyPower();
                NotifyGPS();
                break;
            default:
                Log.d("жопонька","Неизвестный нотификатор "+value);
                break;
        }

    }

    private void NotifyBolt() {
        bolt_amount.setText("x"+dataPack.bolt);
        Log.d("Bolt",""+dataPack.bolt);
    }
    private void NotifyIsbolt()
    {
        anomaly.setText("ПРИМЕНЕН Б.О.Л.Т.");
    }

    private void NotifyNobolt()
    {
        anomaly.setText("Поиск...");

    }

    //endregion


    //region стандартные методы
    @SuppressLint("WrongViewCast")
    private void InitVisualElements(){


        password = passcode;
        sbname = Initializator.GetAddress() + password;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pda);

        mRadarView = (RadarView) findViewById(R.id.radarView);
        mRadarView.startAnimation();
        deadpic = findViewById(R.id.deadpic);
        rotate=findViewById(R.id.rotate);
        backpack=findViewById(R.id.backpack);
        mapicon=findViewById(R.id.mapicon);
        suicide=findViewById(R.id.suicide);
        vibra=findViewById(R.id.vibra);
      //  vibras=findViewById(R.id.vibras);
//
       // log_menu=findViewById(R.id.log_menu);
        invtext=findViewById(R.id.Invtext);
        effectlogo=findViewById(R.id.effectpic);
        effectlogo.setAlpha((float) 0.6);

        c_timer = findViewById(R.id.countdown);
        d_but = findViewById(R.id.d_button);
        rad_res_new = findViewById(R.id.rad_res_new);
        grav_res_new = findViewById(R.id.grav_res_new);
        psi_res_new = findViewById(R.id.psi_res_new);
        poison_res_new = findViewById(R.id.poison_res_new);
        fire_res_new = findViewById(R.id.fire_res_new);
        electro_res_new=findViewById(R.id.electro_res_new);
        inventor = findViewById(R.id.inventor_new);
        res_tex1 = findViewById(R.id.res_tex1);

        res_tex2 = findViewById(R.id.res_tex2);
        res_tex3 = findViewById(R.id.res_tex3);
        res_tex4 = findViewById(R.id.res_tex4);
        res_tex5 = findViewById(R.id.res_tex5);

        medikit_amount = findViewById(R.id.medikit_amount);
        antirad_amount = findViewById(R.id.antirad_amount);
        medikit_new = findViewById(R.id.medikit_new);
        antirad_new = findViewById(R.id.antirad_new);
        slot1txt = findViewById(R.id.slot1txt);
        slot2txt = findViewById(R.id.slot2txt);
        slot3txt = findViewById(R.id.slot3txt);
        slot4txt = findViewById(R.id.slot4txt);
        slot5txt = findViewById(R.id.slot5txt);
        artseffects = findViewById(R.id.artseffects);
        savebut = findViewById(R.id.savebut);
     //   h_status = findViewById(R.id.health_status);
        mil_med=findViewById(R.id.mil_med);
        repairs_image=findViewById(R.id.remont);
        repairs_amount=findViewById(R.id.rem_amount);
        sci_med=findViewById(R.id.sci_med);
        mil_med_amount=findViewById(R.id.mil_med_amount);
        sci_med_amount=findViewById(R.id.sci_med_amount);
        bolt_image=findViewById(R.id.bolt);
        bolt_amount=findViewById(R.id.bolt_amount);


        //invtext.setText("Псишлем: отсутствует"+"\n" +"\n" + "Респиратор: отсутствует"+"\n" +"\n" + "Противогаз: отсутствует"+"\n" +"\n" + "Система замкнутого дыхания: отсутствует"+"\n" +"\n" + "Тайная тропа ЧН: отсутствует");
        //item_list=findViewById(R.id.items_list);
       // item_list.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        //СЧЕТЧИК ТИКОВ ХРОНОМЕТРА, ЗАПУСКАЕТСЯ ПРИ УДЕРЖИВАНИИ КНОПКИ СУИЦИДА

        vibra.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SendActionBroadcast("setVibro");

            }
        });
//        vibras.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SendActionBroadcast("setVibro");
//            }
//        });

        rotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        mapicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), map.class));
            }
        });

        suicide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        bolt_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inv_index=1;
                Log.d("bolt", "Нажат болт "+inv_index);
                InvMenu(v);
            }
        });

        //ОБРАБОТЧИК ДЛИТЕЛЬНОГО НАЖАТИЯ КНОПКИ СУИЦИДА


//       log_menu.setOnClickListener(new View.OnClickListener());
//
//       {
//           @Override
//           public boolean onLongClick(View v) {
//               showPopupMenu(v);
//               return false;
//           }
//       });

        // menubut=findViewById(R.id.RL6);


        //ОБРАБОТЧИК НАЖАТИЯ НА СЛОТ № 1 С АРТЕФАКТОМ
        slot1txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //колобок
                SendActionBroadcast("KolobokToggle");
            }
        });


//ОБРАБОТЧИК НАЖАТИЯ НА СЛОТ № 2 С АРТЕФАКТОМ
        slot2txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //пленка
                SendActionBroadcast("PlenkaToggle");
            }
        });


        //ОБРАБОТЧИК НАЖАТИЯ НА СЛОТ № 2 С АРТЕФАКТОМ
        slot3txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //пузырь
                SendActionBroadcast("PuzirToggle");
            }
        });


        //ОБРАБОТЧИК НАЖАТИЯ НА СЛОТ № 2 С АРТЕФАКТОМ
        slot4txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //сердце оазиса
                SendActionBroadcast("HeartToggle");
            }
        });

        slot5txt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //батарейка
                SendActionBroadcast("BatteryToggle");
            }
        });
        ///ОБРАБОТЧИК КНОПКИ СОХРАНЕНИЯ
//        savebut.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {  //с этим отдельно надо разобраться
//
//                if (!dataPack.dead) {
//                    SendActionBroadcast("SaveData");
//                } else {
//                    NotifyToast("Мертвые не сохраняются...");
//                }
//            }
//        });


        RL3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        RL5=findViewById(R.id.relativeLayout5);

        RL3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return lSwipeDetector.onTouchEvent(event);
            }
        });

        closeinv = findViewById(R.id.closeinv);
        RL3.setVisibility(View.VISIBLE);
        RL5.setVisibility(View.GONE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //suit = start.suit;
        suitname = findViewById(R.id.suitname);
        suittext_new = findViewById(R.id.suittext_new);
        //exp = start.expa;




        //df = new SimpleDateFormat("HH:mm");


        eventtext = findViewById(R.id.event_text);

        inv_new = findViewById(R.id.inv_new);

        events = findViewById(R.id.eventslist);

        adapter = new ArrayAdapter<>(this,
                R.layout.event_list, R.id.event_text, eventsdata);
        // Привяжем массив через адаптер к ListView
        events.setAdapter(adapter);

        map_new = findViewById(R.id.map_new);

        healtext_new = findViewById(R.id.healtext_new);
        //   dbHelper = new DBHelper(this);

        radbar_new = findViewById(R.id.radbar_new);

        qrscaner = findViewById(R.id.QRScan);
        ranknew = findViewById(R.id.ranknew);

        callsign_2 = findViewById(R.id.callsign2);

        pbHorizontal = findViewById(R.id.pb_heal);
        suitface = findViewById(R.id.suitface);

        pb_rad = findViewById(R.id.pb_rad);

        pb_suit = findViewById(R.id.pb_suit);

        faction = findViewById(R.id.faction);

        deadlayout = findViewById(R.id.deadlayout);


        anomaly = findViewById(R.id.anomaly_id);
        RL6=findViewById(R.id.RL6);
       // RL67=findViewById(R.id.RL6);

        //dlgdead = new dead_dialog();
        //dlgdead.show(getFragmentManager(), "dlgdead");


        //Установка шрифтов для некоторых текстоых элементов
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/graffiti.ttf");
        ranknew.setTypeface(face);
        callsign_2.setTypeface(face);
        faction.setTypeface(face);
        anomaly.setTypeface(face);
        suitname.setText("");
        //disp_count_portrait();


        //ОБРАБОТЧИК НАЖАТИЯ НА ЗНАЧОК АПТЕЧКИ
        medikit_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inv_index=2;
                InvMenu(v);
            }
        });

        mil_med.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inv_index=3;
                InvMenu(v);
            }
        });

        repairs_image.setOnClickListener(new OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 inv_index=4;
                                                 InvMenu(v);
                                             }
                                         });

                sci_med.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inv_index=5;
                        InvMenu(v);
                    }
                });

//ОБРАБОТЧИК НАЖАТИЯ НА АНТИРАД
        antirad_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inv_index=6;

                InvMenu(v);            }
        });

        //ОСТАНОВКА ВИБРАЦИИ ПРИ НАЖАТИИ НА ЛЕВУЮ ПАНЕЛЬ
        RL3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.cancel();
            }
        });
//ОСТАНОВКА ВИБРАЦИИ ПРИ НАЖАТИИ НА ПАВВУЮ ПАНЕЛЬ



        //ОБРАБОТЧИК НАЖАТИЯ НА КНОПКУ ИНВЕНТАРЬ
        //inventor.setOnClickListener(new OnClickListener() {
        suitface.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RL3.setVisibility(View.GONE);

                RL5.setVisibility(View.VISIBLE);
            }
        });

        backpack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RL3.setVisibility(View.GONE);
                RL5.setVisibility(View.VISIBLE);
            }
        });


        //ОБРАБОТЧИК НАЖАТИЯ Н АКНОПКУ ЗАКРЫТИЯ ИНВЕНТАРЯ
        closeinv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RL3.setVisibility(View.VISIBLE);

                RL5.setVisibility(View.GONE);
            }
        });


        //ОБРАБОТЧИК НАЖАТИЯ НА КНОПКУ КАРТА
        //map_new.setOnClickListener(new OnClickListener() {
        mRadarView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), gps_activity.class));
            }
        });

//        item_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition,   int childPosition, long id) {
//               NotifyToast("Номер группы: " + groupPosition +
//                        " Номер позиции списка: " + childPosition);
//                return false;
//            }
//        });


        final Activity activity = this;


//ОБРАБОТЧИК НАЖАТИЯ ДЛЯ ЗАПУСКА АКТУАЛЬНОГО СКАНЕРА QR-КОДОВ. кОРОТКОЕ НАЖАТИЕ, ЗАПУСК ФРОНТАЛЬНОЙ КАМЕРЫ
        qrscaner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt(" ");
                integrator.setCameraId(1);
                integrator.autoWide();
                //         integrator.setBeepEnabled(false);
                //     integrator.setBarcodeImageEnabled(false);
                try {
                    integrator.initiateScan();
                } catch (Exception e){
                    NotifyLog("Не удалось авктивировать сканер кодов "+e.getMessage());
                    Log.d("жопонька", "Не удалось авктивировать сканер кодов "+e.getMessage());
                }
                //f_but.setVisibility(View.GONE);
               // b_but.setVisibility(View.GONE);
            }
        });

//ОБРАБОТЧИК НАЖАТИЯ ДЛЯ ЗАПУСКА АКТУАЛЬНОГО СКАНЕРА QR-КОДОВ. ДЛИННОЕ НАЖАТИЕ, ЗАПУСК ТЫЛОВОЙ КАМЕРЫ
        qrscaner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //


                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt(" ");
                integrator.setCameraId(0);
                integrator.autoWide();
                //         integrator.setBeepEnabled(false);
                //     integrator.setBarcodeImageEnabled(false);
                try {
                    integrator.initiateScan();
                } catch (Exception e){
                    NotifyLog("Не удалось авктивировать сканер кодов "+e.getMessage());
                    Log.d("жопонька", "Не удалось авктивировать сканер кодов "+e.getMessage());
                }

                return false;
            }
        });


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //No call for super(). Bug on API Level > 11.
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(NeedStartService()){
            Log.d("жопонька","инициируем запуск сервиса из активити ПДА");
            intent = new Intent(this, ShiveluchService.class);
            intent.setPackage("com.example.user.pdashiveluch");
            intent.putExtra("ResetPlayer",resetPlayer);
            intent.putExtra("Name",Name);
            intent.putExtra("GroupID",group);
            startService(intent);
        } else{
            Log.d("жопонька","в активити ПДА обнаружен старый ранее запущенный сервис");
        }




    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("жопонька","onCreate активити ПДА");
        super.onCreate(savedInstanceState);
      //  disp_count();
        Intent myIntent=getIntent();
        Name=myIntent.getStringExtra("Name");
        group=myIntent.getIntExtra("Group",1);
        lSwipeDetector = new GestureDetectorCompat(this, new SwipeListener());
        resetPlayer=myIntent.getBooleanExtra("ResetPlayer",true);
        passcode=myIntent.getStringExtra("PassCode");
        passid=myIntent.getStringExtra("PassID");
        // ПРОВЕРКА НАЛИЧИЯ НЕОБХОДИМЫХ РАЗРЕШЕНИЙ
        permissionCheck = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
           // permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            //permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 50);

            /*
        if (permissionCheck != 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }

             */



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }

        InitVisualElements();

        vybr = new SimpleDateFormat("HH:mm");
        BackPack();







    }

    @Override
    protected void onDestroy() {
        //HermesEventBus.getDefault().destroy();
        Log.d("onDestroy","onDestroy активности pda");
        try
        {unregisterReceiver(ServiceReceiver);}
        catch (Exception e)
        {
            Log.d("onDestroy", "не удалось он дестрой");
        }
        super.onDestroy();

        //canceldead();

    }


    @Override
    protected void onPause() {
        unregisterReceiver(ServiceReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitBroadcatReceiver();
        SendRequestService();
    }



    //endregion

    //region интерактивная работа
    //ВИБРАЦИЯ ПРИ СМЕРТИ
    /*
    private void Vibro() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }


    }

    //ЗАКРЫТИЕ ОКНА ВОЗНИКШЕГО ПРИ СМЕРТИ ИГРОКА
    public void canceldead() {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //fragment = 0;
        //Зачем при смерти выставлять эти параметры?

        vibrator.cancel();
    }

     */
    //ПО НАЖАТИЮ НА КНОПКУ "НАЗАД" НА ТЕЛЕФОНЕ
    @Override
    public void onBackPressed() {

        RL3.setVisibility(View.VISIBLE);

        RL5.setVisibility(View.GONE);
        // rules_layout.setVisibility(View.GONE);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.cancel();
        SendActionBroadcast("VibrateCancel");

    }

    //endregion
    //region Работа с QR

    //ИТОГ РАБОТЫ СКАНЕРА QR-КОДОВ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String[] allcodes=Initializator.GetQRCodes();
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("жопонька", "Cancelled scan");
                NotifyToast("Cancelled");
            } else {
                Log.d("жопонька", "Scanned");
                codename = result.getContents();
                for (int i = 0; i < allcodes.length; i++) {
                    if (codename.equals(allcodes[i])) {
                        //NotifyToast("Найдено: " + results_descr[i]);
                       // infoqr_new = results_descr[i];

                    }
                }

                barcode();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                     SendActionBroadcast("GiveSuicide");
                                return true;
                            case R.id.menu2:
                                Toast.makeText(getApplicationContext(),
                                        "Как прекрасен этот мир - посмотри!",
                                        Toast.LENGTH_LONG).show();

                                return true;


                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
               // Toast.makeText(getApplicationContext(), "Все ушло",
                      //  Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }

public void BackPack()

{
    Map<String, String> map;

    ArrayList<Map<String, String>> groupDataList = new ArrayList<>();


    for (String group : BackpackGroups) {

        map = new HashMap<>();
        map.put("groupName", group); // Список видов имущества
        groupDataList.add(map);
    }


    String groupFrom[] = new String[] { "groupName" };

    int groupTo[] = new int[] { android.R.id.text1 };

    // создаем общую коллекцию для коллекций элементов
    ArrayList<ArrayList<Map<String, String>>> childDataList = new ArrayList<>();

    ArrayList<Map<String, String>> сhildDataItemList = new ArrayList<>();

    for (String item : ArtefactGroup) {
        map = new HashMap<>();
        map.put("itemName", item); //
        сhildDataItemList.add(map);
    }

    childDataList.add(сhildDataItemList);


    сhildDataItemList = new ArrayList<>();
    for (String item : KitGroup) {
        map = new HashMap<>();
        map.put("itemName", item);
        сhildDataItemList.add(map);
    }
    childDataList.add(сhildDataItemList);

    // создаем коллекцию элементов для третьей группы
    сhildDataItemList = new ArrayList<>();
    for (String item : InventoryGroup) {
        map = new HashMap<>();
        map.put("itemName", item);
        сhildDataItemList.add(map);
    }
    childDataList.add(сhildDataItemList);


    String childFrom[] = new String[] { "itemName" };

    int childTo[] = new int[] { android.R.id.text1 };

    SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
            this, groupDataList,
            android.R.layout.simple_expandable_list_item_1, groupFrom,
            groupTo, childDataList, android.R.layout.simple_list_item_1,
            childFrom, childTo);

    //item_list.setAdapter(adapter);
}


    public void barcode() {
        Log.d("жопонька", "Парсинг QR кода");
        pda.qr_result = 0;
        String[] allcodes=Initializator.GetQRCodes();
        char infoqr = barcode.mess.toString().charAt(0);
        char infoqr2 = codename.charAt(0);
        //  Toast.makeText(getApplicationContext(), codename, Toast.LENGTH_SHORT).show();

        String infoqr_str = "" + infoqr;
        String infoqr_str2 = "" + infoqr2;
        if (infoqr_str.toString().equals("И")) {
            NotifyToast("Найдена информация, записана в историю");

            String addict = Initializator.GetCurrentDF() + ". Найдено: " + barcode.mess.toString();
            eventsdata.add(addict);
            adapter.notifyDataSetChanged();

        }
        if (infoqr_str2.toString().equals("И")) {
            NotifyToast("Найдена информация, записана в историю");

            String addict = Initializator.GetCurrentDF() + ". Найдено: " + codename;

            eventsdata.add(addict);
            adapter.notifyDataSetChanged();
            infoqr_new = "";
            infoqr_str2 = "";
        }


        if (codename.equals(allcodes[0])) {
            SendActionBroadcast("GiveSuitKurtka");
        }

        if (codename.equals(allcodes[1])) {
            SendActionBroadcast("GiveSuitZaria");
        }

        if (codename.equals(allcodes[2])) {
            SendActionBroadcast("GiveSuitSeva");
        }

        if (codename.equals(allcodes[3])) {
            SendActionBroadcast("GiveSuitExo");
        }

        if (codename.equals(allcodes[4])) {
            SendActionBroadcast("GiveSuitBand");
        }

        if (codename.equals(allcodes[5])) {
            SendActionBroadcast("GiveSuitPlas");
        }

        if (codename.equals(allcodes[6])) {
            SendActionBroadcast("GiveSuitBron");
        }
        if (codename.equals(allcodes[7])) {
            SendActionBroadcast("GiveSuitBulat");
        }
        if (codename.equals(allcodes[8])) {
            SendActionBroadcast("GiveSuitBerill");
        }

        if (codename.equals(allcodes[9])) {
            SendActionBroadcast("GiveSuitDolgKombez");
        }

        if (codename.equals(allcodes[10])) {
            SendActionBroadcast("GiveSuitDolgBron");
        }
        if (codename.equals(allcodes[11])) {
            SendActionBroadcast("GiveSuitDolgExa");
        }

        if (codename.equals(allcodes[12])) {
            SendActionBroadcast("GiveSuitStrazh");
        }

        if (codename.equals(allcodes[13])) {
            SendActionBroadcast("GiveSuitVeter");
        }

        if (codename.equals(allcodes[14])) {
            SendActionBroadcast("GiveSuitFreeExa");
    }

        if (codename.equals(allcodes[15])) {
            SendActionBroadcast("GiveSuitMonKombez");
        }

        if (codename.equals(allcodes[16])) {
            SendActionBroadcast("GiveSuitMonBron");
        }
        if (codename.equals(allcodes[17])) {
            SendActionBroadcast("GiveSuitMonExa");
        }

        if (codename.equals(allcodes[18])) {
            SendActionBroadcast("GiveSuitNaemKombez");
        }

        if (codename.equals(allcodes[19])) {
            SendActionBroadcast("GiveSuitNaemBron");
        }

        if (codename.equals(allcodes[20])) {
            SendActionBroadcast("GiveSuitCS1");
        }

        if (codename.equals(allcodes[21])) {
            SendActionBroadcast("GiveSuitCS2");
        }

        if (codename.equals(allcodes[22])) {
            SendActionBroadcast("GiveSuitCS3");
        }

        if (codename.equals(allcodes[23])) {
            SendActionBroadcast("GiveSuitHalat");
        }

        if (codename.equals(allcodes[24])) {
            SendActionBroadcast("GiveSuitEcolog");
        }

        if (codename.equals(allcodes[25])) {
            SendActionBroadcast("GiveSuitEcolog2");
        }

        if (codename.equals(allcodes[26])) {
            SendActionBroadcast("GiveSuitMonster");
        }
        if (codename.equals(allcodes[27])) {
            SendActionBroadcast("GiveSuitIgrotex");
        }

        if (codename.equals(allcodes[28])) {
            SendActionBroadcast("GiveSuitBolot");
        }

        if (codename.equals(allcodes[29])) {
            SendActionBroadcast("GiveBat");

        }
        if (codename.equals(allcodes[30])) {
            SendActionBroadcast("GiveKristall");

        }
        if (codename.equals(allcodes[31])) {
            SendActionBroadcast("GivePust");

        }

        if (codename.equals(allcodes[32])) {
            SendActionBroadcast("GiveRybka");

        }

        if (codename.equals(allcodes[33])) {
            SendActionBroadcast("GiveFlower");

        }

        if (codename.equals(allcodes[34])) {
            SendActionBroadcast("GivePsiHelm");

        }

        if (codename.equals(allcodes[35])) {
            SendActionBroadcast("GiveAntirad");

        }
        if (codename.equals(allcodes[36])) {
            SendActionBroadcast("GiveMedikit");

        }

        if (codename.equals(allcodes[37])) {
            SendActionBroadcast("GiveMilMedikit");

        }

        if (codename.equals(allcodes[38])) {
            SendActionBroadcast("GiveSciMedikit");

        }

        if (codename.equals(allcodes[39])) {
            SendActionBroadcast("GiveRespirator");

        }

        if (codename.equals(allcodes[40])) {
            SendActionBroadcast("GiveProtivogas");

        }

        if (codename.equals(allcodes[41])) {
            SendActionBroadcast("GiveSZD");

        }

        if (codename.equals(allcodes[42])) {
            SendActionBroadcast("GiveTropa");

        }

        if (codename.equals(allcodes[43])) {
            SendActionBroadcast("GiveEOTropa");

        }

        if (codename.equals(allcodes[44])) {
            SendActionBroadcast("GiveNark");

        }

        if (codename.equals(allcodes[45])) {
            SendActionBroadcast("GiveDoza");

        }

        if (codename.equals(allcodes[46])) {
            SendActionBroadcast("GiveNarkHeal");

        }


        if (codename.equals(allcodes[47])) {
            SendActionBroadcast("GiveBolezn");

        }

        if (codename.equals(allcodes[48])) {
            SendActionBroadcast("GiveImmun");

        }
        if (codename.equals(allcodes[49])) {
            SendActionBroadcast("GiveHeal");

        }
        if (codename.equals(allcodes[50])) {
            SendActionBroadcast("GiveOtrava");

        }

        if (codename.equals(allcodes[51])) {
            SendActionBroadcast("GiveOtvar");

        }

        if (codename.equals(allcodes[52])) {
            SendActionBroadcast("IncreaseExp10");

        }
        if (codename.equals(allcodes[53])) {
            SendActionBroadcast("IncreaseExp25");

        }
        if (codename.equals(allcodes[54])) {
            SendActionBroadcast("IncreaseExp50");

        }
        if (codename.equals(allcodes[55])) {
            SendActionBroadcast("SuiteRepair");

        }

        if (codename.equals(allcodes[56])) {
            SendActionBroadcast("RespawnQR");
        }

        if (codename.equals(allcodes[57])) {
            SendActionBroadcast("IncreaseRadResist");
        }

        if (codename.equals(allcodes[58])) {
            SendActionBroadcast("DecreaseRadResist");
        }

        if (codename.equals(allcodes[59])) {
            SendActionBroadcast("IncreasePoisonResist");
        }

        if (codename.equals(allcodes[60])) {
            SendActionBroadcast("DecreasePoisonResist");
        }

        if (codename.equals(allcodes[61])) {
            SendActionBroadcast("IncreasePsiResist");
        }

        if (codename.equals(allcodes[62])) {
            SendActionBroadcast("DecreasePsiResist");
        }

        if (codename.equals(allcodes[63])) {
            SendActionBroadcast("IncreaseGravResist");
        }

        if (codename.equals(allcodes[64])) {
            SendActionBroadcast("DecreaseGravResist");
        }

        if (codename.equals(allcodes[65])) {
            SendActionBroadcast("IncreaseFireResist");
        }

        if (codename.equals(allcodes[66])) {
            SendActionBroadcast("DecreaseFireResist");
        }

        if (codename.equals(allcodes[67])) {
            SendActionBroadcast("IncreaseElectroResist");
        }

        if (codename.equals(allcodes[68])) {
            SendActionBroadcast("DecreaseElectroResist");
        }

        if (codename.equals(allcodes[69])) {
            SendActionBroadcast("GiveRepair");

        }

        if (codename.equals(allcodes[70])) {
            SendActionBroadcast("GiveBolezn");

        }
        if (codename.equals(allcodes[71])) {
            SendActionBroadcast("GiveBolt");

        }

        if (codename.equals(allcodes[72])) {
            SendActionBroadcast("GiveBar");

        }
        if (codename.equals(allcodes[73])) {
            SendActionBroadcast("GiveAdept");

        }

        if (codename.equals(allcodes[74])) {
            SendActionBroadcast("RemoveAdept");

        }

        if (codename.equals(allcodes[75])) {
            SendActionBroadcast("GiveHunter");

        }

        if (codename.equals(allcodes[76])) {
            SendActionBroadcast("DrinkVodka");
        }

        if (codename.equals(allcodes[77])) {
            SendActionBroadcast("RemoveGipnos");

        }

        if (codename.equals(allcodes[78])) {
            SendActionBroadcast("Osob");

        }








//
//        if (codename.equals(allcodes[27])) {
//            SendActionBroadcast("RespawnQR");
//            String addict = Initializator.GetCurrentDF() + ".  " + "Оживление QR-кодом";
//            NotifyLog(addict);
//        }
//
//        if (codename.equals(allcodes[28])) {
//            SendActionBroadcast("GiveSuitMerc");
//        }
//        if (codename.equals(allcodes[29])) {
//            SendActionBroadcast("GiveSuitBerill");
//        }

        barcode.mess = " ";
        pda.qr_result = 0;

    }

    //endregion

    public class SwipeListener extends GestureDetector.SimpleOnGestureListener {
        int SWIPE_MAX_DISTANCE=300;
        int SWIPE_MIN_DISTANCE=100;
        int SWIPE_MIN_VELOCITY=100;
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_DISTANCE)
                return false;
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                RL3.setVisibility(View.GONE);

                RL5.setVisibility(View.VISIBLE);

            }
            return false;
        }
    }

    private void InvMenu(View v)
    {
        Log.d("Болт", "Нажат предмет инвентаря "+inv_index);
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.inventory_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuaction:
                        if (inv_index==1) { SendActionBroadcast("UseBolt"); inv_index=0;}
                        if (inv_index==2) {SendActionBroadcast("UseMedkit"); inv_index=0;}
                        if (inv_index==3) {SendActionBroadcast("UseMilMedkit"); inv_index=0;}
                        if (inv_index==4) {SendActionBroadcast("UseRepairs"); inv_index=0;}
                        if (inv_index==5) {SendActionBroadcast("UseSciMedkit"); inv_index=0;}
                        if (inv_index==6) {SendActionBroadcast("UseAntirad"); inv_index=0;}
                        return true;
                    case R.id.menuinfo:

                        if (inv_index==1){Toast.makeText(getApplicationContext(),
                                "Б.О.Л.Т. (Болванка Особая Лантан-Титановая) деактивирует воздействие аномалии на 15 секунд",
                                Toast.LENGTH_LONG).show();inv_index=0;}
                        if (inv_index==2){Toast.makeText(getApplicationContext(),
                                "Обычная аптечка. Восстанавливает 25 единиц здоровья",
                                Toast.LENGTH_LONG).show();inv_index=0;}
                        if (inv_index==3){Toast.makeText(getApplicationContext(),
                                "Военная аптечка. Восстанавливает 25 единиц здоровья, выводит 1000 ед. радиации",
                                Toast.LENGTH_LONG).show();inv_index=0;}
                        if (inv_index==4){Toast.makeText(getApplicationContext(),
                                "Ремкомплект. Полностью чинит текущий костюм",
                                Toast.LENGTH_LONG).show();inv_index=0;}
                        if (inv_index==5){Toast.makeText(getApplicationContext(),
                                "Научная аптечка. Восстанавливает 50 единиц здоровья, выводит 1500 единиц радиации",
                                Toast.LENGTH_LONG).show();inv_index=0;}
                        if (inv_index==6){Toast.makeText(getApplicationContext(),
                                "Комплекс антирадиационных препаратов. Выводит 1000 единиц радиации",
                                Toast.LENGTH_LONG).show();inv_index=0;}


                        return true;

                    default:
                        return false;
                }



            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // Toast.makeText(getApplicationContext(), "Все ушло",
                //  Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }


}