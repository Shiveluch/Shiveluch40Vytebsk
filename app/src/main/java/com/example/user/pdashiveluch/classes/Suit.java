package com.example.user.pdashiveluch.classes;


import android.util.Log;

import com.example.user.pdashiveluch.ShiveluchService;

public class Suit {
    String Name;
    int id;
    String Description;
    float suit_stam_big = 950;//реальная прочность костюма в дальнейшем перенести в характеристики костюма

    int basic_resist_fire, basic_resist_grav, basic_resist_poison, basic_resist_rad,
            basic_resist_psi, basic_resist_electro;

    int suitImage;
    ShiveluchService service;

    //int[] suitesnew = {R.drawable.stalknew, R.drawable.dutynew, R.drawable.bandnew,
    //        R.drawable.freedomnew, R.drawable.scinew, R.drawable.monolitnew, R.drawable.sevanew,
    //        R.drawable.zarianew, R.drawable.ecolognew, R.drawable.bulatnew, R.drawable.f_exa,
    //        R.drawable.d_exa, R.drawable.m_exa, R.drawable.naemn, R.drawable.berill};

    public String getName() {
        return Name;
    }

    public  String getDescription(){
        return Description;
    }

    public int GetImageNum(){
        return suitImage;
    }



    public Suit(ShiveluchService service, int id, String Name, String Description,int stamina, int basic_resist_fire, int basic_resist_grav, int basic_resist_poison, int basic_resist_psi, int basic_resist_rad, int basic_resist_electro){
        Debug.Log("suit constructor");
        this.id=id;
        this.Name=Name;
        this.Description=Description;
        this.suit_stam_big=stamina;
        this.basic_resist_fire=basic_resist_fire;
        this.basic_resist_grav=basic_resist_grav;
        this.basic_resist_poison=basic_resist_poison;
        this.basic_resist_rad=basic_resist_rad;
        this.basic_resist_psi=basic_resist_psi;
        this.basic_resist_electro=basic_resist_electro;
       // this.suitImage=suitImage;
        this.service=service;
    }



    public float getSuit_stam_big() {
        return suit_stam_big;
    }

    public void ResetStamina(){
        suit_stam_big=1000;
        service.NotifyActivity("PARAMETERS");
    }

    public void Repair(float value){
        suit_stam_big=Math.min(1000, value);
        service.NotifyActivity("PARAMETERS");
    }

    public int getBasic_resist_electro()
    {
      return basic_resist_electro;

    }


    public int getBasic_resist_fire() {
        return basic_resist_fire;
    }

    public int getBasic_resist_grav() {
        return basic_resist_grav;
    }

    public int getBasic_resist_poison() {
        return basic_resist_poison;
    }

    public int getBasic_resist_rad() {
        return basic_resist_rad;
    }

    public int getBasic_resist_psi() {
        return basic_resist_psi;
    }

    public int getId(){
        return id;
    }

    public void DecreaseStamina(float value){
        float newStamina=suit_stam_big-value;
        suit_stam_big=Math.max(0, Math.min(1000, newStamina));
        service.NotifyActivity("PARAMETERS");

    }
}
