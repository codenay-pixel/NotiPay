package com.example.notipay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etNamaPengguna = findViewById<EditText>(R.id.etNamaPengguna)
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)

        // Cek apakah sebelumnya sudah pernah login
        val prefs = getSharedPreferences("NotiPayPrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("nama_pengguna", "")

        // Jika sudah ada nama tersimpan, langsung lempar ke Dashboard
        if (savedName!!.isNotEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnMasuk.setOnClickListener {
            val nama = etNamaPengguna.text.toString().trim()
            if (nama.isNotEmpty()) {
                // Simpan nama pengguna ke memori HP
                prefs.edit().putString("nama_pengguna", nama).apply()

                // Pindah ke halaman utama
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Nama pengguna tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}