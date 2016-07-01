package com.huan.percy.communitysocialsystem;

import java.util.Random;

/**
 * Created by Percy on 2016/7/1.
 */
public  class FaceMatch {
    public  int getLocalFace(){
        Random rand = new Random();
        int randNum = rand.nextInt(40)+1;
        switch(randNum) {
            case 1:
                return R.drawable.heroes_01;
            case 2:
                return R.drawable.heroes_02;
            case 3:
                return R.drawable.heroes_03;
            case 4:
                return R.drawable.heroes_04;
            case 5:
                return R.drawable.heroes_05;
            case 6:
                return R.drawable.heroes_06;
            case 7:
                return R.drawable.heroes_07;
            case 8:
                return R.drawable.heroes_08;
            case 9:
                return R.drawable.heroes_09;
            case 10:
                return R.drawable.heroes_10;
            case 11:
                return R.drawable.heroes_11;
            case 12:
                return R.drawable.heroes_12;
            case 13:
                return R.drawable.heroes_13;
            case 14:
                return R.drawable.heroes_14;
            case 15:
                return R.drawable.heroes_15;
            case 16:
                return R.drawable.heroes_16;
            case 17:
                return R.drawable.heroes_17;
            case 18:
                return R.drawable.heroes_18;
            case 19:
                return R.drawable.heroes_19;
            case 20:
                return R.drawable.heroes_20;
            case 21:
                return R.drawable.heroes_21;
            case 23:
                return R.drawable.heroes_23;
            case 24:
                return R.drawable.heroes_24;
            case 25:
                return R.drawable.heroes_25;
            case 26:
                return R.drawable.heroes_26;
            case 27:
                return R.drawable.heroes_27;
            case 28:
                return R.drawable.heroes_28;
            case 29:
                return R.drawable.heroes_29;
            case 30:
                return R.drawable.heroes_30;
            case 31:
                return R.drawable.heroes_31;
            case 32:
                return R.drawable.heroes_32;
            case 33:
                return R.drawable.heroes_33;
            case 34:
                return R.drawable.heroes_34;
            case 22:
                return R.drawable.heroes_22;
            case 35:
                return R.drawable.heroes_35;
            case 36:
                return R.drawable.heroes_36;
            case 37:
                return R.drawable.heroes_37;
            case 38:
                return R.drawable.heroes_38;
            case 39:
                return R.drawable.heroes_39;
            case 40:
                return R.drawable.heroes_40;
            default:
                return R.drawable.heroes_01;
        }
    }

}
