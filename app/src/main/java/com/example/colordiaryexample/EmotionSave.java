package com.example.colordiaryexample;

import android.app.Application;

public class EmotionSave extends Application {
    private int state = 0;
    private int badstate = 0;
    private int bad1=0,bad2=0,bad3=0,bad4=0,bad5=0,bad6=0,bad7=0,bad8=0;


    @Override
    public void onCreate(){
        state = 0;
        badstate = 0;
        bad1=0;
        bad2=0;
        bad3=0;
        bad4=0;
        bad5=0;
        bad6=0;
        bad7=0;
        bad8=0;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setBadstate(int badstate) {
        this.badstate = badstate;
    }

    public int getBadstate() {
        return badstate;
    }



    public int getBad1() {
        return bad1;
    }

    public void setBad1(int bad1, int bad11) {
        this.bad1 = bad1+bad11;
    }

    public int getBad2() {
        return bad2;
    }

    public void setBad2(int bad2, int bad22) {
        this.bad2 = bad2+bad22;
    }

    public int getBad3() {
        return bad3;
    }

    public void setBad3(int bad3, int bad33) {
        this.bad3 = bad3+bad33;
    }

    public int getBad4() {
        return bad4;
    }

    public void setBad4(int bad4, int bad44) {
        this.bad4 = bad4+bad44;
    }

    public int getBad5() {
        return bad5;
    }

    public void setBad5(int bad5, int bad55) {
        this.bad5 = bad5+bad55;
    }

    public int getBad6() {
        return bad6;
    }

    public void setBad6(int bad6, int bad66) {
        this.bad6 = bad6+ bad66;
    }

    public int getBad7() {
        return bad7;
    }

    public void setBad7(int bad7, int bad77) {
        this.bad7 = bad7+ bad77;
    }

    public int getBad8() {
        return bad8;
    }

    public void setBad8(int bad8, int bad88) {
        this.bad8 = bad8+ bad88;
    }
}
