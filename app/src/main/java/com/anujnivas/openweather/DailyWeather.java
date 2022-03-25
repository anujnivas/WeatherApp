package com.anujnivas.openweather;

import java.util.regex.Pattern;

public class DailyWeather extends Weather{
    private String morning_temperature;
    private String afternoon_temperature;
    private String evening_temperature;
    private String night_temperature;
    private String uv_index;
    private String precip;
    private String min_temperature;
    private String max_temperature;
    private String date;

    public String getAfternoon_temperature() {
        return  afternoon_temperature.split(Pattern.quote("."))[0];

    }

    public void setAfternoon_temperature(String afternoon_temperature) {
        this.afternoon_temperature = afternoon_temperature;
    }

    public String getEvening_temperature() {
        return  evening_temperature.split(Pattern.quote("."))[0];

    }

    public void setEvening_temperature(String evening_temperature) {
        this.evening_temperature = evening_temperature;
    }

    public String getNight_temperature() {

        return  night_temperature.split(Pattern.quote("."))[0];
    }

    public void setNight_temperature(String night_temperature) {
        this.night_temperature = night_temperature;
    }

    public String getUv_index() {
        return uv_index;
    }

    public void setUv_index(String uv_index) {
        this.uv_index = uv_index;
    }

    public String getPrecip() {
        return precip;
    }

    public void setPrecip(String precip) {
        this.precip = precip;
    }

    public String getMin_temperature() {
        return  min_temperature.split(Pattern.quote("."))[0];
    }

    public void setMin_temperature(String min_temperature) {
        this.min_temperature = min_temperature;
    }

    public String getMax_temperature() {
        return  max_temperature.split(Pattern.quote("."))[0];
    }

    public void setMax_temperature(String max_temperature) {
        this.max_temperature = max_temperature;
    }

    public String getMorning_temperature() {
        return  morning_temperature.split(Pattern.quote("."))[0];
    }

    public void setMorning_temperature(String morning_temperature) {
        this.morning_temperature = morning_temperature;
    }

    public Long getDate() {
        return  Long.parseLong(date);
    }

    public void setDate(String date) {
        this.date = date;
    }



    DailyWeather(String temperature, String temperature_desc, String time, int icon,String timezone,String morning_temperature,String afternoon_temperature,
                 String evening_temperature, String night_temperature,String min_temperature,String max_temperature,String uv_index, String precip,String date) {
        super(temperature, temperature_desc, time, icon,timezone);
        this.afternoon_temperature=afternoon_temperature;
        this.evening_temperature=evening_temperature;
        this.morning_temperature=morning_temperature;
        this.night_temperature=night_temperature;
        this.min_temperature=min_temperature;
        this.max_temperature=max_temperature;
        this.uv_index=uv_index;
        this.precip=precip;
        this.date=date;

    }
}
