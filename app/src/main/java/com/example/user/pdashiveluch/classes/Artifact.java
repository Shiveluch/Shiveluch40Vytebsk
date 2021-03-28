package com.example.user.pdashiveluch.classes;

public class Artifact {
    String _name;
    String _description;
    int image;

    int _resist_fire;
    int _resist_grav;
    int _resist_poison;
    int _resist_rad;
    int _resist_psi;
    int _resist_electro;

    int _addition_rad; //добавляет радиационный фон
    int _addition_heal; //лечит
    //int _addition_chemical_resist; //химза в процентах
    //int _addition_fire_electro_resist; //

    //region getset
    public int getImage() {
        return image;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_resist_fire() {
        return _resist_fire;
    }

    public int get_resist_electro() {return _resist_electro;}

    public void set_resist_fire(int _resist_fire) {
        this._resist_fire = _resist_fire;
    }

    public int get_resist_grav() {
        return _resist_grav;
    }

    public void set_resist_grav(int _resist_grav) {
        this._resist_grav = _resist_grav;
    }

    public int get_resist_poison() {
        return _resist_poison;
    }

    public void set_resist_poison(int _resist_poison) {
        this._resist_poison = _resist_poison;
    }

    public int get_resist_rad() {
        return _resist_rad;
    }

    public void set_resist_rad(int _resist_rad) {
        this._resist_rad = _resist_rad;
    }

    public int get_resist_psi() {
        return _resist_psi;
    }

    public void set_resist_psi(int _resist_psi) {
        this._resist_psi = _resist_psi;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public int get_addition_heal() {
        return _addition_heal;
    }

    public void set_addition_heal(int _addition_heal) {
        this._addition_heal = _addition_heal;
    }

    public int get_addition_rad() {
        return _addition_rad;
    }

    public void set_addition_rad(int _addition_rad) {
        this._addition_rad = _addition_rad;
    }

//endregion

    public Artifact(String in_name, String description, int image,int in_resist_fire,int in_resist_grav, int in_resist_rad,int in_resist_poison, int in_resist_psi, int in_resist_electro, int in_addition_heal, int in_addition_rad){
        this.image=image;
        this._description=description;
        _name=in_name;
        _resist_fire=in_resist_fire;
        _resist_grav=in_resist_grav;
        _resist_poison=in_resist_grav;
        _resist_psi=in_resist_psi;
        _resist_rad=in_resist_rad;
        _resist_poison=in_resist_poison;
        _resist_electro=in_resist_electro;
        _addition_heal=in_addition_heal;
        _addition_rad=in_addition_rad;
    }


}
