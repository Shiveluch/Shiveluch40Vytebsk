package com.example.user.pdashiveluch.classes;

import android.util.Log;

import com.example.user.pdashiveluch.ShiveluchService;

import java.io.Serializable;

//region serialized
public class DataPack implements Serializable {
    public boolean dead;
    public int heal_big;//реальный уровень здоровья

    public int rankmod;
    public String RankText;
    public boolean group; //2. принадлежность к группировке,
    public boolean nark; // наркозависимость
    public boolean v_otklik;
    public boolean bolezn; //Неизвестная болезнь
    public boolean otrava; //отравление
    public String group_name;
    public int group_id;

    //private Suit suit; //идентификатор костюма. не может быть пустым. хотя бы куртка простая
    public String suitName;
    public int suitID;
    public String suitDescription;
    public int suit_stam_big;//реальная прочность костюма в дальнейшем перенести в характеристики костюма

    public int basic_resist_fire, basic_resist_grav, basic_resist_poison, basic_resist_rad,
            basic_resist_psi, basic_resist_electro;
    public int add_fire_resist, add_poison_resist, add_electro_resist, add_rad_resist, add_grav_resist, add_psi_resist;
    public int suitImage;
    //private int z_count = 100;//1. устойчивость атаке контролера
    public boolean zombi;  //2. Состояние зомбированности
    public boolean psi_helm; //флаг наличия/отсутствия пси-шлема
    public boolean respirator; //флаг наличия/отсутствия респиратора
    public boolean protivogas; //флаг наличия/отсутствия противогаза
    public boolean SZD; //флаг наличия/отсутствия СЗД
    public boolean tropa; //флаг наличия/отсутствия Тропы ЧН
    public boolean immun; //наличие иммунитета

    public int PsiHealth; //писхическое здоровье
    public int rad = 150; //уровень радиации (реальный и для отображения на шкале от 1 до 5000

    public int medikits, mil_medikits, sci_medikits, antirads; //аптечки и антирадин
    public int power;
    public int loctime;
    public int exp = 1; //1. Опыт 2. Счетчик набора опыта при прохождении определенного времени
    public boolean monvoice; //зов монолита
    public String name; //позывной игрока
    public Places place;
    public String KolobokStatus;
    public String PlenkaStatus;
    public String PuzirStatus;
    public String HeartStatus;
    public String BatteryStatus;
    public String KolobokDescription;
    public String PlenkaDescription;
    public String PuzirDescription;
    public String HeartDescription;
    public String BatteryDescription;
    public double latitude,longitude;
    public int distance;

    public int progressanomaly;
    public int FireResist;
    public int RadResist;
    public int PoisonResist;
    public int GravResist;
    public int PsiResist;
    public int ElectroResist;
    public int Containers;
    public int repairs;



    public static DataPack GetDataPack(ShiveluchService service){
        //Debug.Log("GetDataPack 1");
        PlayerCharcteristics val=service.playerCharcteristics;
        DataPack serialized=new DataPack();

        serialized.dead=val.isDead();
        //Debug.Log("GetDataPack 2");
        serialized.heal_big=val.getIntegerHealth();
        //Debug.Log("GetDataPack 2.5");
        serialized.rankmod=val.get_rankmod();
        //Debug.Log("GetDataPack x");
        serialized.RankText=val.GetRankText();
        serialized.Containers=val.getAllcontainers();
        serialized.group=val.is_group();
        serialized.nark=val.is_nark();
        serialized.v_otklik=val.is_v_otklik();
        serialized.bolezn=val.isBolezn();
        serialized.otrava=val.isOtrava();
        serialized.group_name=val.getGroup().getName();
        serialized.group_id=val.getGroup().getID();
        serialized.suitName=val.getSuit().getName();
        serialized.suitID=val.getSuit().id;
        serialized.distance=val.getDistance();
        //Debug.Log("GetDataPack 4");
        serialized.suitDescription=val.getSuit().getDescription();
        serialized.suit_stam_big=val.getIntegerSuitStamina();
        serialized.basic_resist_fire=val.getSuit().getBasic_resist_fire();
        serialized.basic_resist_electro=val.getSuit().getBasic_resist_electro();
        serialized.basic_resist_grav=val.getSuit().getBasic_resist_grav();
        serialized.basic_resist_poison=val.getSuit().getBasic_resist_poison();
        serialized.basic_resist_psi=val.getSuit().getBasic_resist_psi();
        serialized.basic_resist_rad=val.getSuit().getBasic_resist_rad();
        serialized.add_fire_resist=val.getAdd_fire_resist();
        serialized.add_poison_resist=val.getAdd_poison_resist();
        serialized.add_grav_resist=val.getAdd_grav_resist();
        serialized.add_electro_resist=val.getAdd_electro_resist();
        serialized.add_psi_resist=val.getAdd_psi_resist();
        serialized.add_rad_resist=val.getAdd_rad_resist();
        serialized.suitImage=val.getSuit().GetImageNum();
        serialized.zombi=val.isZombi();
        serialized.immun=val.isImmun();
        serialized.psi_helm=val.isPsi_helm();
        serialized.respirator=val.isRespirator();
        serialized.protivogas=val.isProtivogas();
        serialized.SZD=val.isSZD();
        serialized.tropa=val.isTropa();
        serialized.power=val.getPower();
        serialized.loctime=val.getLoctime();

        serialized.PsiHealth=val.getIntegerPsiHealth();
        serialized.rad=val.getIntegerRad();
        serialized.medikits=val.getMedikits();
        serialized.mil_medikits=val.getMilMedikits();
        serialized.sci_medikits=val.getSciMedikits();
        serialized.repairs=val.getRepairs();
        serialized.antirads=val.getAntirads();
        serialized.exp=val.getExp();
        serialized.monvoice=val.isMonvoice();
        serialized.name=val.getName();

        serialized.latitude=val.getLatitude();
        serialized.longitude=val.getLongitude();

        ArtSlot artSlot;
        artSlot=val.getKolobok();
        serialized.KolobokStatus=artSlot.GetStringStatus();
        serialized.KolobokDescription=artSlot.GetDescription();
        artSlot=val.getPlenka();
        serialized.PlenkaStatus=artSlot.GetStringStatus();
        serialized.PlenkaDescription=artSlot.GetDescription();
        artSlot=val.getPuzir();
        serialized.PuzirStatus=artSlot.GetStringStatus();
        serialized.PuzirDescription=artSlot.GetDescription();
        artSlot=val.getHeart();
        serialized.HeartStatus=artSlot.GetStringStatus();
        serialized.HeartDescription=artSlot.GetDescription();
        artSlot=val.getBattery();
        serialized.BatteryStatus=artSlot.GetStringStatus();
        serialized.BatteryDescription=artSlot.GetDescription();

        serialized.progressanomaly=service.getProgressanomaly();
        //Debug.Log("GetDataPack 5");

        serialized.FireResist=service.playerCharcteristics.GetFireResist();
        serialized.RadResist=service.playerCharcteristics.GetRadResist();
        serialized.PoisonResist=service.playerCharcteristics.GetPoisonResist();
        serialized.GravResist=service.playerCharcteristics.GetGravResist();
        serialized.PsiResist=service.playerCharcteristics.GetPsiResist();
        serialized.ElectroResist=service.playerCharcteristics.GetElectroResist();
        serialized.place=service.getPlace();
        //Debug.Log("GetDataPack 6");

        return serialized;
    }

}

