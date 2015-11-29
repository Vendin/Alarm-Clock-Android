package com.example.av.alarm_clock;

/**
 * Created by mikrut on 29.11.15.
 */
public class Alarm {
    private Integer id;
    private short hour;
    private short minute;
    private boolean enabled;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public short getHour() {
        return hour;
    }

    public void setHour(short hour) {
        if (hour >= 0 && hour <= 23) {
            this.hour = hour;
        } else {
            throw new IllegalArgumentException("Hour can be from 0 to 23");
        }
    }

    public short getMinute() {
        return minute;
    }

    public void setMinute(short minute) {
        if (minute >= 0 && minute <= 59) {
            this.minute = minute;
        } else {
            throw new IllegalArgumentException("Minute can be from 0 to 59");
        }
    }
}
