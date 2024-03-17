package com.example.weatherapp.setting.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.home.view.HomeFragmentDirections
import java.util.Locale

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationSharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        locationSharedPreferences = requireActivity().getSharedPreferences(
            Constants.LOCATION_SHARED_PREFERENCE,
            Context.MODE_PRIVATE)

        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.supportActionBar?.show()

        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val languageRadioButton: RadioButton = binding.root.findViewById(checkedId) as RadioButton
            when (languageRadioButton.text) {

                getString(R.string.arabic) ->{
                    sharedPreferences.edit()
                    .putString(
                        Constants.LANGUAGE_KEY,
                        Constants.Enum_lANGUAGE.ar.toString()
                    )
                    .apply()
                    changeLanguageLocaleTo("ar")
                }


                getString(R.string.english) ->

                {
                    sharedPreferences.edit()
                        .putString(Constants.LANGUAGE_KEY, Constants.Enum_lANGUAGE.en.toString())
                        .apply()
                    changeLanguageLocaleTo("en")
                }
            }
        }
        binding.temperatureRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val temperatureRadioButton: RadioButton = binding.root.findViewById(checkedId) as RadioButton
            when (temperatureRadioButton.text) {
                getString(R.string.celsius) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constants.UNITS_KEY,
                            Constants.ENUM_UNITS.metric.toString()
                        )
                        .apply()
                }
                getString(R.string.kelvin) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constants.UNITS_KEY,
                            Constants.ENUM_UNITS.standard.toString()
                        )
                        .apply()
                }
                getString(R.string.fahrenheit) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constants.UNITS_KEY,
                            Constants.ENUM_UNITS.imperial.toString()
                        )
                        .apply()
                }
            }
        }

        binding.locationRadioGroup.setOnCheckedChangeListener{group, checkedId ->
            val locationRadioGroup: RadioButton = binding.root.findViewById(checkedId) as RadioButton
            when (locationRadioGroup.text) {

                getString(R.string.gps) -> {
                    locationSharedPreferences.edit()
                        .putString(
                            Constants.LOCATION_KEY,
                            Constants.ENUM_LOCATION.gps.toString()
                        ).apply()
                }

                getString(R.string.map) -> {
                    locationSharedPreferences.edit()
                        .putString(
                            Constants.LOCATION_KEY,
                            Constants.ENUM_LOCATION.map.toString()
                        ).apply()
                    var tye = "Setting"
                    var action : SettingFragmentDirections.ActionSettingFragmentToMapFragment =
                        SettingFragmentDirections.actionSettingFragmentToMapFragment().apply {
                            type = tye
                        }
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }
        }
    }


private fun changeLanguageLocaleTo(languageTag: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageTag)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

private fun setupUI() {
    var lang = sharedPreferences.getString(
        Constants.LANGUAGE_KEY,
        Constants.Enum_lANGUAGE.en.toString()
    )

    var unit = sharedPreferences.getString(
        Constants.UNITS_KEY,
        Constants.ENUM_UNITS.metric.toString()
    )

    var location = locationSharedPreferences.getString(
        Constants.LOCATION_KEY,
        Constants.ENUM_LOCATION.gps.toString()
    )
    Log.i("SettingFragment", "Location method selected: $location")

    if (lang == Constants.Enum_lANGUAGE.en.toString()) {
        binding.languageRadioGroup.check(binding.radioButton2Language.id)
    } else {
        binding.languageRadioGroup.check(binding.radioButton1Language.id)
    }
    //==================

    if (unit == Constants.ENUM_UNITS.standard.toString()){
        binding.temperatureRadioGroup.check(binding.radioButton3Temperature.id)}

    else if (unit == Constants.ENUM_UNITS.imperial.toString()){
    binding.temperatureRadioGroup.check(binding.radioButton2Temperature.id)}

    else{
        binding.temperatureRadioGroup.check(binding.radioButton1Temperature.id)}
    //-----------------------

    if (location == Constants.ENUM_LOCATION.gps.toString()) {
        binding.locationRadioGroup.check(binding.radioButton1Location.id)
    } else {
        binding.locationRadioGroup.check(binding.radioButton2Location.id)
    }


}
}

