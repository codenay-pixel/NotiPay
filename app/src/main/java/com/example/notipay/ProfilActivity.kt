package com.example.notipay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val btnBackProfil = findViewById<TextView>(R.id.btnBackProfil)
        val tvNamaProfil = findViewById<TextView>(R.id.tvNamaProfil)
        val menuBeranda = findViewById<TextView>(R.id.menuBerandaProfil)
        val menuRiwayat = findViewById<TextView>(R.id.menuRiwayatProfil)

        // Ambil nama dari memori HP saat login tadi
        val prefs = getSharedPreferences("NotiPayPrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("nama_pengguna", "Pengguna UMKM")
        tvNamaProfil.text = savedName

        // Logika Navigasi
        btnBackProfil.setOnClickListener { finish() } // Kembali ke halaman sebelumnya

        menuBeranda.setOnClickListener {
            // Kembali ke Dashboard utama
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Menghapus tumpukan history
            startActivity(intent)
        }

        menuRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
            finish() // Tutup halaman profil agar tidak bertumpuk
        }
    }
}