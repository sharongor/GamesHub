package il.example.weatherapp.ui.HomePermissions

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import il.example.weatherapp.R
import il.example.weatherapp.ui.HomeScreen.HomeScreenViewModel
import il.example.weatherapp.utils.Error
import il.example.weatherapp.utils.Loading
import il.example.weatherapp.utils.Success
import il.example.weatherapp.utils.autoCleared
import il.example.weatherapp.databinding.HomePermissionsLayoutBinding

class HomePermissionsFragment : Fragment() {

    private var binding : HomePermissionsLayoutBinding by autoCleared()
    private lateinit var sharedPreferences: SharedPreferences

    private val locationAllowAllLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (fineLocationGranted || coarseLocationGranted) {
            binding.locationRadio.isChecked = true
        } else {
            // Check if both permissions were denied twice and "Don't ask again" was checked
            val showFineLocationRationale = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            val showCoarseLocationRationale = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (showFineLocationRationale || showCoarseLocationRationale) {
                showLocationSettingsDialog()
            }
        }
    }


    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (fineLocationGranted || coarseLocationGranted) {
            binding.locationRadio.isChecked = true
        } else {
            // Check if both permissions were denied twice and "Don't ask again" was checked
            val showFineLocationRationale = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            val showCoarseLocationRationale = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (showFineLocationRationale || showCoarseLocationRationale) {
                showLocationSettingsDialog()
            }
        }
    }

    private fun showLocationSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.home_rationale_title))
            .setMessage(getString(R.string.show_weather_at_ypur_location))
            .setPositiveButton(getString(R.string.home_rationale_settings)) { dialog, _ ->
                // Open app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(getString(R.string.package1), requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                // Handle cancellation
                dialog.dismiss()
            }
            .show()
    }

    private val notificationAllowAllLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            locationAllowAllLauncher.launch(arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            // If user denies twice and checks "Don't ask again"
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showNotificationsDialog()
            }
            locationAllowAllLauncher.launch(arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private val postNotificationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showNotificationsDialog()
            }
        }
    }

    private fun showNotificationsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.notifications_permission_required))
            .setMessage(getString(R.string.feature_requires_permission))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
                startActivity(intent)
                dialog.dismiss()

            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                // Handle cancellation
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()

        //the user returned from the settings and turned manually any of them ,the the radio buttons will show that they've been enabled
        if ((ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)) {
            binding.locationRadio.isChecked = true
        }
        //if the user returned from the settings and turned the notifications on then check the radio to show that it has been enabled
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            binding.permissionsRadio.isChecked = true
        }
        //if the user first granted permissions for location and then disabled it from the settings
        if(binding.locationRadio.isChecked && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED)){
            findNavController().navigate(R.id.action_homePermissionsFragment_to_introductFragment)
        }
        //if the user firstly enabled the notifications and then disable it from the settings move to the first screen
        if(binding.permissionsRadio.isChecked && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=PackageManager.PERMISSION_GRANTED )){
            findNavController().navigate(R.id.action_homePermissionsFragment_to_introductFragment)
        }

        //if both of the permissions granted simply navigate to the screen to show current location weather
        if(((ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)  ==
                    PackageManager.PERMISSION_GRANTED) ||
                    ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)) {
            findNavController().navigate(R.id.action_homePermissionsFragment_to_homeScreenFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomePermissionsLayoutBinding.inflate(inflater,container,false)
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_launched), Context.MODE_PRIVATE)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if the app just installed we will get here from the homeScreenFragment, then the shared preference firstly empty so we will move to introduction
        //just before we move to introduction we change the value of the sharedPreference to true so we won't see the introduction screen on the second time
        if(sharedPreferences.getString(getString(R.string.launched_true),"").equals("")){
            sharedPreferences.edit().putString(getString(R.string.launched_true),getString(R.string.true_untranslatable) ).apply()
            findNavController().navigate(R.id.action_homePermissionsFragment_to_introductFragment)
        }

        //if permissions was granted just navigate quickly to the home screen
        if((ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)  ==
                    PackageManager.PERMISSION_GRANTED) ||
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            findNavController().navigate(R.id.action_homePermissionsFragment_to_homeScreenFragment)
        }
        //permissions aren't granted yet
        else {
            //location permissions
            binding.cardLocationPerm.setOnClickListener {
                locationPermissionLauncher.launch(arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            }

            //notifications permissions
            binding.cardNotificationPerm.setOnClickListener {
                postNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }


            //grant all the permissions needed
            binding.btnAllowAll.setOnClickListener {
                notificationAllowAllLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

            }
            //didn't give permissions at all
            binding.btnSkip.setOnClickListener {
                findNavController().navigate(R.id.action_homePermissionsFragment_to_homeScreenFragment)
            }
        }
    }
}
