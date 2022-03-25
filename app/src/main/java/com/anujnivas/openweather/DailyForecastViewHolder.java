package com.anujnivas.openweather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DailyForecastViewHolder extends RecyclerView.ViewHolder {

    TextView daily_temperature_tv;
    TextView daily_temperature_desc_tv;
    TextView daily_uv_index_tv;
    TextView daily_percip_tv;
    TextView morning_tv;
    TextView afternoon_tv;
    TextView evening_tv;
    TextView night_tv;
    TextView daily_date_tv;
    ImageView daily_weather_icon;

    DailyForecastViewHolder(View view){
        super(view);
        this.morning_tv=view.findViewById(R.id.morning_tv);
        this.evening_tv=view.findViewById(R.id.evening_tv);
        this.night_tv=view.findViewById(R.id.night_tv);
        this.afternoon_tv=view.findViewById(R.id.afternoon_tv);
        this.daily_weather_icon=view.findViewById(R.id.daily_weather_icon);
        this.daily_uv_index_tv=view.findViewById(R.id.daily_uv_index_tv);
        this.daily_percip_tv=view.findViewById(R.id.daily_percip_tv);
        this.daily_temperature_desc_tv=view.findViewById(R.id.daily_temperature_desc_tv);
        this.daily_temperature_tv=view.findViewById(R.id.daily_temperature_tv);
        this.daily_date_tv=view.findViewById(R.id.daily_date_tv);


    }
}
