package il.example.weatherapp.ui.ViewModel


import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val geoCoderRepository: GeoCoderRepository
) : ViewModel() {

    //using transformation to fetch data only if the city variable is changed
    //in that way we obtain the chance to fetch data only of the data of the weather has changed and not in everytime we are gonna get to that screen
    val city2 = MutableLiveData<String>()
    val transformationWeather = city2.switchMap { cityName ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.loading())
            val geoResult = geoCoderRepository.cityToLongLat(cityName)
            if (geoResult.status is Success) {
                val concated_lat_long = "${geoResult.status.data!!.lat},${geoResult.status.data!!.long}"
                val city = geoResult.status.data!!
                val toParse = EnglishCity(weatherRepository.fetchWeather(concated_lat_long),city)
                if(toParse.response.status is Success){
                    emit(Resource.success(toParse))
                }
                else if(toParse.response.status is Error){
                    emit(Resource.error(toParse.response.status.message))
                }
            }
            else if(geoResult.status is Error) {
                emit(Resource.error(geoResult.status.message))
            }
        }
    }

    //in our fragments that function will trigger the transformation above
    fun setCity(cityInput:String){
        if(city2.value!=cityInput){
            city2.value = cityInput
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
