package il.example.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import il.example.weatherapp.R
import il.example.weatherapp.databinding.ActivityMainBinding
import il.example.weatherapp.utils.AppUtils

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppUtils.createNotificationChannel(this)
        AppUtils.cancelNotification(this)
    }
}