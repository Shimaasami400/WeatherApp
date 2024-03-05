package com.example.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
    var mSharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        Handler().postDelayed({
            mSharedPreferences = getSharedPreferences("mySp", MODE_PRIVATE)
            val isFirstTime = mSharedPreferences.getBoolean("firstTimeToOpen", true)
            val isAuthenticated = false
            if (isFirstTime) {
                val editor = mSharedPreferences.edit()
                editor.putBoolean("firstTime", false)
                editor.apply()
                val intent: Intent = Intent(
                    this@Splash,
                    OnBoardingActivity::class.java
                )
                //Intent intent = new Intent(Splash.this,Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }  else {
                val intent: Intent = Intent(this@Splash, Register::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }, 4000)
    }
}*/

    private lateinit var binding: ActivitySplashBinding
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Handler().postDelayed({
            mSharedPreferences = getSharedPreferences("mySp", MODE_PRIVATE)
            val isFirstTime = mSharedPreferences.getBoolean("firstTimeToOpen", true)


                val editor = mSharedPreferences.edit()
                editor.putBoolean("firstTimeToOpen", false)
                editor.apply()
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

        }, 4000)
    }
}