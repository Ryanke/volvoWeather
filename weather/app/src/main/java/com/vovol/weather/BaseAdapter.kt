package com.vovol.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BaseAdapter(datas: List<WeatherInfo>) :
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder?>() {
    var datas: List<WeatherInfo>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.v_demo_view_page_item_text, parent, false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        var WeatherInfo: WeatherInfo = datas[position]
        var forecast: Forecast? = WeatherInfo.forecasts?.get(0)
        if (forecast != null) {
            var cast: Cast = forecast!!.casts.get(1)
            holder.date?.text = "日期：" + cast.date
            holder.week?.text = "星期" + cast.week
            holder.day_weather?.text = "白天" + cast.dayweather
            holder.night_weather?.text = "夜晚" + cast.nightweather
            holder.day_temp?.text = "白天温度" + cast.daytemp + "°C"
            holder.night_temp?.text = "夜晚温度" + cast.nighttemp + "°C"
            holder.day_wind?.text = "白天风向" + cast.daywind
            holder.day_power?.text = "风力" + cast.daypower
            holder.night_wind?.text = "夜晚风向" + cast.nightwind
            holder.night_power?.text = "风力" + cast.nightpower
        }

    }

    override fun getItemCount(): Int {
        return datas!!.size
    }

    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView?
        var week: TextView?
        var day_weather: TextView?
        var night_weather: TextView?
        var day_temp: TextView?
        var night_temp: TextView?
        var day_wind: TextView?
        var night_wind: TextView?
        var day_power: TextView?
        var night_power: TextView?

        init {
            date = itemView.findViewById(R.id.date)
            week = itemView.findViewById(R.id.week)
            day_weather = itemView.findViewById(R.id.day_weather)
            night_weather = itemView.findViewById(R.id.night_weather)
            day_temp = itemView.findViewById(R.id.day_temp)
            night_temp = itemView.findViewById(R.id.night_temp)
            day_wind = itemView.findViewById(R.id.day_wind)
            night_wind = itemView.findViewById(R.id.night_wind)
            day_power = itemView.findViewById(R.id.day_power)
            night_power = itemView.findViewById(R.id.night_power)
        }
    }

    init {
        this.datas = datas
    }
}