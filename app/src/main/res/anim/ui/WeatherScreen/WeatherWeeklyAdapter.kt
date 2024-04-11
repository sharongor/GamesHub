package il.example.weatherapp.ui.WeatherScreen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.example.weatherapp.R
import il.example.weatherapp.data.models.new_api.forecastDays
import il.example.weatherapp.data.models.new_api.forecastday
import il.example.weatherapp.databinding.WeeklyForecastLayoutBinding

class WeatherWeeklyAdapter(private val list: List<forecastday>,private val context:Context) : RecyclerView.Adapter<WeatherWeeklyAdapter.WeeklyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyViewHolder {
        val binding = WeeklyForecastLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WeeklyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class WeeklyViewHolder(private val binding: WeeklyForecastLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:forecastday){
            val currentDate = item.date.split("-")
            val currentUpdated = currentDate[2]+"."+currentDate[1]
            binding.weeklyDay.text = currentUpdated
            Glide.with(context).load(context.getString(R.string.https)+item.day.condition.icon).into(binding.weeklyImage)
            Glide.with(context).load(R.drawable.rain_drop).into(binding.weeklyIconPercip)
            binding.weeklyPercip.text = String.format(context.getString(R.string._1f),item.day.totalprecip_mm)+context.getString(R.string.mm)
            binding.weeklyMaximumTemperature.text = item.day.maxtemp_c.toInt().toString()+"°"
            binding.weeklyMinimumTemperature.text = item.day.mintemp_c.toInt().toString()+"°"
        }
    }
}