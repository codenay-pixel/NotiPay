package com.example.notipay

import android.content.Context
import android.content.SharedPreferences

class VoicePreferenceManager(context: Context) {

    // Inisialisasi SharedPreferences
    private val prefs: SharedPreferences = context.getSharedPreferences("NotiPayPrefs", Context.MODE_PRIVATE)

    // Fungsi untuk mengecek apakah suara aktif (default: true)
    fun isVoiceEnabled(): Boolean {
        return prefs.getBoolean("isVoiceEnabled", true)
    }

    // Fungsi untuk mengubah status suara
    fun setVoiceEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean("isVoiceEnabled", isEnabled).apply()
    }
}