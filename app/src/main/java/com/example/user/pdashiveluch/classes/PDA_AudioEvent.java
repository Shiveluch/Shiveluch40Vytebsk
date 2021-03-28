package com.example.user.pdashiveluch.classes;

import android.util.Log;

import com.example.user.pdashiveluch.Initializator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PDA_AudioEvent extends PDA_Event {
    protected String endDateTime;
    private PDA_AudioManager.AppSounds Snd;

    public PDA_AudioEvent(String start, String end, PDA_AudioManager.AppSounds Sound){
        startDateTime=start;
        endDateTime=end;
        this.Snd=Sound;



    }



    public String getEndDateTimeStr(){
        return endDateTime;
    }

    public Date getEndDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date storedTime=new Date();
        try {
            storedTime = dateFormat.parse(endDateTime);
        } catch (Exception e){
            Debug.Log("Исключение при парсинге тайм стампа в эвенте выброса"+e.getMessage());
        }
        return storedTime;
    }

    public Date getAlarmTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date storedTime=new Date();
        try {
            storedTime = dateFormat.parse(startDateTime);
        } catch (Exception e){
            Debug.Log("Исключение при парсинге тайм стампа в эвенте выброса"+e.getMessage());
        }
        storedTime.setTime(storedTime.getTime()- Initializator.GetBlowoutAlarmTime());
        return storedTime;
    }

    public PDA_AudioManager.AppSounds getSound(){
        return Snd;
    }


}
