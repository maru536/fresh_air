package com.perfect.freshair.Utils;

public class MicroDustUtils {

    public static final String dust_good = "좋음";
    public static final String dust_normal = "보통";
    public static final String dust_bad = "나쁨";
    public static final String dust_worst = "매우 나쁨";


    static public String parseDustValue(int value)
    {
        String ret = dust_normal;
        //좋음`은 0~15㎍/㎥, `보통`은 16~35㎍/㎥, `나쁨`은 36~75㎍/㎥, `매우 나쁨`은 76㎍/㎥
        if(value <= 15)
        {
            ret = dust_good;
        }else if(value <= 35)
        {
            ret = dust_normal;
        }else if(value <= 75)
        {
            ret = dust_bad;
        }else{
            ret = dust_worst;
        }

        return ret;
    }
}
