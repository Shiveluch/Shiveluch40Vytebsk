package com.example.user.pdashiveluch.classes;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.user.pdashiveluch.R;

import java.util.ArrayList;
import java.util.Random;



public class PDA_AudioManager implements SoundPool.OnLoadCompleteListener{
    //private static PDA_AudioManager instance;
    //private static PDA_AudioManager instance;
    public enum AppSounds {KISEL, CONTOLLER, DEATH, RADIATION, FIRE, GRAV, ELECTRA, BUERER, MINE, MONSTROBOI_DEATH, IPLAN, MENU, PSIH, VIBR, ZOMBIE, MONOLIT, EOVIBR, Monstroboi,ATMO, BEFV, BLOWOUT, UNDERCONTROL};



    private Context appContext;

    private SoundPool mSoundPool;


    private int mKisel;
    private int mController;
    private int mDeath;
    private int mFire;
    private int mGrav;
    private int mMonstroboi_death;
    private int mBeforeVybr;
    private int mMine;
    private int mBuerer;
    private int mElectra;
    private int mIplan;
    private int mMenu;
    private int mPsih;
    private int mVybr;
    private int mZombie;
    private int mRadiation;
    private int mEndOfVibr;
    private int mMonstroboi;


    private int[] mMonolitSounds;

    private int [] mAtmoVoice;
    private int [] mBlowoutVoice;
    private int [] mControlVoice;

    private AudioManager audioManager;
    private float curVolume;
    private float maxVolume;
    private float leftVolume;
    private float rightVolume;
    private int priority;
    private int no_loop;
    private float normal_playback_rate;
    private ArrayList<Integer> LoadedRAWs=new ArrayList<>();
    private ArrayList<Integer> PlayBuf=new ArrayList<>();

        /*public static PDA_AudioManager GetInstance(Context contextValue){
            if(instance==null)
                instance=new PDA_AudioManager(contextValue);
            return instance;
        }*/

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.d("аудива", "onLoadComplete, sampleId = " + sampleId + ", status = " + status);
        if(status==0)
            LoadedRAWs.add(sampleId);
        if(PlayBuf.contains(sampleId))
            Play(sampleId);
    }

    private void Play(int soundID){
        if(LoadedRAWs.contains(soundID)){
            RefreshVolumeSettings();
            Log.d("аудива","попытка воспроизведения аудио ресурсов");
            mSoundPool.play(soundID, leftVolume, rightVolume, priority, no_loop,
                    normal_playback_rate);
        } else {
            Log.d("аудива","ресурс ещё не загружен, закинем звук в очередь");
            PlayBuf.add(soundID);
        }

    }

    public PDA_AudioManager (Context contextValue) {
        appContext = contextValue;

        mSoundPool = new SoundPool(16, AudioManager.STREAM_SYSTEM, 100);
        mSoundPool.setOnLoadCompleteListener(this);
        Log.d("аудива","старт загрузки аудио ресурсов");
        mMenu = mSoundPool.load(appContext,R.raw.menu,1);
        mRadiation = mSoundPool.load(appContext, R.raw.file1, 1);
        mFire = mSoundPool.load(appContext, R.raw.fire, 1);
        mGrav = mSoundPool.load(appContext, R.raw.grav, 1);
        mPsih = mSoundPool.load(appContext, R.raw.psih, 1);
        mKisel = mSoundPool.load(appContext, R.raw.bulk, 1);
        mVybr = mSoundPool.load(appContext, R.raw.vybr, 1);
        mMine=mSoundPool.load(appContext, R.raw.mine, 1);
        mController = mSoundPool.load(appContext, R.raw.control, 1);
        mDeath = mSoundPool.load(appContext, R.raw.death, 1);
        mZombie = mSoundPool.load(appContext, R.raw.zombie, 1);
        mEndOfVibr=mSoundPool.load(appContext,R.raw.eovybr,1);
        mElectra=mSoundPool.load(appContext,R.raw.electra,1);
        mBuerer=mSoundPool.load(appContext, R.raw.buerer, 1);
        mBeforeVybr=mSoundPool.load(appContext,R.raw.befv,1);


        mMonolitSounds = new int[7];
        mMonolitSounds[0] = mSoundPool.load(appContext, R.raw.voice1, 1);
        mMonolitSounds[1] = mSoundPool.load(appContext, R.raw.voice2, 1);
        mMonolitSounds[2] = mSoundPool.load(appContext, R.raw.voice3, 1);
        mMonolitSounds[3] = mSoundPool.load(appContext, R.raw.voice4, 1);
        mMonolitSounds[4] = mSoundPool.load(appContext, R.raw.voice5, 1);
        mMonolitSounds[5] = mSoundPool.load(appContext, R.raw.voice6, 1);
        mMonolitSounds[6] = mSoundPool.load(appContext, R.raw.voice7, 1);
        Log.d("аудива","завершение загрузки аудио ресурсов");

        mAtmoVoice=new int[10];
        mAtmoVoice[0]=mSoundPool.load(appContext,R.raw.atmo1,1);
        mAtmoVoice[1]=mSoundPool.load(appContext,R.raw.atmo2,1);
        mAtmoVoice[2]=mSoundPool.load(appContext,R.raw.atmo3,1);
        mAtmoVoice[3]=mSoundPool.load(appContext,R.raw.atmo4,1);
        mAtmoVoice[4]=mSoundPool.load(appContext,R.raw.atmo5,1);
        mAtmoVoice[5]=mSoundPool.load(appContext,R.raw.atmo6,1);
        mAtmoVoice[6]=mSoundPool.load(appContext,R.raw.atmo7,1);
        mAtmoVoice[7]=mSoundPool.load(appContext,R.raw.atmo8,1);
        mAtmoVoice[8]=mSoundPool.load(appContext,R.raw.atmo9,1);
        mAtmoVoice[9]=mSoundPool.load(appContext,R.raw.atmo10,1);


        mBlowoutVoice=new int[8];
        mBlowoutVoice[0]=mSoundPool.load(appContext,R.raw.blowout1,1);
        mBlowoutVoice[1]=mSoundPool.load(appContext,R.raw.blowout2,1);
        mBlowoutVoice[2]=mSoundPool.load(appContext,R.raw.blowout3,1);
        mBlowoutVoice[3]=mSoundPool.load(appContext,R.raw.blowout4,1);
        mBlowoutVoice[4]=mSoundPool.load(appContext,R.raw.blowout5,1);
        mBlowoutVoice[5]=mSoundPool.load(appContext,R.raw.blowout6,1);
        mBlowoutVoice[6]=mSoundPool.load(appContext,R.raw.blowout7,1);
        mBlowoutVoice[7]=mSoundPool.load(appContext,R.raw.blowout8,1);


        mControlVoice=new int[4];
        mControlVoice[0]=mSoundPool.load(appContext,R.raw.zomb1,1);
        mControlVoice[1]=mSoundPool.load(appContext,R.raw.zomb2,1);
        mControlVoice[2]=mSoundPool.load(appContext,R.raw.zomb3,1);
        mControlVoice[3]=mSoundPool.load(appContext,R.raw.zomb4,1);




        //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        RefreshVolumeSettings();
        priority = 1;
        no_loop = 0;
        normal_playback_rate = 1f;
    }

    private void RefreshVolumeSettings(){
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        leftVolume = curVolume / maxVolume;
        rightVolume = curVolume / maxVolume;
    }

    private void AtmoSounds()
    {
        Random randoms = new Random();
        int rnd;
        rnd = randoms.nextInt(9);
        if (rnd > 9)
            rnd = 9;
        Play(mAtmoVoice[rnd]);

    }


    private void BlowoutSounds()
    {
        Random randoms = new Random();
        int rnd;
        rnd = randoms.nextInt(7);
        if (rnd > 7)
            rnd = 7;
        Play(mBlowoutVoice[rnd]);

    }


    private void UnderControl()
    {
        Random randoms = new Random();
        int rnd;
        rnd = randoms.nextInt(4) ;
        if (rnd > 3)
            rnd = 3;
        Log.d("UnderRND","Random"+rnd);
        Play(mControlVoice[rnd]);

    }

    private void MonolitVoice() {
        Random randoms = new Random();
        int rnd;
        rnd = randoms.nextInt(6) + 1;
        if (rnd > 7)
            rnd = 7;
        Play(mMonolitSounds[rnd]);

    }
    public void PlaySound(AppSounds value) {
        switch (value) {
            case KISEL:
                Play(mKisel);
                break;
            case CONTOLLER:
                Play(mController);
                break;
            case DEATH:
                Play(mDeath);
                break;
            case RADIATION:
                Play(mRadiation);
                break;
            case FIRE:
                Play(mFire);
                break;
            case GRAV:
                Play(mGrav);
                break;

            case ELECTRA:
                Play(mElectra);
                break;
            case BUERER:
                Play(mBuerer);
                break;
            case MONSTROBOI_DEATH:
                Play(mMonstroboi_death);
                break;
            case MINE:
                Play(mMine);
                break;

            case IPLAN:
                Play(mIplan);
                break;
            case MENU:
                Play(mMenu);
                break;
            case PSIH:
                Play(mPsih);
                break;
            case VIBR:
                Play(mVybr);
                break;
            case BEFV:
                Play(mBeforeVybr);
                break;
            case ZOMBIE:
                Play(mZombie);
                break;
            case UNDERCONTROL:
                UnderControl();
                break;

            case MONOLIT:
                MonolitVoice();
                break;
            case ATMO:
                AtmoSounds();
                break;
            case BLOWOUT:
                BlowoutSounds();
                break;
            case EOVIBR:
                Play(mEndOfVibr);
                break;
            case Monstroboi:
                mSoundPool.play(mMonstroboi,leftVolume,rightVolume,priority,no_loop,normal_playback_rate);
                break;
        }
    }





}
