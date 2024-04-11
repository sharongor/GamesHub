package il.example.weatherapp.ui.HomeScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import il.example.weatherapp.R
import il.example.weatherapp.data.models.new_api.HourForecastItem
import il.example.weatherapp.databinding.HourDailyBinding

class HomeHourAdapter(private val list:List<HourForecastItem>,
                      private val context: Context)
    : RecyclerView.Adapter<HomeHourAdapter.HoursViewHolder>() {

    inner class HoursViewHolder(private val hourBinding: HourDailyBinding)
        : RecyclerView.ViewHolder(hourBinding.root){

        fun bind(hourItem: HourForecastItem){
            hourBinding.hourTime.text = hourItem.time.split(" ")[1]
            Glide.with(context).load(context.getString(R.string.https)+hourItem.condition.icon).into(hourBinding.imageWeatherHour)
            hourBinding.currentHourTemp.text = hourItem.temp_c.toInt().toString()+"°"
            val percentPercipmm = hourItem.precip_mm
            if(percentPercipmm > 100){
                hourBinding.precipMm.text = percentPercipmm.toString()+context.getString(R.string.mm)
            }
            else {
                hourBinding.precipMm.text = percentPercipmm.toString()+context.getString(R.string.mm)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
        val binding = HourDailyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
