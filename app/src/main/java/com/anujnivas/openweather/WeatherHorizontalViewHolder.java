package com.anujnivas.openweather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class WeatherHorizontalViewHolder extends RecyclerView.ViewHolder {
    TextView temp;
    TextView temp_desc;
    TextView time;
    ImageView icon;
    TextView day_tv_hourly;
    WeatherHorizontalViewHolder(View view){
        super(view);
        this.day_tv_hourly=view.findViewById(R.id.day_tv_hourly);
        this.temp=view.findViewById(R.id.temperature_tv_hourly);
        this.temp_desc=view.findViewById(R.id.temperature_desc_tv_hourly);
        this.time=view.findViewById(R.id.time_tv_hourly);
        this.icon=view.findViewById(R.id.hourly_icon);
    }
}
