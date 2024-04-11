package il.example.weatherapp.ui.WeatherScreen

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import il.example.weatherapp.R
import il.example.weatherapp.data.models.LocalModels.Favourites
import il.example.weatherapp.databinding.WeatherScreenFragmentBinding
import il.example.weatherapp.ui.ViewModel.WeatherViewModel
import il.example.weatherapp.utils.Error
import il.example.weatherapp.utils.Loading
import il.example.weatherapp.utils.Success
import il.example.weatherapp.utils.autoCleared


@AndroidEntryPoint
class WeatherScreenFragment: Fragment() {

    private var binding : WeatherScreenFragmentBinding by autoCleared()

    private val viewModel : WeatherViewModel by viewModels()

    private var cityToDisplay=""

    private lateinit var longitude:String
    private lateinit var latitude:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WeatherScreenFragmentBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //from home screen
        arguments?.getString(getString(R.string.city))?.let {
            viewModel.setCity(it)
            cityToDisplay=it
        }


        //from history fragment
        arguments?.getString(getString(R.string.city_history))?.let {
            viewModel.setCity(it)
        }

        //from the favorites screen
        arguments?.getString(getString(R.string.fav_city))?.let {
            viewModel.setCity(it)
        }


        //if using transformation we will observe transformationWeather instead of weather
        //if not using transformation we will observe weather
        viewModel.transformationWeather.observe(viewLifecycleOwner){
            when(it.status){
                is Loading ->{
                    binding.progressBar.isVisible = true
                }
                is Success -> {

                    longitude=it.status.data!!.geoCoderResult.long.toString()
                    latitude= it.status.data!!.geoCoderResult.lat.toString()
                    binding.progressBar.isVisible = false
                    val dataCurrent = it.status.data!!.response.status.data!!.current
                    val forecast = it.status.data!!.response.status.data!!.forecast
                    val location = it.status.data!!.response.status.data!!.location

                    binding.hoursRv.adapter = WeatherHourAdapter(forecast.forecastday[0].hour,requireContext())
                    binding.hoursRv.layoutManager =LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

                    binding.currentDegrees.text = dataCurrent.temp_c.toInt().toString()+"°"
                    binding.currentCityName.text = it.status.data!!.geoCoderResult.city.split(",")[0]
                    cityToDisplay = it.status.data!!.geoCoderResult.city.split(",")[0]

                    when(dataCurrent.condition.code){
                        1000 -> { // 1000 can be both Clear or Sunny , an inside term is needed
                            if(dataCurrent.condition.text == getString(R.string.clear_untranslatable)){
                                binding.myLocationCondition.text = getString(R.string.current_weather) +": "+getString(R.string.clear)
                            }
                            else{
                                binding.myLocationCondition.text = getString(R.string.current_weather) +": "+getString(R.string.sunny)
                            }
                        }

                        1003 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) +": "+ getString(R.string.partly_cloudy)
                        }

                        1006 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) +": "+ getString(R.string.cloudy)
                        }
                        1009 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) +": "+ getString(R.string.overcast)
                        }
                        1030 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) +": "+ getString(R.string.mist)
                        }
                        1114 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.blowing_snow)

                        }
                        1117 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.blizzard)

                        }
                        1135 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.fog)

                        }
                        1147 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.freezing_fog)

                        }
                        1150 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_light_drizzle)

                        }
                        1153 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_drizzle)

                        }
                        1168 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.freezing_drizzle)

                        }
                        1171 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.heavy_freezing_drizzle)

                        }
                        1180 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_light_rain)

                        }
                        1183 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_rain)

                        }
                        1186 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_rain_at_times)

                        }
                        1189 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_rain)

                        }
                        1192 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.heavy_rain_at_times)

                        }
                        1195 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.heavy_rain)

                        }
                        1198 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_freezing_rain)

                        }
                        1201 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_or_heavy_freezing_rain)

                        }
                        1204 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_sleet)

                        }
                        1207 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_or_heavy_sleet)

                        }
                        1210 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_light_snow)
                        }

                        1213 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_snow)
                        }

                        1216 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_moderate_snow)
                        }

                        1219 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_snow)
                        }

                        1222 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_heavy_snow)
                        }

                        1225 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.heavy_snow)
                        }

                        1237 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.ice_pellets)
                        }

                        1240 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_rain_shower)
                        }

                        1243 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " +getString(R.string.moderate_or_heavy_rain_shower)
                        }
                        1246 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " +getString(R.string.torrential_rain_shower)

                        }
                        1249 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " +getString(R.string.light_sleet_showers)

                        }
                        1252 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " +getString(R.string.moderate_or_heavy_sleet_showers)

                        }
                        1255 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " +getString(R.string.light_snow_showers)

                        }
                        1258 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " +getString(R.string.moderate_or_heavy_snow_showers)

                        }
                        1261 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.light_showers_of_ice_pellets)
                        }

                        1264 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_or_heavy_showers_of_ice_pelltes)
                        }

                        1273 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_light_rain_with_thunder)
                        }

                        1276 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_or_heavy_rain_with_thunder)
                        }

                        1279 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_light_snow_with_thunder)
                        }

                        1282 -> {
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.moderate_or_heavy_snow_with_thunder)
                        }
                        1063->{
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_rain_nearby)
                        }
                        1066->{
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_snow_nearby)
                        }
                        1069->{
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_sleet_nearby)
                        }
                        1072->{
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.patchy_freezing_drizzle_nearby)
                        }
                        1087->{
                            binding.myLocationCondition.text = getString(R.string.current_weather) + ": " + getString(R.string.thundery_outbreaks_in_nearby)
                        }
                    }
                    val max_deg = forecast.forecastday[0].day.maxtemp_c.toInt().toString()
                    val min_deg = forecast.forecastday[0].day.mintemp_c.toInt().toString()
                    binding.maxTempMinTempFeelsLike.text = max_deg+"°"+" / " + min_deg+"° " + getString(R.string.feels_like)+" " + dataCurrent.feelslike_c.toInt().toString()+"°"

                    //Progress bar and sunrise + sunset connected to the bar
                    val sunrise = setToInt(forecast.forecastday[0].astro.sunrise)
                    val sunset =  setToInt(forecast.forecastday[0].astro.sunset)
                    val currentTime = setCurrentToInt(location.localtime.split(" ")[1])
                    val maxTime = sunset - sunrise
                    val progressBar = currentTime - sunrise
                    binding.progressBarrr.max=maxTime
                    binding.progressBarrr.progress = progressBar
                    binding.sunriseTime.text=forecast.forecastday[0].astro.sunrise.split(" ")[0] +" " +  getString(R.string.PM)
                    binding.sunsetTime.text = forecast.forecastday[0].astro.sunset.split(" ")[0] +" " +  getString(R.string.AM)


                    //weekly adapter forecast
                    binding.weeklyRv.adapter = WeatherWeeklyAdapter(forecast.forecastday,requireContext())
                    binding.weeklyRv.layoutManager = LinearLayoutManager(requireContext())


                    //Wind + Humidity
                    binding.currentWind.text = dataCurrent.wind_kph.toInt().toString()+" " + getString(R.string.km_h)
                    binding.currentHumidity.text = dataCurrent.humidity.toInt().toString() + "%"
                    Glide.with(requireContext()).load(R.drawable.baseline_air_24).into(binding.windCurrentIcon)
                    Glide.with(requireContext()).load(R.drawable.baseline_water_drop_24).into(binding.humidityCurrentIcon)


                    //Moon card
                    when(forecast.forecastday[0].astro.moonrise.split(" ")[1]){
                        getString(R.string.am) -> {
                            binding.moonriseWeather.text = forecast.forecastday[0].astro.moonrise.split(" ")[0] + " " + getString(R.string.AM)
                        }
                        getString(R.string.pm) -> {
                            binding.moonriseWeather.text = forecast.forecastday[0].astro.moonrise.split(" ")[0] + " " + getString(R.string.PM)
                        }
                    }

                    when(forecast.forecastday[0].astro.moonset.split(" ")[1]){
                        getString(R.string.am) -> {
                            binding.moonsetWeather.text = forecast.forecastday[0].astro.moonset.split(" ")[0] + " " + getString(R.string.AM)
                        }
                        getString(R.string.pm) -> {
                            binding.moonsetWeather.text = forecast.forecastday[0].astro.moonset.split(" ")[0] + " " + getString(R.string.PM)
                        }
                    }
                    binding.moonPhaseWeather.text = forecast.forecastday[0].astro.moon_phase



                    //Background changes based on current time
                    val hoursResponse = location.localtime.split(" ")[1].split(":")[0].toInt()

                    if((hoursResponse in 7..15) && (dataCurrent.condition.text.contains(getString(R.string.cloudy_untranslatable),true)
                                ||dataCurrent.condition.text.contains(getString(R.string.fog_untranslatable),false))){
                        binding.root.setBackgroundResource(R.drawable.cloudy_day)
                    }
                    else if (hoursResponse in 7..15){
                        binding.root.setBackgroundResource(R.drawable.morning_colors)
                    }
                    else if(hoursResponse in 16..17){
                        binding.root.setBackgroundResource(R.drawable.afternoon_colors)
                    }
                    else {
                        binding.root.setBackgroundResource(R.drawable.night_colors)
                    }
                }

                is Error-> {
                    binding.progressBar.isVisible = false
                    when(it.status.message){
                        3 -> {
                            Toast.makeText(requireContext(),getString(R.string.no_such_city),Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            Toast.makeText(requireContext(),getString(R.string.network_call_has_failed),Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            Toast.makeText(requireContext(),getString(R.string.exception_caught),Toast.LENGTH_SHORT).show()
                        }
                    }
                    findNavController().navigate(R.id.action_weatherScreenFragment_to_homeScreenFragment)
                }
            }
        }

        binding.btnShowMap.setOnClickListener {
            val location = "$latitude,$longitude"
            openMapsWithLocation(location)
        }
    }

    // this functions are casting a time instance into a functional number

    private fun setToInt(time : String): Int{
        var tmp = time.take(5)
        var hours = time.take(2)
        var minutes = tmp.takeLast(2)

        if( hours.take(1).toInt() == 0 ){
            hours = hours.takeLast(1)
        }
        if( minutes.take(1).toInt() == 0 ){
            minutes = minutes.takeLast(1)
        }

        return if(getString(R.string.pm_pm) in time || getString(R.string.PM) in time){
            60*(hours.toInt() + 12 ) + minutes.toInt()
        }else{
            60*(hours.toInt()  ) + minutes.toInt()
        }
    }

    private fun setCurrentToInt(time : String): Int{
        if(time.length==4){
            val tmp = time.take(4)
            val hours = time.take(1)
            var minutes = tmp.takeLast(2)

            if( minutes.take(1).toInt() == 0 ){
                minutes = minutes.takeLast(1)
            }
            return 60*(hours.toInt()) + minutes.toInt()
        }
        else {
            val tmp = time.take(5)
            val hours = time.take(2)
            var minutes = tmp.takeLast(2)

            if( minutes.take(1).toInt() == 0 ){
                minutes = minutes.takeLast(1)
            }
            return 60*(hours.toInt()) + minutes.toInt()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        val favouriteItem = menu.findItem(R.id.favourite_icon)
        val homeItem = menu.findItem(R.id.home_btn_icon)
        val historyItem = menu.findItem(R.id.history_icon)

        val colorControlNormal = ContextCompat.getColor(requireContext(), R.color.white)

        // Set tint for the icons
        favouriteItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        homeItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        historyItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.favourite_icon){
            val options = arrayOf(getString(R.string.add_city_to_favorites) ,
                getString(R.string.go_to_favorites_screen))

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.choose_an_option))
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        viewModel.insertCity(Favourites(cityToDisplay))
                        findNavController().navigate(R.id.action_weatherScreenFragment_to_favouritesFragment)
                    }
                    1 -> {
                        findNavController().navigate(R.id.action_weatherScreenFragment_to_favouritesFragment)
                    }
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        else if (item.itemId == R.id.home_btn_icon){
            findNavController().navigate(R.id.action_weatherScreenFragment_to_homeScreenFragment)
        }
        else if(item.itemId== R.id.history_icon){
            findNavController().navigate(R.id.action_weatherScreenFragment_to_historyFragment)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun openMapsWithLocation(location: String) {
        val mapIntentUri = Uri.parse(getString(R.string.geo_0_0_q, location))
        val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
        mapIntent.setPackage(getString(R.string.google_maps)) // Specify the package name of Google Maps app to ensure it's used
        if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Google Maps app is not installed, handle accordingly (open web browser, show error message, etc.)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.google_play_google_maps))
            )
            startActivity(intent)
        }
    }
}

