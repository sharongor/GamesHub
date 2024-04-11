package il.example.weatherapp.ui.Favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import il.example.weatherapp.data.Repository.GeoCoderRepository
import il.example.weatherapp.data.Repository.WeatherRepository
import il.example.weatherapp.data.models.LocalModels.Favourites
import il.example.weatherapp.utils.Error
import il.example.weatherapp.utils.Resource
import il.example.weatherapp.utils.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val geoCoderRepository: GeoCoderRepository
) : ViewModel() {


    val favourites :LiveData<List<Favourites>> = weatherRepository.getAllFavourites()


    val transformationFavourites = favourites.switchMap { list ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO){
            emit(Resource.loading())
            val celiusList = mutableMapOf<String,String>()
            for (city in list) {
                val geoResult = geoCoderRepository.cityToLongLat(city.city)
                if (geoResult.status is Success) {
                    val concatenatedLatLong =
                        "${geoResult.status.data!!.lat},${geoResult.status.data!!.long}"
                    val resultBody = weatherRepository.fetchWeather(concatenatedLatLong)
                    if (resultBody.status is Success) {
                        celiusList.put(city.city, resultBody.status.data!!.current.temp_c.toInt().toString() +":"+resultBody.status.data!!.location.localtime.split(" ")[1])
                    } else if(resultBody.status is Error) {
                        emit(Resource.error(resultBody.status.message))
                        return@liveData
                    }
                } else if(geoResult.status is Error) {
                    emit(Resource.error(geoResult.status.message))
                    return@liveData
                }
            }
            val weatherData = WeatherData(list, celiusList)
            emit(Resource.success(weatherData))
        }
    }


    fun deleteFavourite(favourite:Favourites) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteFavourite(favourite)
        }
    }


    fun insertCity(city:Favourites){
        viewModelScope.launch(Dispatchers.IO) {
            val cityCapitalize = city.city.capitalize().trim()
            val capitalizedCity = city.copy(city = cityCapitalize)
            weatherRepository.insertCity(capitalizedCity)
        }
    }
}