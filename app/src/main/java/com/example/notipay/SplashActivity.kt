package com.example.notipay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Menahan layar splash selama 2000 milidetik (2 detik)
        Handler(Looper.getMainLooper()).postDelayed({
            // Diubah agar mengarah ke LoginActivity terlebih dahulu
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}