package com.perfect.freshair.Utils;

import com.perfect.freshair.Model.CurrentStatus;

import java.util.List;

public class MicroDustUtils {

    public static final String dust_good = "좋음";
    public static final String dust_normal = "보통";
    public static final String dust_bad = "나쁨";
    public static final String dust_worst = "매우 나쁨";


    static public String parseDustValue(int value)
    {
        String ret = dust_normal;
        //좋음`은 0~30㎍/㎥, `보통`은 31~80㎍/㎥, `나쁨`은 81~150㎍/㎥, `매우 나쁨`은 151㎍/㎥
        if(value <= 30)
        {
            ret = dust_good;
        }else if(value <= 80)
        {
            ret = dust_normal;
        }else if(value <= 150)
        {
            ret = dust_bad;
        }else{
            ret = dust_worst;
        }

        return ret;
    }

    static public String getCoach(int value)
    {
        String ret = dust_normal;
        //좋음`은 0~15㎍/㎥, `보통`은 16~35㎍/㎥, `나쁨`은 36~75㎍/㎥, `매우 나쁨`은 76㎍/㎥
        if(value <= 15)
        {
            ret = "좋은 공기에서 활동중이시군요. 안전합니다.";
        }else if(value <= 35)
        {
            ret = "평균적인 공기상태에 노출되어 있습니다. 안심하셔도 됩니다.";
        }else if(value <= 75)
        {
            ret = "나쁜 공기에 노출되어 있습니다. 주의가 필요합니다.";
        }else{
            ret = "최악의 공기에 노출되어 있습니다. 마스크를 꼭 착용하세요.";
        }

        return ret;
    }

    static public int getDustAverage(List<CurrentStatus> list)
    {
        int size = list.size();
        int sum = 0;
        for(int i =0; i<size;i++)
        {
            sum += list.get(i).getDust().getPm25();
        }

        int res = 0;

        try{
            res = (sum/size);
        }catch (Exception e)
        {
            ;
        }

        return res;
    }
}
