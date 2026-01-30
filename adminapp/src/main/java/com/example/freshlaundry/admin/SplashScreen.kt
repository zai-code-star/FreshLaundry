package com.example.freshlaundry.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import androidx.appcompat.app.AppCompatDelegate

class SplashScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000) // 3 detik delay splash screen

            // Cek apakah user sudah login
            val user = auth.currentUser
            if (user != null) {
                // Sudah login -> ke MainActivity
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            } else {
                // Belum login -> ke LoginActivity
                startActivity(Intent(this@SplashScreen, LoginAdmin::class.java))
            }
            finish()
        }
    }
}
