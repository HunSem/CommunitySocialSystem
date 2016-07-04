package com.huan.percy.communitysocialsystem;

import java.util.Random;

/**
 * Created by Percy on 2016/7/1.
 */
public  class FaceMatch {
    public  int getLocalFace(){
        Random rand = new Random();
        int randNum = rand.nextInt(20)+1;
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
            default:
                return R.drawable.heroes_01;
        }
    }

}
