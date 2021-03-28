package com.example.user.pdashiveluch.classes;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PDA_Event {
    protected String startDateTime;

    protected boolean completed=false;

    public boolean isCompleted(){
        return completed;
    }

    public void Complete(boolean value){
        completed=value;
    }
    public void Complete(){
        completed=true;
    }
    public String getStartDateTimeStr(){
        return startDateTime;
    }

    public Date getStartDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date storedTime=new Date();
        try {
            storedTime = dateFormat.parse(startDateTime);
        } catch (Exception e){
            Debug.Log("Исключение при парсинге тайм стампа в эвенте"+e.getMessage());
        }
        return storedTime;
    }
}
