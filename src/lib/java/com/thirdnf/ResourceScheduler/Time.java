package com.thirdnf.ResourceScheduler;

/**
 * Created by IntelliJ IDEA.
 * User: jgerth
 * Date: 1/13/11
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Time implements Comparable<Time>
{
    private final int _hours;
    private final int _minutes;
    private final int _seconds;

    public Time(int hours, int minutes, int seconds)
    {
        // Normalize
        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        }

        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }

        _hours = hours;
        _minutes = minutes;
        _seconds = seconds;
    }


    public int getHours()
    {
        return _hours;
    }

    public int getMinutes()
    {
        return _minutes;
    }

    public int getSeconds()
    {
        return _seconds;
    }


    public boolean isOnTheHour()
    {
        return _minutes == 0 && _seconds == 0;
    }


    public int compareTo(Time o)
    {
        if (o._hours > _hours) { return 1; }
        if (o._hours < _hours) { return -1; }

        if (o._minutes > _minutes) { return 1; }
        if (o._minutes < _minutes) { return -1; }

        if (o._seconds > _seconds) { return 1; }
        if (o._seconds < _seconds) { return -1; }

        return 0;
    }


    public int hashCode()
    {
        return (_hours*60 + _minutes)*60 + _seconds;
    }


    public boolean equals(Object o)
    {
        if (! (o instanceof Time)) { return false; }

        return compareTo((Time)o) == 0;
    }


    public Time addSeconds(int seconds)
    {
        return new Time(_hours, _minutes, _seconds + seconds);
    }


    public Time addMinutes(int minutes)
    {
        return new Time(_hours, _minutes + minutes, _seconds);
    }

    public Time addHours(int hours)
    {
        return new Time(_hours + hours, _minutes, _seconds);
    }

    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean pm = false;
        if (_hours > 12) {
            stringBuilder.append(_hours - 12);
            pm = true;
        }
        else {
            stringBuilder.append(_hours);
        }
        stringBuilder.append(':');

        stringBuilder.append(_minutes);
        if (_minutes < 10) { stringBuilder.append('0'); }

        if (pm) {
            stringBuilder.append(" PM");
        }
        else {
            stringBuilder.append(" AM");
        }

        return stringBuilder.toString();
    }
}
