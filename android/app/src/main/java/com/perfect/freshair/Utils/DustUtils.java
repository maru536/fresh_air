package com.perfect.freshair.Utils;

import com.perfect.freshair.DB.MeasurementDBHandler;
import com.perfect.freshair.Model.Measurement;

import java.sql.Timestamp;
import java.util.List;

public class DustUtils {
    public static int calcAvgDayDust(MeasurementDBHandler dbHandler) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp startTime = currentTime;
        Timestamp endTime = currentTime;

        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);

        endTime.setHours(23);
        endTime.setMinutes(59);
        endTime.setSeconds(59);

        List<Measurement> todayStatusList = dbHandler.search(startTime.getTime(), endTime.getTime());

        int sumDust = 0;

        for (Measurement status : todayStatusList)
            sumDust += status.getDust().getPm25();

        return sumDust / todayStatusList.size();
    }
}
