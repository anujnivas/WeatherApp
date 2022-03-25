package com.anujnivas.openweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Daily_Forecast_Adapter extends RecyclerView.Adapter<DailyForecastViewHolder> {
    private final List<DailyWeather> dailyWeatherArrayList;
    private  final Daily_Forecast mainActivity;
    Daily_Forecast_Adapter(List<DailyWeather> dailyWeatherArrayList,Daily_Forecast mainActivity){
        this.dailyWeatherArrayList=dailyWeatherArrayList;
        this.mainActivity=mainActivity;
    }
    @NonNull
    @Override
    public DailyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_weather_entry,parent,false);

        return new DailyForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyForecastViewHolder holder, int position) {
        String unit="\u00B0F";
        switch (Weather.unit){
            case 0:
                unit = "\u00B0F";
                break;
            case 1:
                unit = "\u00B0C";
        }

        SimpleDateFormat title_time= new SimpleDateFormat("EEEE, M/dd");
        holder.daily_date_tv.setText("   "+title_time.format(new Date(dailyWeatherArrayList.get(position).getDate() * 1000L)));


        String temp=dailyWeatherArrayList.get(position).getMax_temperature()+unit+"/"+dailyWeatherArrayList.get(position).getMin_temperature()+unit;
//        holder.daily_date_tv.setText("A");
        holder.daily_temperature_tv.setText(temp);
        holder.daily_weather_icon.setImageResource(dailyWeatherArrayList.get(position).getIcon());
        holder.daily_temperature_desc_tv.setText(dailyWeatherArrayList.get(position).getTemperature_desc());
        int percentage=(int)  (Double.parseDouble(dailyWeatherArrayList.get(position).getPrecip())*100);
        holder.daily_percip_tv.setText("("+percentage+"% precip.)");
        holder.daily_uv_index_tv.setText("UV Index: "+(int)Double.parseDouble(dailyWeatherArrayList.get(position).getUv_index()));
        holder.morning_tv.setText(dailyWeatherArrayList.get(position).getMorning_temperature()+unit);
        holder.afternoon_tv.setText(dailyWeatherArrayList.get(position).getAfternoon_temperature()+unit);
        holder.evening_tv.setText(dailyWeatherArrayList.get(position).getEvening_temperature()+unit);
        holder.night_tv.setText(dailyWeatherArrayList.get(position).getNight_temperature()+unit);
    }

    @Override
    public int getItemCount() {
        return dailyWeatherArrayList.size();
    }
}
