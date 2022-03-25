package com.anujnivas.openweather;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Weather implements Serializable {
    private String temperature;
    private String temperature_desc;
    private String time;
    private int icon;
    private String timezone;
    protected static int unit=0;
    Weather(String temperature,String temperature_desc, String time,int icon,String timezone){
        this.temperature=temperature;
        this.temperature_desc=temperature_desc;
        this.time=time;
        this.icon=icon;
        this.timezone=timezone;
    }


    public String getTemperature() { return temperature.split(Pattern.quote("."))[0]; }

    public String getTemperature_desc() {
        return temperature_desc;
    }

    public String getTime() {
        return time;
    }

    public int getIcon(){
        return icon;
    }

    public String getTimeZone(){
        return timezone;
    }
}
