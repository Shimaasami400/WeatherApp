package com.example.weatherapp.setting.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingBinding
import java.util.Locale

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedPreferences: SharedPreferences

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

    if (lang == Constants.Enum_lANGUAGE.en.toString()) {
        binding.languageRadioGroup.check(binding.radioButton2Language.id)
    } else {
        binding.languageRadioGroup.check(binding.radioButton1Language.id)
    }

    if (unit == Constants.ENUM_UNITS.standard.toString()){
        binding.temperatureRadioGroup.check(binding.radioButton3Temperature.id)}

    else if (unit == Constants.ENUM_UNITS.imperial.toString()){
    binding.temperatureRadioGroup.check(binding.radioButton2Temperature.id)}

    else{
        binding.temperatureRadioGroup.check(binding.radioButton1Temperature.id)}
}
}

