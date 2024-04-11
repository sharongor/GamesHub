package il.example.weatherapp.ui.Favourites

import il.example.weatherapp.data.models.LocalModels.Favourites

data class WeatherData(val favourites: List<Favourites>, val celsiusList: Map<String, String>)
