package il.example.weatherapp.ui.ViewModel

import il.example.weatherapp.data.models.coordinates.MyLatLong
import il.example.weatherapp.data.models.new_api.AllDataNew
import il.example.weatherapp.utils.Resource

data class EnglishCity(val response:Resource<AllDataNew>, val geoCoderResult:MyLatLong)
