package com.example.user.pdashiveluch.classes;


import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.example.user.pdashiveluch.Group;
import com.example.user.pdashiveluch.Initializator;
import com.example.user.pdashiveluch.R;
import com.example.user.pdashiveluch.ShiveluchService;

import java.util.Random;

public final class PlayerCharcteristics {

    // region *****СТАТЫ ИГРОКА
    private int _rankmod; // 1. модификатор защиты от аномалий в зависимости от ранга. 1,7 ,12,20
    private int rad_inv_resist, temp_rad_resist;
    private int poison_inv_resist, temp_poison_resist;
    private int fire_inv_resist, temp_fire_resist;
    private int add_fire_resist,add_rad_resist, add_poison_resist, add_psi_resist, add_grav_resist, add_electro_resist;
    private boolean _group; //2. принадлежность к группировке,
    private boolean _nark; // наркозависимость
    private int nark_count;//таймер наступленяи вредных эффектов при наркомании
    private int bolezn_count;//таймер наступленяи вредных эффектов при наркомании
    private Group group; //ид группы
    private Suit suit; //идентификатор костюма. не может быть пустым. хотя бы куртка простая
    public boolean v_otklik;
    //private int z_count = 100;//1. устойчивость атаке контролера
    private boolean zombi;  //2. Состояние зомбированности
    private boolean psi_helm; //флаг наличия/отсутствия пси-шлема
    public boolean respirator; //флаг наличия/отсутствия респиратора
    public boolean protivogas; //флаг наличия/отсутствия противогаза
    public boolean SZD; //флаг наличия/отсутствия СЗД
    public boolean bolezn; //флаг наличия/отсутствия болезни
    public boolean otrava; //флаг наличия/отсутствия отравления
    public boolean tropa; //флаг наличия/отсутствия Тропы ЧН
    public boolean adept;//флаг адептства
    public boolean osob;
    public boolean hunter;
    public boolean immun;
    public boolean skin;
    public int bolt;
    private float heal_big = 95000;//реальный уровень здоровья
    private int loctime;
    private int distance;
    private boolean stalker_start;

    private float PsiHealth; //писхическое здоровье
    private float rad = 150; //уровень радиации (реальный и для отображения на шкале от 1 до 5000

    private int medikits, mil_medikits, sci_medikits, antirads; //аптечки и антирадин
    private int exp = 1, expcount = 0; //1. Опыт 2. Счетчик набора опыта при прохождении определенного времени
    private int orient=0; //флаг ориентировки
    private int power;
    private boolean dead;//флаг смерти
    private boolean monvoice; //зов монолита
    private Random random = new Random(); //случайное число для оценки ремонта костюма с помощью QR-кода
    private int contcount=0, containers=0;
    private int ZombiCount=0;

    private String name; //позывной игрока
    private ArtSlot Kolobok, Plenka, Puzir, Heart, Battery;
    private int repairs;
    double latitude,longitude;



    //endregion

    //private pda activity; //обратная связь
    private ShiveluchService service;
    private boolean gipnos;

    public PlayerCharcteristics(ShiveluchService value){
        service=value;
        _rankmod=1;
        _group=false;
        _nark=false;
        immun=false;
        nark_count = 0;
        group=null;
        contcount=0;
        containers=0;
        ZombiCount=0;
        add_fire_resist=0;
        add_electro_resist=0;
        add_grav_resist=0;
        add_poison_resist=0;
        add_psi_resist=0;
        add_rad_resist=0;
        skin=true;



        //z_count = 100;
        zombi=false;
        psi_helm=false;
        immun=false;
        heal_big = 95000;
        PsiHealth=100;
        monvoice = false;

        rad = 1500;
        power=0;
        loctime=3;


        medikits=0;
        mil_medikits=0;
        sci_medikits=0;
        antirads=0;


        exp = 1;
        expcount = 0;
        dead=true;

        name="";

        Kolobok=new ArtSlot(service);
        Plenka=new ArtSlot(service);
        Puzir=new ArtSlot(service);
        Heart=new ArtSlot(service);
        Battery=new ArtSlot(service);

        GiveSuitKurt();

        setGroup(Initializator.Groups().get(1));

    }

    //region артефакты

    public void GiveKolobok(){
        Artifact art=new Artifact("Батарейка", "",
                R.drawable.shar,-5,0,0,
                -5,0,20,10,1);
        art.set_description("Огонь -5. 0.1 рад/сек. +0.1 зд./сек Эл-во +20\n");
        Kolobok.set_artifact(art);
        service.NotifyToast("Получен артефакт: "+art.get_name());
        service.NotifyActivity("ART");
    }

    public void GivePlenka(){
        Artifact art=new Artifact("Кристалл", "",
                R.drawable.rybka,20,0,0,5,
                0,5,0,1);
        art.set_description("Огонь +20. Химия +5 Эл-во +5 0.1 рад/сек\n");
        Plenka.set_artifact(art);
        service.NotifyToast("Получен артефакт: "+art.get_name());
        service.NotifyActivity("ART");
    }

    public void GivePuzir(){
        Artifact art=new Artifact("Пустышка", "",
                R.drawable.dusha,0,5,0,15,
                0,0,0,-2);
        art.set_description("Грав. +5. Химия +15. -0.2 рад/сек  \n");
        Puzir.set_artifact(art);
        service.NotifyToast("Получен артефакт: "+art.get_name());
        service.NotifyActivity("ART");

    }

    public void GiveHeart(){
        Artifact art=new Artifact("Золотая рыбка", "",
                R.drawable.meduza,0,0,10,5,
                0,5,20,4);
        art.set_description("Рад. +10. Химия +5 Эл-во +5 Здоровье +0.2/сек. 0.4 рад/сек  \n");
        Heart.set_artifact(art);
        service.NotifyToast("Получен артефакт: "+art.get_name());
        service.NotifyActivity("ART");
    }

    public void GiveBattery(){
        Artifact art=new Artifact("Каменный цветок", "",R.drawable.batareika,
                0,0,10,0,10,
                0,20,0);
        art.set_description("Рад. +10. Пси +10.  0.2 зд/сек\n");
        Battery.set_artifact(art);
        service.NotifyToast("Получен артефакт: "+art.get_name());
        service.NotifyActivity("ART");

    }

    public ArtSlot getKolobok() {
        return Kolobok;
    }

    public void setKolobok(ArtSlot kolobok) {
        Kolobok = kolobok;
    }

    public ArtSlot getPlenka() {
        return Plenka;
    }

    public void setPlenka(ArtSlot plenka) {
        Plenka = plenka;
    }

    public ArtSlot getPuzir() {
        return Puzir;
    }

    public void setPuzir(ArtSlot puzir) {
        Puzir = puzir;
    }

    public ArtSlot getHeart() {
        return Heart;
    }

    public void setHeart(ArtSlot heart) {
        Heart = heart;
    }

    public ArtSlot getBattery() {
        return Battery;
    }

    public void setBattery(ArtSlot battery) {
        Battery = battery;
    }
    //endregion

    //region резисты

    public int GetFireResist(){
        int res=0;
        res+=Kolobok.GetFireResist();
        res+=Plenka.GetFireResist();
        res+=Puzir.GetFireResist();
        res+=Heart.GetFireResist();
        res+=Battery.GetFireResist();
        res+=getAdd_fire_resist();
        res+=_rankmod;
        if(suit!=null)
            res+=suit.getBasic_resist_fire();

        if (res<=0) res=1;
        return res;

    }

    public int GetElectroResist(){
        int res=0;
        res+=Kolobok.GetElectroResist();
        res+=Plenka.GetElectroResist();
        res+=Puzir.GetElectroResist();
        res+=Heart.GetElectroResist();
        res+=Battery.GetElectroResist();
        res+=_rankmod;
        res+=getAdd_electro_resist();
        if(suit!=null)
            res+=suit.getBasic_resist_electro();
        if (res<=0) res=1;
        return res;

    }

    public int GetGravResist(){
        int res=0;
        res+=Kolobok.GetGravResist();
        res+=Plenka.GetGravResist();
        res+=Puzir.GetGravResist();
        res+=Heart.GetGravResist();
        res+=Battery.GetGravResist();
        res+=_rankmod;
        res+=getAdd_grav_resist();
        if(suit!=null)
            res+=suit.getBasic_resist_grav();
        if (res<=0) res=1;
        return res;
    }

    public int GetPoisonResist(){
        int res=0;
        res+=Kolobok.GetPoisonResist();
        res+=Plenka.GetPoisonResist();
        res+=Puzir.GetPoisonResist();
        res+=Heart.GetPoisonResist();
        res+=Battery.GetPoisonResist();
        res+=_rankmod;
        res+=getPoison_inv_resist();
        res+=getAdd_poison_resist();
        if(suit!=null)
            res+=suit.getBasic_resist_poison();
        if (res<=0) res=1;
        return res;
    }

    public int GetRadResist(){
        int res=0;
        res+=Kolobok.GetRadResist();
        res+=Plenka.GetRadResist();
        res+=Puzir.GetRadResist();
        res+=Heart.GetRadResist();
        res+=Battery.GetRadResist();
        res+=_rankmod;
        res+=getRad_inv_resist();
        res+=getAdd_rad_resist();
        if(suit!=null)
            res+=suit.getBasic_resist_rad();
        if (res<=0) res=1;
        return res;
    }

    public int GetPsiResist(){
        int res=0;
        res+=Kolobok.GetPsiResist();
        res+=Plenka.GetPsiResist();
        res+=Puzir.GetPsiResist();
        res+=Heart.GetPsiResist();
        res+=Battery.GetPsiResist();
        res+=_rankmod;
        res+=getAdd_psi_resist();
        if(suit!=null)
            res+=suit.getBasic_resist_psi();
        if (res<=0) res=1;
        return res;
    }

    //endregion

    public void setRadInvResist(int rad_inv_resist)
    {
        this.rad_inv_resist=rad_inv_resist;
    }

    public void setAdd_fire_resist(int add_fire_resist)
    {
        this.add_fire_resist=add_fire_resist;
    }

    public void setAdd_rad_resist(int add_rad_resist)
    {
        this.add_rad_resist=add_rad_resist;
    }

    public void setAdd_electro_resist(int add_electro_resist)
    {
        this.add_electro_resist=add_electro_resist;
    }

    public void setAdd_grav_resist(int add_grav_resist)
    {
        this.add_grav_resist=add_grav_resist;
    }

    public void setAdd_psi_resist(int add_psi_resist)
    {
        this.add_psi_resist=add_psi_resist;
    }

    public void setAdd_poison_resist(int add_poison_resist)
    {
        this.add_poison_resist=add_poison_resist;
    }

    public int getRad_inv_resist() {
        return rad_inv_resist;
    }

    public int getAdd_fire_resist()
    {
        return add_fire_resist;

    }

    public int getAdd_rad_resist()
    {
        return add_rad_resist;

    }

    public int getAdd_psi_resist()
    {
        return add_psi_resist;

    }

    public int getAdd_poison_resist()
    {
        return add_poison_resist;

    }

    public int getAdd_grav_resist()
    {
        return add_grav_resist;

    }

    public int getAdd_electro_resist()
    {
        return add_electro_resist;

    }

    public void setPoisonInvResist(int poison_inv_resist)
    {
        this.poison_inv_resist=poison_inv_resist;
    }

    public int getPoison_inv_resist() {
        return poison_inv_resist;
    }

    //region setget
    public int get_rankmod() {
        return _rankmod;
    }

    public void set_rankmod(int _rankmod) {
        this._rankmod = _rankmod;
    }

    public boolean is_group() {
        return _group;
    }

    public void set_group(boolean _group) {
        this._group = _group;
    }

    public boolean is_nark() {
        return _nark;
    }

    public  boolean is_v_otklik()
    {
        return v_otklik;

    }

    public void set_vibro (boolean v_otklik)
    {if(v_otklik==this.v_otklik)
            return;

        this.v_otklik = v_otklik;
    if (v_otklik)
    {
        service.NotifyToast("Виброотклик включен");


    }
    else
    {
        service.NotifyToast("Виброоотклик отключен");


    }
    }

    public void set_nark(boolean _nark) {
        if(_nark==this._nark)
            return;

        this._nark = _nark;

        if(_nark){

            IncreaseHealth(100000);
            service.NotifyToast("Наркозависимость");
            String addict = Initializator.GetCurrentDF() + ".  " + "Наркозависимость";
            service.NotifyLog(addict);
        } else {
            service.NotifyToast("Излечение от наркозависимости");
            String addict = Initializator.GetCurrentDF() + ".  " + "Излечение от наркозависимости";
            service.NotifyLog(addict);
        }
        service.NotifyActivity("EFFECTS");
    }



    public int getNark_count() {
        return nark_count;
    }



    public void setNark_count(int nark_count) {
        this.nark_count = nark_count;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }



    public boolean isImmun() {
        return immun;
    }

    public void setImmun(boolean immun) {
        if(this.immun==immun)
            return;
        this.immun = immun;

    }


    public void setZombi(boolean zombi) {
        if(this.zombi==zombi)
            return;
        this.zombi = zombi;
        Vibrator vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
           // ZombiMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            long[] pattern = {0, 500, 1000};
            vibrator.vibrate(pattern,0);
        }
    }
    public boolean isZombi() {
        return zombi;
    }


    public void ZombiMode()
    {
        ZombiCount++;
        Log.d("Zomb ",""+ZombiCount);
        if (ZombiCount>600) {setZombi(false); setPsiHealth(100);}

    }


    public boolean isPsi_helm() {
        return psi_helm;
    }

    public boolean isRespirator() {
        return respirator;
    }

    public boolean isProtivogas() {
        return protivogas;
    }

    public boolean isSZD() {
        return SZD;
    }

    public boolean isBolezn() {
        return bolezn;
    }

    public boolean isOtrava()
    {
        return otrava;
    }


    public boolean isTropa() {
        return tropa;
    }

    public void setPsi_helm(boolean psi_helm) {
        if(this.psi_helm==psi_helm)
            return;
        this.psi_helm = psi_helm;
        service.NotifyActivity("INVENTORY");

    }

    public void setAdept(boolean adept) {
        if(this.adept==adept)
            return;
        this.adept = adept;
        service.NotifyActivity("ADEPT");

    }

    public void setOsob(boolean osob) {
        if(this.osob==osob)
            return;
        this.osob = osob;
        service.NotifyActivity("OSOB");

    }

    public void setHunter(boolean hunter) {
        if(this.hunter==hunter)
            return;
        this.hunter = hunter;
        service.NotifyActivity("INVENTORY");

    }

    public boolean isAdept() {
        return adept;
    }

    public boolean isOsob() {
        return osob;
    }




    public void setRespirator(boolean respirator) {
        if(this.respirator==respirator)
            return;
        this.respirator = respirator;
        setRadInvResist(10);
        setPoisonInvResist(10);

        service.NotifyActivity("INVENTORY");

    }

    public void setProtivogas(boolean protivogas) {
        if(this.protivogas==protivogas)
            return;
        this.protivogas = protivogas;
        setRadInvResist(20);
        setPoisonInvResist(20);
        service.NotifyActivity("INVENTORY");

    }

    public void setSZD(boolean SZD) {
        if(this.SZD==SZD)
            return;
        this.SZD = SZD;
        setRadInvResist(30);
        setPoisonInvResist(30);
        service.NotifyActivity("INVENTORY");

    }

    public void setBolezn(boolean bolezn) {
        if(this.bolezn==bolezn)
            return;
        this.bolezn = bolezn;
        service.NotifyToast("Наизвестная болезнь");
        String addict = Initializator.GetCurrentDF() + ".  " + "Неизвестная болезнь";
        service.NotifyLog(addict);
        service.NotifyActivity("EFFECTS");

    }

    public void setOtrava(boolean otrava) {
        if(this.otrava==otrava)
            return;
        this.otrava = otrava;
      float temp_healbig=getHeal_big();
      if (temp_healbig>50000) setHeal_big(50000);
        service.NotifyActivity("EFFECTS");

    }

    public void setTropa(boolean tropa) {
        if(this.tropa==tropa)
            return;
        this.tropa = tropa;
        setPoisonInvResist(100);
        service.NotifyActivity("INVENTORY");

    }


    public int getContcount() {
        return contcount;
    }


    private void setCont(int contcount)
    {
        this.contcount=contcount;
        if (contcount>20)
        {
            containers++;
            this.contcount=0;

        }
        service.NotifyActivity("PARAMETERS");
    }

    public int getAllcontainers()
    {
        return containers;
    }

    public float getHeal_big() {
        return heal_big;
    }


    public int getIntegerHealth(){
        return Math.round(heal_big);
    }


    //private void setPsiHealth (int PsiHealth)

    {}

    private void setHeal_big(float heal_big) {
        float oldHealth=this.heal_big;
        this.heal_big = heal_big;

        //ЗДОРОВЬЕ НЕ МОЖЕТ БЫТЬ БОЛЬШЕ 100
        if(this.heal_big>100000)
            this.heal_big=100000;
        if(this.heal_big<=0){
            this.heal_big=0;

        }
        //if(oldHealth==this.heal_big)
        //    return;
        if(heal_big==0&&!isDead())
            setDead(true);

        if (getHeal_big() >= 100000 && isDead()) {
            setDead(false);
            String addict = Initializator.GetCurrentDF() + ".  " + "Возращение в игру: полное излечение";
            service.NotifyLog(addict);
        }
        service.NotifyActivity("PARAMETERS");
    }

    public float getPsiHealth(){
        return PsiHealth;
    }

    public int getIntegerPsiHealth(){
        return Math.round(PsiHealth);
    }

    public void setPsiHealth(float value){
        PsiHealth=value;
        if(getPsiHealth()<0)
            PsiHealth=0;
    }

    public void setLoctime(int loctime)
    {
        this.loctime=loctime;

    }

    public void PsiDamage(float value, PsiSources source){
        if(value==0)
            return;
        this.setPsiHealth(getPsiHealth()-value);



        Debug.Log("Псиздоровье: "+getPsiHealth());
        service.NotifyActivity("PARAMETERS");
        if (PsiHealth<60) {
            service.NotifyActivity("VYBROS");
            Vibrator vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                long[] pattern = {0, 500, 1000};
                vibrator.vibrate(pattern, 0);
            }
        }
            if (PsiHealth>=60) {service.NotifyActivity("INSHELTER");

                Vibrator vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.cancel();
                }





        if(PsiHealth==0){
            setZombi(true);
            String addict = Initializator.GetCurrentDF() + ".  ";
            switch (source){
                case Controller:
                    addict=addict + "Зомбирован контролером";
                    service.NotifyActivity("ZOMBI");
                    break;
                case Anomaly:
                    setHeal_big(0);
                    service.NotifyActivity("DEAD");
                    addict=addict + "Пси аномалия, смерть";
                    break;
                case Blowout:
                    service.NotifyActivity("DEAD");
                    addict=addict + "Попал под выброс, смерть";

                    setHeal_big(0);
                    break;
                default:
                    addict=addict + "Внезапная смерть";
                    setHeal_big(0);
                    break;
            }
            service.NotifyLog(addict);
        }
    }

    public void PsiHealing(){
        setPsiHealth(100);
    }
    //public pda getActivity() {
    //    return activity;
    //}

    //public void setActivity(pda activity) {
     //   this.activity = activity;
        //getKolobok().SetActivity(activity);
        //getPuzir().SetActivity(activity);
        //getPlenka().SetActivity(activity);
        //getHeart().SetActivity(activity);
        //getBattery().SetActivity(activity);
    //}

    public void IncreaseContCount (int value)
    {
        setCont(getContcount()+value);
    }

    public void IncreaseHealth(float value){
        setHeal_big(getHeal_big()+value);
    }

    public void Damage(float value){
        setHeal_big(getHeal_big()-value);
    }



    public float getRad() {
        return rad;
    }

    public int getIntegerRad(){
        return Math.round(rad);
    }

    public void setRad(float rad) {
        this.rad = rad;
        //РАДИАЦИЯ НЕ МОЖЕТ БЫТЬ МЕНЬШЕ 150
        if (this.rad <= 1500) {
            this.rad = 1500;

        }
    }

    public boolean isMonvoice() {
        return monvoice;
    }

    public void setMonvoice(boolean monvoice) {
        this.monvoice = monvoice;
    }

    public void IncreaseRad(float value){
        if(value==0)
            return;
        setRad(getRad()+value);
        service.NotifyActivity("PARAMETERS");
    }

    public int getPower()
    {

        return power;
    }

    public int getLoctime()
    {
        return loctime;

    }


    public int getMedikits() {
        return medikits;
    }

    public int getMilMedikits() {
        return mil_medikits;
    }

    public int getSciMedikits() {
        return sci_medikits;
    }

    public int getRepairs() {
        return repairs;
    }

    public void setMedikits(int medikits) {
        this.medikits = medikits;
    }

    public void setPower(int power) {
        this.power=power;
    }




        public void setMilMedikits(int mil_medikits) {
        this.mil_medikits = mil_medikits;
    }

    public  void setRepair (int repairs){this.repairs=repairs;}

    public void setSciMedikits(int sci_medikits) {
        this.sci_medikits = sci_medikits;
    }



    public void GiveMedkit(){
        setMedikits(getMedikits()+1);
        service.NotifyActivity("MEDKIT");
        String addict = Initializator.GetCurrentDF() + ".  " + "Покупка аптечки";
        service.NotifyLog(addict);
    }



    public void GiveMilMedikit(){
        setMilMedikits(getMilMedikits()+1);
        service.NotifyActivity("MIL_MEDKIT");
        String addict = Initializator.GetCurrentDF() + ".  " + "Покупка военной аптечки";
        service.NotifyLog(addict);
    }

    public void GiveRepair(){
        setRepair(getRepairs()+1);
        service.NotifyActivity("REPAIRS");
        String addict = Initializator.GetCurrentDF() + ".  " + "Покупка ренмкомплекта";
        service.NotifyLog(addict);
    }



    public void GiveSciMedikit(){
        setSciMedikits(getSciMedikits()+1);
        service.NotifyActivity("SCI_MEDKIT");
        String addict = Initializator.GetCurrentDF() + ".  " + "Покупка научной аптечки";
        service.NotifyLog(addict);
    }


    public int getAntirads() {
        return antirads;
    }

    public void GiveAntirad(){
        setAntirads(getAntirads()+1);
        service.NotifyActivity("ANTIRAD");
        String addict = Initializator.GetCurrentDF() + ".  " + "Восстановлен антирад";
        service.NotifyLog(addict);
    }

    public void setAntirads(int antirads) {
        this.antirads = antirads;
    }

    public void setStalker_start(boolean stalker_start) { this.stalker_start = stalker_start;
    }

    public boolean getStalkerstart()
    {
        return stalker_start;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp=Math.max(0, exp);
    }



    public int getExpcount() {
        return expcount;
    }

    public void setExpcount(int expcount) {
        this.expcount = expcount;
    }
    public boolean isSkin() {
        return skin;
    }
    public void setSkin(boolean skin) {
        if (this.skin==skin)
            return;

        this.skin=skin;
        service.NotifyActivity("SKIN");
    }


    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        if(this.dead==dead)
            return;
        this.dead = dead;
        Vibrator vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
        if(dead){
            setPsi_helm(false);
            setRespirator(false);
            setProtivogas(false);
            setSZD(false);
            set_nark(false);
            set_vibro(true);
            setOtrava(false);
            setTropa(false);
            setImmun(false);
            setAntirads(0);
            setMedikits(0);
            setMilMedikits(0);
            setSciMedikits(0);
            Kolobok.set_artifact(null);
            Plenka.set_artifact(null);
            Puzir.set_artifact(null);
            Heart.set_artifact(null);
            Battery.set_artifact(null);
            if(isZombi())
                service.myAudioManager.PlaySound(PDA_AudioManager.AppSounds.ZOMBIE);
            else
                service.myAudioManager.PlaySound(PDA_AudioManager.AppSounds.DEATH);
            int currentSuit=getSuit().getId();
            if (currentSuit<27) GiveSuitKurt();



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                long[] pattern = {0, 500, 1000};
                vibrator.vibrate(pattern,0);
            }
        } else {
            rad = 1500;
            suit.ResetStamina();
            vibrator.cancel();
            long[] pattern = {0, 500, 500,250,500,155,500, 500, 500,250,500,155,500, 500, 500,250,500,155,500};
            vibrator.vibrate(pattern,-1);
        }

        service.NotifyActivity("DEAD");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    //endregion

    public void IncreaseNarkCount(){
        nark_count++;
        if(nark_count>10800)
            setDead(true);
    }

    public void IncreaseBoleznCount()

    {
        bolezn_count++;
        Log.d("Болезнь",""+bolezn_count);
        if (bolezn_count>1) {
            Log.d("Болезнь","Сброс счетчика");
            IncreaseHealth(-10);
            bolezn_count=0;
        }

    }

    public void setOtravaEffect()
    {
        if (getHeal_big()>50000) setHeal_big(50000);

    }

    public void IncreaseExp(int value){
        setExp(getExp()+value);
        if(value>=0){
            service.NotifyToast("Добавлено "+value+" опыта");
            String addict = Initializator.GetCurrentDF() + ".  " + "Получено "+value+" опыта";
            service.NotifyLog(addict);
        } else {

        }

    }

    public void IncreaseFire_resist (int value)
    {
        int t=getAdd_fire_resist()+value;
        if ((GetFireResist()-t)<=0)
            return;

        setAdd_fire_resist((getAdd_fire_resist()+value));
        Log.d("Fire",""+getAdd_fire_resist()+" "+GetFireResist());


    }

    public void IncreasePsi_resist (int value)
    {
        int t=getAdd_psi_resist()+value;
//        if ((GetPsiResist()-t)<=0)
//            return;

        setAdd_psi_resist((getAdd_psi_resist()+value));

    }

    public void IncreaseElectro_resist (int value)
    {
        int t=getAdd_electro_resist()+value;
        if ((GetElectroResist()-t)<=0)
            return;

        setAdd_electro_resist((getAdd_electro_resist()+value));

    }

    public void IncreaseGrav_resist (int value)
    {
        int t=getAdd_grav_resist()+value;
        if ((GetGravResist()-t)<=0)
            return;

        setAdd_grav_resist((getAdd_grav_resist()+value));

    }

    public void IncreaseRad_resist (int value)
    {
        int t=getAdd_rad_resist()+value;
        if ((GetRadResist()-t)<=0)
            return;

        setAdd_rad_resist((getAdd_rad_resist()+value));

    }

    public void IncreasePoison_resist (int value)
    {
        int t=getAdd_poison_resist()+value;
        if ((GetPoisonResist()-t)<=0)
            return;

        setAdd_poison_resist((getAdd_poison_resist()+value));

    }

    public void IncreaseExpCount(){
        setExpcount(getExpcount()+1);
    }

    public void RankRecalc(){
        //int exp=playerCharcteristics.getExp();
        if ( exp< 0) {
            setExp(0);
        }
        if (exp < 100)
            set_rankmod(1);


        if (exp > 100 && exp < 199) {
            set_rankmod(7);
        }
        if (exp >= 200) {
            set_rankmod(12);
        }
    }

    public String GetRankText(){
        String result="";
        switch (_rankmod){
            case 1:
                result="Сталкер";
                break;
            case 7:
                result="Ветеран";
                break;
            case 12:
                result="Мастер";
                break;
        }
        return result;
    }


    public void NarkEffect(){ //ЭФФЕКТ ПРИ НАРКОЗАВИСИМОСТИ
            heal_big = heal_big - 1000;
            exp = exp - 1;
            nark_count = 0;

    }


    public void ArtifactsEffects(){ //вызывается при необходимости просчета эффекта от артефактов на здоровье и радиацию
        IncreaseRad(Kolobok.GetAdditionRad() + Plenka.GetAdditionRad()+Puzir.GetAdditionRad()+Puzir.GetAdditionRad()+Heart.GetAdditionRad()+Battery.GetAdditionRad()/60);
        Damage(Kolobok.GetAdditionHeal() + Plenka.GetAdditionHeal()+Puzir.GetAdditionHeal()+Puzir.GetAdditionHeal()+Heart.GetAdditionHeal()+Battery.GetAdditionHeal()/60);
    }

    public boolean UseMedkit(){
        if(medikits==0)
            return false;
        medikits--;
        heal_big = heal_big + 25000;
        if (heal_big > 100000) heal_big = 99900;
        service.NotifyActivity("MEDKIT");
        service.NotifyToast("Добавлено 25 здоровья");
        return true;
    }

    public boolean UseMilMedkit(){
        if(mil_medikits==0)
            return false;
        mil_medikits--;
        heal_big = heal_big + 25000;
        if (heal_big > 100000) heal_big = 99900;
        rad = rad - 10000;
        if (rad < 1500) rad = 1500;
        service.NotifyActivity("MIL_MEDKIT");
        service.NotifyToast("Добавлено 25 здоровья, уровень набранной радиации снижен");
        return true;
    }

    public void UseRepairs()

    {

        if(repairs==0)
            return;
        repairs--;
        int s_repair = 250;
        suit.Repair(s_repair);
        String addict = Initializator.GetCurrentDF() + ".  " + "Отремонтирован костюм. Прочность: " + getIntegerSuitStamina()/10;
        service.NotifyLog(addict);
        service.NotifyActivity("REPAIRS");

    }

    public boolean UseSciMedkit(){
        if(sci_medikits==0)
            return false;
        sci_medikits--;
        heal_big = heal_big + 50000;
        if (heal_big > 100000) heal_big = 99900;
        rad = rad - 15000;
        if (rad < 1500) rad = 1500;
        service.NotifyActivity("SCI_MEDKIT");
        service.NotifyToast("Добавлено 50 здоровья, уровень набранной радиации снижен");
        return true;
    }

    public boolean UseAntirad(){
        if(antirads==0)
            return false;
        antirads--;
        rad = rad - 10000;
        if (rad < 1500) rad = 1500;
        service.NotifyActivity("ANTIRAD");
        service.NotifyToast("Выведено 1000 радиации");
        return true;
    }


    //region suits
    public void SuitRepair(){
        int s_repair = random.nextInt(500) + 500;
        suit.Repair(s_repair);
        String addict = Initializator.GetCurrentDF() + ".  " + "Отремонтирован костюм. Прочность: " + getIntegerSuitStamina()/10;
        service.NotifyLog(addict);
        service.NotifyActivity("SUIT");
    }

    public Suit getSuit() {
        return suit;
    }

    public float getSuitStamina(){
        if(suit==null)
            return 0;
        else
            return suit.getSuit_stam_big();
    }

    public int getIntegerSuitStamina(){
        return Math.round(getSuitStamina());
    }

    public void DestroySuit(){
        suit=null;
    }

    private void SetSuit(Suit value){
        Debug.Log("set suit");
        suit=value;
        service.NotifyActivity("SUIT");
    }
    public void SetSuitByID(int id){
        switch(id){
            case 1:
                GiveSuitKurt();
                break;

            case 2:
                GiveSuitZaria();
                break;

            case 3:
                GiveSuitSeva();
                break;

            case 4:
                GiveSuitExo();
                break;

            case 5:
                GiveSuitBand();
                break;

            case 6:
                GiveSuitPlas();
                break;

            case 7:
                GiveSuitBron();
                break;

            case 8:
                GiveSuitBulat();
                break;

            case 9:
                GiveSuitBerill();
                break;

            case 10:
                GiveSuitDolgKombez();
                break;

            case 11:
                GiveSuitDolgBron();
                break;

            case 12:
                GiveSuitDolgExa();
                break;

            case 13:
                GiveSuitStrazh();
                break;

            case 14:
                GiveSuitVeter();
                break;

            case 15:
                GiveSuitFreeExa();
                break;

            case 16:
                GiveSuitMonKombez();
                break;

            case 17:
                GiveSuitMonBron();
                break;

            case 18:
                GiveSuitMonExa();
                break;

            case 19:
                GiveSuitNaemKombez();
                break;

            case 20:
                GiveSuitNaemBron();
                break;

            case 21:
                GiveSuitCS1();
                break;

            case 22:
                GiveSuitCS2();
                break;

            case 23:
                GiveSuitCS3();
                break;

            case 24:
                GiveSuitHalat();
                break;

            case 25:
                GiveSuitEcolog();
                break;

            case 26:
                GiveSuitEcolog2();
                break;

            case 27:
                GiveSuitMonster();
                break;

            case 28:
                GiveSuitIgrotex();
                break;

            case 29:
                GiveSuitBolot();
                break;

        }}


    public void GiveSuitKurt() { suit=new Suit(service,1,"Кожаная куртка","",1000,5,5,10,0,5,10);}
    public void GiveSuitZaria(){ suit=new Suit(service,2,"Заря","",1000,30,25,25,15,30,25);}
    public void GiveSuitSeva(){ suit=new Suit(service,3,"Сева","",1000,55,50,55,30,55,50);}
    public void GiveSuitExo(){ suit=new Suit(service,4,"Экзоскелет","",1000,60,60,50,30,60,55);}
    public void GiveSuitBand(){ suit=new Suit(service,5,"Бандитская куртка","",1000,10,10,10,0,0,10);}
    public void GiveSuitPlas(){ suit=new Suit(service,6,"Кожаный плащ","",1000,15,10,10,0,5,10);}
    public void GiveSuitBron(){ suit=new Suit(service,7,"Армейский бронежилет","",1000,15,15,15,5,15,15);}
    public void GiveSuitBulat(){ suit=new Suit(service,8,"Булат","",1000,35,20,25,8,20,25);}
    public void GiveSuitBerill(){ suit=new Suit(service,9,"Берилл","",1000,50,35,40,15,35,40);}
    public void GiveSuitDolgKombez(){ suit=new Suit(service,10,"Комбинезон Долга (ПС3-9д)","",1000,25,25,25,10,25,25);}
    public void GiveSuitDolgBron(){ suit=new Suit(service,11,"Броня Долга (ПС3-9Мд)","",1000,40,40,40,20,40,40);}
    public void GiveSuitDolgExa(){ suit=new Suit(service,12,"Экза Долга","",1000,60,60,55,30,55,60);}
    public void GiveSuitStrazh(){ suit=new Suit(service,13,"Ветер Свободы","",1000,20,20,35,10,30,20);}
    public void GiveSuitVeter(){ suit=new Suit(service,14,"Страж Свободы","",1000,35,35,50,20,45,35);}
    public void GiveSuitFreeExa(){ suit=new Suit(service,15,"Экза Свободы","",1000,55,50,65,30,65,55);}
    public void GiveSuitMonKombez(){ suit=new Suit(service,16,"Комбинезон Монолита","",1000,30,30,30,100,30,30);}
    public void GiveSuitMonBron(){ suit=new Suit(service,17,"Броня Монолита","",1000,45,45,45,100,45,45);}
    public void GiveSuitMonExa(){ suit=new Suit(service,18,"Экза Монолита","",1000,60,60,60,100,60,60);}
    public void GiveSuitNaemKombez(){ suit=new Suit(service,19,"Комбинезон Наёмников","",1000,25,35,20,10,20,25);}
    public void GiveSuitNaemBron(){ suit=new Suit(service,20,"Броня Наёмников","",1000,40,50,35,20,35,40);}
    public void GiveSuitCS1(){ suit=new Suit(service,21,"Комбинезон ЧН-1","",1000,15,20,25,10,25,15);}
    public void GiveSuitCS2(){ suit=new Suit(service,22,"Броня ЧН-2","",1000,28,33,38,20,38,28);}
    public void GiveSuitCS3(){ suit=new Suit(service,23,"Бронекостюм ЧН-3а","",1000,45,50,55,30,60,45);}
    public void GiveSuitHalat(){ suit=new Suit(service,24,"Халат Ученых","",1000,5,5,5,0,5,5);}
    public void GiveSuitEcolog(){ suit=new Suit(service,25,"Эколог (ССП-99)","",1000,28,28,38,20,38,28);}
    public void GiveSuitEcolog2(){ suit=new Suit(service,26,"ССП-99М","",1000,45,45,55,30,55,45);}
    public void GiveSuitMonster(){ suit=new Suit(service,27,"Костюм «Монстр»","",1000,90,90,90,100,90,90);}
    public void GiveSuitIgrotex(){ suit=new Suit(service,28,"Костюм «Игротех»","",1000,100,100,100,100,100,100);}
    public void GiveSuitBolot(){ suit=new Suit(service,29,"Костюм «Болотный Доктор»","",1000,75,75,75,75,75,75);}




//    public void GiveSuitKurt() { SetSuit(new Suit(service,1,"Кожаная куртка","Нет описания",1000,5,5,10,0,5,10));    }
//    public void GiveSuitZaria(){ suit=new Suit(service,2,"Заря","Нет описания",1000,25,25,20,0,35,20);}
//    public void GiveSuitSeva(){ suit=new Suit(service,3,"Сева","Нет описания",1000,40,40,35,0,50,35);}
//    public void GiveSuitExo(){ suit=new Suit(service,4,"Экзоскелет","Нет описания",1000,60,60,55,5,60,55);}
//    public void GiveSuitBand(){ suit=new Suit(service,5,"Бандитская куртка","Нет описания",1000,10,10,10,0,0,10);}
//    public void GiveSuitPlas(){ suit=new Suit(service,6,"Кожаный плащ","Нет описания",1000,15,10,10,0,5,10);}
//    public void GiveSuitBron(){ suit=new Suit(service,7,"Армейский бронежилет","Нет описания",1000,15,15,15,0,15,15);}
//    public void GiveSuitBulat(){ suit=new Suit(service,8,"Булат","Нет описания",1000,35,20,25,0,20,25);}
//    public void GiveSuitBerill(){ suit=new Suit(service,9,"Берилл","Нет описания",1000,50,35,40,0,35,40);}
//    public void GiveSuitDolgKombez(){ suit=new Suit(service,10,"Комбинезон Долга (ПС3-9д)","Нет описания",1000,25,25,25,0,25,25);}
//    public void GiveSuitDolgBron(){ suit=new Suit(service,11,"Броня Долга (ПС3-9Мд)","Нет описания",1000,40,40,40,0,40,40);}
//    public void GiveSuitDolgExa(){ suit=new Suit(service,12,"Экзоскелет Долга","Нет описания",1000,60,60,55,10,55,60);}
//    public void GiveSuitStrazh(){ suit=new Suit(service,13,"Страж Свободы","Нет описания",1000,20,20,35,0,30,20);}
//    public void GiveSuitVeter(){ suit=new Suit(service,14,"Ветер Свободы","Нет описания",1000,35,35,50,0,45,35);}
//    public void GiveSuitFreeExa(){ suit=new Suit(service,15,"Экзоскелет Свободы","Нет описания",1000,55,50,65,10,65,55);}
//    public void GiveSuitMonKombez(){ suit=new Suit(service,16,"Комбинезон Монолита","Нет описания",1000,30,30,30,100,30,30);}
//    public void GiveSuitMonBron(){ suit=new Suit(service,17,"Броня Монолита","Нет описания",1000,45,45,45,100,45,45);}
//    public void GiveSuitMonExa(){ suit=new Suit(service,18,"Экзоскелет Монолита","Нет описания",1000,60,60,60,100,60,60);}
//    public void GiveSuitNaemKombez(){ suit=new Suit(service,19,"Комбинезон Наёмников","Нет описания",1000,25,35,20,0,20,25);}
//    public void GiveSuitNaemBron(){ suit=new Suit(service,20,"Броня Наёмников","Нет описания",1000,40,50,35,0,35,40);}
//    public void GiveSuitCS1(){ suit=new Suit(service,21,"Комбинезон ЧН-1","Нет описания",1000,15,20,25,0,25,15);}
//    public void GiveSuitCS2(){ suit=new Suit(service,22,"Броня ЧН-2","Нет описания",1000,28,33,38,0,38,28);}
//    public void GiveSuitCS3(){ suit=new Suit(service,23,"Бронекостюм ЧН-3а","Нет описания",1000,45,50,55,5,60,45);}
//    public void GiveSuitHalat(){ suit=new Suit(service,24,"Халат Ученых","Нет описания",1000,5,5,5,0,5,5);}
//    public void GiveSuitEcolog(){ suit=new Suit(service,25,"Эколог (ССП-99)","Нет описания",1000,28,28,38,5,38,28);}
//    public void GiveSuitEcolog2(){ suit=new Suit(service,26,"ССП-99М","Нет описания",1000,45,45,55,5,55,45);}
//    public void GiveSuitMonster(){ suit=new Suit(service,27,"Костюм «Монстр»","Нет описания",1000,90,90,90,90,90,90);}
//    public void GiveSuitIgrotex(){ suit=new Suit(service,28,"Костюм «Игротех»","Нет описания",1000,100,100,100,100,100,100);}
//    public void GiveSuitBolot(){ suit=new Suit(service,29,"Костюм «Болотный Доктор»","Нет описания",1000,75,75,75,75,75,75);}



    /*public void GiveSuitKurtka() {
        SetSuit(new Suit(service,1,"СТАЛКЕРСКАЯ КУРТКА","Не обеспечивает даже самой минимальной защиты. Дешевая и распространенная",1000,
                5,0,0,0,0));
    }

    public void GiveSuitZaria() {
        suit=new Suit(service,2,"Костюм \"Заря\"","Нет описания",1000,R.drawable.zarianew,
                25,5,20,5,10);
    }

    public void GiveSuitBand() {
        suit=new Suit(service,3,"Кольчужная куртка","Нет описания",1000,R.drawable.bandnew,
                5,0,0,0,0);
    }


    public void GiveSuitZakat() {
        suit=new Suit(service,4,"Комбинезон Закат","Нет описания",1000,R.drawable.scinew,
                25,5,20,5,10);}

    public void GiveSuitBerill() {
        suit=new Suit(service,5,"Комбинезон БЕРИЛЛ-5M","Нет описания",1000,R.drawable.berill,
                10,5,10,5,5);
    }
    public void GiveSuitScat() {
        suit=new Suit(service,6,"комбинезон СКАТ-9","Нет описания",1000,R.drawable.dolg,
                25,20,25,20,25);
    }

    public void GiveSuitPS() {
        suit=new Suit(service,7,"Комбинезон ПС-5М","Нет описания",1000,R.drawable.freedomnew,
                25,5,20,5,10);
    }

    public void GiveSuitPS3() {
        suit=new Suit(service,8,"Комбинезон ПС3-9д","Нет описания",1000,R.drawable.monolitnew,
                25,20,25,20,25);
    }

    public void GiveSuitVeter() {
        suit=new Suit(service,9,"Комбинезон ВЕТЕР СВОБОДЫ","Нет описания",1000,R.drawable.scinew,
                25,5,10,10,10);
    }

    public void GiveSuitStrazh() {
        suit=new Suit(service,10,"Комбинезон СТРАЖ СВОБОДЫ","Нет описания",1000,R.drawable.scinew,
                25,20,25,20,25);
    }


    public void GiveSuitClearOne() {
        suit=new Suit(service,11,"Комбинезон ЧН-1","Нет описания",1000,R.drawable.sevanew,
                10,10,10,10,10);
    }


    public void GiveSuitClearThree() {
        suit=new Suit(service,12,"Комбинезон ЧН-3А","Нет описания",1000,R.drawable.ecolognew,
                20,20,20,20,20);
    }

    public void GiveSuitNaem() {
        suit=new Suit(service,13,"Комбинезон наемника","Нет описания",1000,R.drawable.f_exa,
                25,10,10,25,10);
    }

    public void GiveSuitEcolog() {
        suit=new Suit(service,14,"Комбинезон ССП-99 ЭКОЛОГ","Нет описания",1000,R.drawable.bulatnew,
                90,50,90,50,75);
    }



    public void GiveSuitMonolit() {
        suit=new Suit(service,15,"Комбинезон СТРАЖ МОНОЛИТА","Нет описания",1000,R.drawable.d_exa,
                25,25,25,100,90);
    }

    public void GiveSuitSeva() {
        suit=new Suit(service, 16,"Комбинезон СЕВА","Нет описания",1000,R.drawable.m_exa,
                50,50,50,50,50);
    }

    public void GiveSuitExa() {
        suit=new Suit(service, 17,"Экзоскелет","Нет описания",1000,R.drawable.naemn,
                75,75,25,50,50);

    }*/



    public void DamageSuit(float value){

       // float oldStamina=suit.getSuit_stam_big();
        suit.DecreaseStamina(value);
//        if(oldStamina!=suit.getSuit_stam_big()&&suit.getSuit_stam_big()==0){
//            Damage(heal_big);
//            String addict = Initializator.GetCurrentDF() + ".  " + "Смерть в гравитационной аномалии - поломка костюма";
//            service.NotifyLog(addict);

     //   }
    }

    public void setLatitude(double value){
        latitude=value;
    }

    public void setLongitude(double value){
        longitude=value;
    }

    public double getLatitude() {return latitude;}
    public double getLongitude(){return longitude;}


    public void setDistance(int distance){this.distance=distance;}

    public int getDistance() {return distance;}


    public boolean UseBolt()
    {
        if (bolt==0)
            return false;
        bolt--;
        setBolt(bolt);
        service.NotifyActivity("BOLT");
        service.NotifyToast("Использован Б.О.Л.Т.");
        return true;

    }

    public void setBolt(int bolt) {
        this.bolt = bolt;
    }



    public void GiveBolt(){
        setBolt(getBolt()+1);
        service.NotifyActivity("BOLT");
        //String addict = Initializator.GetCurrentDF() + ".  " + "Восстановлен антирад";
        //service.NotifyLog(addict);
    }

    public int getBolt()
    {
        return bolt;
    }
    public boolean getHunter()
    {
        return hunter;
    }

    public void setGipnos(boolean gipnos) {
        if(this.gipnos==gipnos)
            return;
        this.gipnos = gipnos;
    }

    public boolean getGipnos() {
        return gipnos;
    }


    //endregion


}
