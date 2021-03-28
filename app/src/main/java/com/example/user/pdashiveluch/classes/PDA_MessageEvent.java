package com.example.user.pdashiveluch.classes;

import java.util.Date;

public class PDA_MessageEvent extends PDA_Event {
    protected String message;

    public PDA_MessageEvent(String start, String message){
        startDateTime=start;
        this.message=message;
    }

    public String getMessage(){
        return message;
    }
}
