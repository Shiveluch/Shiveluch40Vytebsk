package com.example.user.pdashiveluch.classes;

import android.util.Log;

import com.example.user.pdashiveluch.Initializator;
import com.example.user.pdashiveluch.classes.Places;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PDA_BlowoutEvent extends PDA_Event{
    protected String endDateTime;
    protected Places[] Vaults;
    public PDA_BlowoutEvent(String start, String end, Places[] vaults){
        startDateTime=start;
        endDateTime=end;
        this.Vaults=vaults;

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

    public boolean isVault(Places value){
        for (Places vault:
             Vaults) {
            if(value==vault)
                return true;
        }
        return false;
    }

}
