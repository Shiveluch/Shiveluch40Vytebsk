package com.example.user.pdashiveluch.classes;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.example.user.pdashiveluch.ShiveluchService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.net.ftp.FTPClient;

public class Debug {

    private static Debug Instance;
    private File file;
    private FileOutputStream fos;
    private boolean workBlocked=false;
    private ShiveluchService service;
    private static final String FILENAME = "log.txt";
    private boolean active=true;



    public static Debug GetInstance(){
        if(Instance==null)
            Instance=new Debug();
        return Instance;
    }


    public static void Open(ShiveluchService service){
        Debug.Log("попытаемся открыть лог файл");
        GetInstance().service=service;

        Debug.Log("начинаем смотреть путь");
        String path=".";
        try {
            //path = GetInstance().service.getClass().getClassLoader().getResource(".").getPath();
            path = GetInstance().service.getApplicationInfo().dataDir;
            Debug.Log("путь "+path);
        } catch (Exception ex){
            Debug.Log("ошибка расчета пути "+ex);
        }

        GetInstance().file=new File(path, FILENAME);
        Debug.Log("файл");
        try {
            GetInstance().file.createNewFile();
        } catch (IOException exc){
            Debug.Log("Ошибка 1 создания log файла"+exc);
            service.NotifyLog("Ошибка 1 создания log файла");
            GetInstance().workBlocked=true;
            return;
        }
        if(!GetInstance().file.canWrite())
            GetInstance().file.setWritable(true,true);

        try {
            GetInstance().fos = service.openFileOutput(FILENAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException exc){
            Debug.Log("Ошибка 2 создания log файла"+exc);
            service.NotifyLog("Ошибка 2 создания log файла");
            GetInstance().workBlocked=true;
            return;
        }

    }

    //тест
    public static void Log(String value){
        Log.d("жопонька",value);
        if(!GetInstance().active)
            return;
        if(GetInstance().workBlocked)
            return;
        if(GetInstance().fos==null)
            return;
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("MM dd yyyy HH-mm-ss-SSS", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        try {
            GetInstance().fos.write((timeText+" "+value+"\n").getBytes());
            GetInstance().fos.flush();
        } catch (IOException ex) {
            Log.d("жопонька","Ошибка записи строки в log файл"+ex);
            //GetInstance().service.NotifyLog("Ошибка записи строки в log файл");
        }

    }

    public static void Close(){
        try{
            GetInstance().fos.close();
        } catch (IOException ex){
            Debug.Log("Ошибка закрытия log файла"+ex);
            //GetInstance().service.NotifyLog("Ошибка закрытия log файла");
        }

    }

    public static void Clear(){
        Debug.Log("Начинаем очистку лога");
        String path=".";
        try {
            //path = GetInstance().service.getClass().getClassLoader().getResource(".").getPath();
            path = GetInstance().service.getApplicationInfo().dataDir;
            Debug.Log("путь "+path);
        } catch (Exception ex){
            Debug.Log("ошибка расчета пути "+ex);
        }

        GetInstance().file=new File(path, FILENAME);
        Debug.Log("файл");

        if(!GetInstance().file.canWrite())
            GetInstance().file.setWritable(true,true);
        try {
            if(GetInstance().file.delete())
                try {
                    GetInstance().file.createNewFile();
                } catch (IOException exc){
                    Debug.Log("Ошибка 3 при попытке создания log файла"+exc);
                    GetInstance().service.NotifyLog("Ошибка 3 при попытке создания log файла"+exc);
                }
        } catch (SecurityException exc){
            Debug.Log("Ошибка при попытке удаления log файла"+exc);
            GetInstance().service.NotifyLog("Ошибка при попытке удаления log файла"+exc);
            GetInstance().workBlocked=true;
            return;
        }
        Debug.Log("Завершили очистку лога без необработанных исключений");
    }
    public static void sendLog(){
        Thread thread = new Thread(null, GetInstance().doBackgroundThreadProcessing,
                "Background");
        thread.start();
    }

    private void setActive(boolean value){
        active=value;
    }

    public static void Activate(){
        GetInstance().setActive(true);
        Debug.Log("лог активирован");
    }

    public static  void Deactivate(){
        GetInstance().setActive(false);
        Debug.Log("Лог деактивирован");
    }

    private static void sendLogInner(){
        Debug.Log("Пытаемся отправить лог");
        FTPClient fClient = new FTPClient();
        String FILENAME = "log.txt";
        String path = GetInstance().service.getApplicationInfo().dataDir;
        FileInputStream fInput;
        Debug.Log("Определились с путем к лог файлу");
        try {
            fInput = GetInstance().service.openFileInput(FILENAME);
        } catch (IOException ex){
            Debug.Log("Ошибка поиска log файла для отправки"+ex);
            GetInstance().service.NotifyLog("Ошибка поиска log файла для отправки");
            return;
        }
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("MM dd yyyy HH-mm-ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        String fs = "/"+Settings.Secure.getString(GetInstance().service.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)+" from "+timeText+".txt";
        Debug.Log("Определились с именем отправляемого файла");
        try {
            Debug.Log("connect");
            fClient.connect("DrMort.ru");
            Debug.Log("passiv mode");
            fClient.enterLocalPassiveMode();
            Debug.Log("login");
            fClient.login("Shiveluch", "896432");
            Debug.Log("upload");
            fClient.storeFile(fs, fInput);
            Debug.Log("logout");
            fClient.logout();
            Debug.Log("disconnect");
            fClient.disconnect();
        } catch (Exception ex) {
            Debug.Log("отправка log файла завершена с ошибкой "+ex);
            GetInstance().service.NotifyLog("отправка log файла завершена с ошибкой "+ex);
        }
        Debug.Log("Завершили отправку без необработанных исключений");
    }

    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            sendLogInner();
        }
    };
}
