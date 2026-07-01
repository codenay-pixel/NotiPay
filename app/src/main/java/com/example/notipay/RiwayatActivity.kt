package com.example.notipay

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class RiwayatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        val dbHelper = DatabaseHelper(this)

        // 1. Atur Teks Bulan dan Tahun di Header
        val txtBulanTahun = findViewById<TextView>(R.id.txtBulanTahun)
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        txtBulanTahun.text = sdf.format(Date())

        // 2. Logika Tombol Kembali (Merespons Ikon ⬅ pada XML Baru)
        val btnBack = findViewById<TextView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Menutup halaman riwayat dan kembali ke Dashboard (MainActivity)
        }

        // 3. Ambil Semua Data Transaksi dari Database
        val allTransactions = dbHelper.getAllTransaksi()

        // 4. Setup RecyclerView Riwayat Lengkap
        val rv = findViewById<RecyclerView>(R.id.rvRiwayatLengkap)
        rv.layoutManager = LinearLayoutManager(this)

        // Konversi ke MutableList agar sesuai dengan parameter TransactionAdapter
        rv.adapter = TransactionAdapter(allTransactions.toMutableList())
    }
}