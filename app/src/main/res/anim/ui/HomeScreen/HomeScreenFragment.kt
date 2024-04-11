package il.example.weatherapp.ui.HomeScreen

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import il.example.weatherapp.R
import il.example.weatherapp.data.models.LocalModels.History
import il.example.weatherapp.databinding.HomeScreenFragmentBinding
import il.example.weatherapp.utils.Error
import il.example.weatherapp.utils.Loading
import il.example.weatherapp.utils.Success
import il.example.weatherapp.utils.autoCleared



@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    var city=""

    private var cityFromLatLong = ""

    private var binding :HomeScreenFragmentBinding by autoCleared()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel : HomeScreenViewModel by activityViewModels()

    private lateinit var sharedPreferences: SharedPreferences



    private val recognizeSpeechLauncher : ActivityResultLauncher<Intent>
            = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            var result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString()
            result = result.replace("[", "").replace("]", "").trim()
            binding.cityText.setText(result)
        }
        else {
            Toast.makeText(requireContext(),getString(R.string.please_repeat),Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.home_launched), Context.MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding =HomeScreenFragmentBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }


    //when moving to different screen reset the EditText
    override fun onPause() {
        super.onPause()
        binding.cityText.setText("")
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Cant schedule exact alarms in versions below TIRAMISU
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.TIRAMISU){
            binding.btnNotifictionsPerm.visibility = View.GONE
        }

        //if the user went to that screen it means that he was in the introduction screen -> permissions screen
        //then he chose Allow All and accepted everything or chose skip

        //when the app just installed :
        //firstly the sharedPreference will be empty, but as soon as its empty we replace that value to true and navigate to permissions screen
        //so on the second time when the user will lunch the app he will be on that screen even if he didn't grant permissions or he did
        //and on this screen if he doesn't grant permissions then there will be a button for him to grant permissions.
        if(sharedPreferences.getString(getString(R.string.launched_once),"").equals("")){
            sharedPreferences.edit().putString(getString(R.string.launched_once),
                getString(R.string.true_untranslatable) ).apply()
            findNavController().navigate(R.id.action_homeScreenFragment_to_homePermissionsFragment)
        }

        if ((ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)) {
            getLocation()
            binding.btnGrantPermissions.visibility = View.GONE
        }



        binding.btnGrantPermissions.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_homePermissionsFragment)
        }


        binding.btnShowMoreInfo.setOnClickListener {
            if(!cityFromLatLong.isNullOrEmpty()){
                findNavController().navigate(R.id.action_homeScreenFragment_to_weatherScreenFragment,
                    bundleOf(getString(R.string.city) to cityFromLatLong.substring(0,1).uppercase()+cityFromLatLong.substring(1).lowercase()))
            }
        }

        binding.btnNotifictionsPerm.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_dailyAlarm)
        }



        binding.btnVoiceRecognition.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, getString(R.string.iw))
                putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.slow_speech))
            }
            recognizeSpeechLauncher.launch(intent)
        }


        //observing for any changes on the weather Livedata item, which holds the data we fetched. when not using Transformation
        //when using transformation we will observe transformationWeather
        viewModel.transformationWeather.observe(viewLifecycleOwner){
            when(it.status){
                is Loading ->{
                    binding.progressBar.isVisible = true
                }
                is Success -> {
                    cityFromLatLong = it.status.data!!.geoCoderResult.city
                    binding.cardHomeCity.visibility = View.VISIBLE
                    binding.cardHomeTemp.visibility = View.VISIBLE
                    binding.cardHomeWeekly.visibility = View.VISIBLE
                    binding.cardHomeSomeDetails.visibility = View.VISIBLE
                    binding.btnShowMoreInfo.visibility = View.VISIBLE
                    binding.progressBar.isVisible = false

                    val dataCurrent = it.status.data!!.response.status.data!!.current
                    val forecast = it.status.data!!.response.status.data!!.forecast

                    binding.myLocationName.text = cityFromLatLong
                    binding.myLocationTemp.text = dataCurrent.temp_c.toInt().toString()+"°"
                    Glide.with(requireContext()).load(getString(R.string.https)+dataCurrent.condition.icon).into(binding.imageCurrentCondition)
                    when(dataCurrent.condition.code){// through the weather code we can choose the corresponding condition's texr
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
                    binding.hoursRv.adapter = HomeHourAdapter(forecast.forecastday[0].hour,requireContext())
                    binding.hoursRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                }
                // handling localization for error messages from classes that can't get the context within
                // the following code block :
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
                }
            }
        }

        //checking if location permission was granted if not launching the launcher which asks the user to grant permissions
        //saving the state of the permissions via the view model ho handle the screen rotation problem -> will ask every time for permissions

        //fetching the data by the city name and navigating to the second fragment

        binding.searchBtn.setOnClickListener {
            if(!city.isNullOrEmpty()){
                viewModel.insertToHistory(History(city))
                findNavController().navigate(R.id.action_homeScreenFragment_to_weatherScreenFragment, bundleOf("city" to city.substring(0,1).uppercase()+city.substring(1).lowercase()))
            }
            else {
                Toast.makeText(requireContext(),getString(R.string.enter_city_name),Toast.LENGTH_SHORT).show()
            }
        }


        binding.cityText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                city = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        binding.cityText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Handle of the "Done" action
                // For example, you can hide the keyboard
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                // Additionally, you can perform any other actions you want here
                if(!city.isNullOrEmpty()){
                    viewModel.insertToHistory(History(city))
                    findNavController().navigate(R.id.action_homeScreenFragment_to_weatherScreenFragment, bundleOf("city" to city.substring(0,1).uppercase()+city.substring(1).lowercase()))
                }
                else {
                    Toast.makeText(requireContext(),getString(R.string.enter_city_name),Toast.LENGTH_SHORT).show()
                }
                true // the event got consumed
            } else {
                false // the event was not handled
            }
        }

        //closing the keyboard when touching anywhere on the screen
        binding.homeParent.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_screen_menu,menu)

        val favouriteItem = menu.findItem(R.id.favourite_icon)
        val historyItem = menu.findItem(R.id.history_icon)

        val colorControlNormal = ContextCompat.getColor(requireContext(), R.color.white)

        // Set tint for the icons
        favouriteItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        historyItem.icon?.let { DrawableCompat.setTint(it, colorControlNormal) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.favourite_icon){

            findNavController().navigate(R.id.action_homeScreenFragment_to_favouritesFragment)
        }
        if(item.itemId == R.id.history_icon){
            findNavController().navigate(R.id.action_homeScreenFragment_to_historyFragment)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getLocation() {

        //need inorder to use lastLocation of the fusedLocationClient
        if ((ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //if permissions for location granted get the last location known and fetch the data according to those coordinates
            val lastLocation = fusedLocationClient.lastLocation

            lastLocation.addOnSuccessListener {
                if (it != null) {
                    viewModel.setCity("${it.latitude},${it.longitude}")
                }
            }

            lastLocation.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_cannot_be_traced),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}



