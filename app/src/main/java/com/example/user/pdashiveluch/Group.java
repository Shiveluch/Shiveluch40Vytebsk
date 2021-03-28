package com.example.user.pdashiveluch;

import com.example.user.pdashiveluch.classes.Places;

public final class Group {
    private Places[] basePlaces;
    private String name;
    private int id;
    private int group_pic;

    public Group(int id,String name, int group_pic, Places[] basePlaces){
        this.id=id;
        this.name=name;
        this.basePlaces=new Places[basePlaces.length];
        this.group_pic=group_pic;
        for (int c=0;c<basePlaces.length;c++) {
            this.basePlaces[c]=basePlaces[c];
        }
    }

    public String getName(){
        return name;
    }

    public boolean isBasePlace(Places value){
        for (Places pl:basePlaces) {
            if(pl==value)
                return true;
        }
        return false;
    }

    public int getID(){
        return id;
    }
}
