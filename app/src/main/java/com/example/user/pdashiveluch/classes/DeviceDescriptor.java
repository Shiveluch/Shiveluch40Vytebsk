package com.example.user.pdashiveluch.classes;

import java.util.HashMap;
import java.util.Map;

public class DeviceDescriptor {
    private String mac="00:00:00:00:00:00";
    private int primaryInt=0;
    private int secondaryInt=0;
    private static Map<String,DeviceDescriptor> dict=new HashMap<String, DeviceDescriptor>();

    public DeviceDescriptor(String mac, int primary, int secondary){
        this.mac=mac;
        this.primaryInt=primary;
        this.secondaryInt=secondary;
        dict.put(mac,this);
    }

    public int getPrimaryInt(){
        return primaryInt;
    }

    public int getSecondaryInt(){
        return secondaryInt;
    }

    public String getMac(){
        return mac;
    }

    public static DeviceDescriptor getDeviceDescriptor(String mac){
        return dict.get(mac);
    }

    public static boolean Check(String mac){
        return dict.containsKey(mac);
    }
}
