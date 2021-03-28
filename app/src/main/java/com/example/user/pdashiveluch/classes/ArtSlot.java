package com.example.user.pdashiveluch.classes;

import com.example.user.pdashiveluch.ShiveluchService;
import com.example.user.pdashiveluch.pda;

public class ArtSlot {
    private Artifact _artifact;
    private boolean _activity;
    private ShiveluchService service;

    public ArtSlot(ShiveluchService service){
        _activity=false;
        this.service=service;
    }

    //public void SetActivity(pda pda_activity){
    //    this.pda_activity=pda_activity;
    //}

    //region setget

    public Artifact get_artifact() {
        return _artifact;
    }

    public void set_artifact(Artifact _artifact) {
        this._artifact = _artifact;
    }

    public boolean Active() {
        return _activity;
    }

    public void set_active(boolean _activity) {
        this._activity = _activity;
        service.NotifyActivity("ART");
    }

    public boolean Gived(){
        if(_artifact!=null)
            return true;
        else
            return false;
    }

    public String GetDescription(){
        if(Gived())
            return _artifact._description;
        else
            return "";
    }

    public int GetFireResist(){
        if(Gived()&&_activity) {
            return _artifact.get_resist_fire();
        }
        else
            return 0;

    }

    public int GetElectroResist()
    {
        if (Gived()&&_activity)
        {
            return _artifact.get_resist_electro();

        }
        else
            return 0;

    }

    public int GetGravResist(){
        if(Gived()&&_activity) {
            return _artifact.get_resist_grav();
        }
        else
            return 0;
    }

    public int GetPoisonResist(){
        if(Gived()&&_activity) {
            return _artifact.get_resist_poison();
        }
        else
            return 0;
    }

    public int GetRadResist(){
        if(Gived()&&_activity) {
            return _artifact.get_resist_rad();
        }
        else
            return 0;
    }

    public int GetPsiResist(){
        if(Gived()&&_activity) {
            return _artifact.get_resist_psi();
        }
        else
            return 0;
    }

    public int GetAdditionRad(){
        if(Gived()&&_activity)
            return _artifact.get_addition_rad();
        else
            return 0;
    }

    public int GetAdditionHeal(){
        if(Gived()&&_activity)
            return _artifact.get_addition_heal();
        else
            return 0;
    }

    public void Toggle(){
        _activity=!_activity;
    }

    public String GetStringStatus(){
        String text="";
        if(Gived()){
            text=text+get_artifact().get_name();
            if(!Active())
                text=text+" (неактивно)";

        } else
            text=text+"Пусто";
        return text;
    }
    //endregion
}
