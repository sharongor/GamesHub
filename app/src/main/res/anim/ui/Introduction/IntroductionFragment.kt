package il.example.weatherapp.ui.Introduction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import il.example.weatherapp.R
import il.example.weatherapp.databinding.IntroductionLayoutBinding
import il.example.weatherapp.utils.autoCleared

class IntroductionFragment: Fragment() {

    private var binding : IntroductionLayoutBinding by autoCleared()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IntroductionLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextHomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introductFragment_to_homePermissionsFragment)
        }
    }
}