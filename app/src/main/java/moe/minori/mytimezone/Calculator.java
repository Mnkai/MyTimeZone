package moe.minori.mytimezone;

/**
 * Created by Minori on 2015-05-17.
 */
public class Calculator {
    public static final int GREENWICH = 0;
    public static final int offsetInSecondPerDegree = 240;

    public static double getTimeZoneInSecond (double longitude)
    {
        return offsetInSecondPerDegree * longitude;
    }

    public static long getMyTimezonedUnixTime(double longitude)
    {
        long millisTimeOffset = (long) (getTimeZoneInSecond(longitude) * 1000);

        return System.currentTimeMillis() + millisTimeOffset;
    }
}
