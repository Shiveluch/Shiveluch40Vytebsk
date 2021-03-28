package com.example.user.pdashiveluch.classes;

import com.example.user.pdashiveluch.ShiveluchService;

public class MessageEvent {
    public String action;
    public DataPack charValue;

    public MessageEvent(String value, DataPack charValue) {
        this.action = value;
        this.charValue=charValue;
    }
}



