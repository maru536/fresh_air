package team.perfect.fresh_air.Contract;

public class TimeContract {
    public static final int SECOND = 60;
    public static final int MINUTE = 60;
    public static final int HOUR = 24;

    public static final long A_SECOND = 1000;
    public static final long A_MINUTE = SECOND * A_SECOND;
    public static final long A_HOUR = MINUTE * A_MINUTE;
    public static final long A_DAY = HOUR * A_HOUR;
}