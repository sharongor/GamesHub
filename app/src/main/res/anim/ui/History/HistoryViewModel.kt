package il.example.weatherapp.ui.History

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import il.example.weatherapp.data.Repository.GeoCoderRepository
import il.example.weatherapp.data.Repository.WeatherRepository
import il.example.weatherapp.data.models.LocalModels.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel(){


    val history: LiveData<List<History>> = weatherRepository.getAllHistory()


    fun deleteHistory(){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteHistory()
        }
    }

    fun deleteSpecificHistory(city: History){
        viewModelScope.launch(Dispatchers.IO){
            weatherRepository.deleteSpecificHistory(city)
        }
    }
}