package com.example.notipay

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.OutputStreamWriter
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: TransactionAdapter
    private var transactionList = mutableListOf<Transaksi>()
    private lateinit var txtTotalSaldo: TextView

    private val createDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                tulisDataKeCSV(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        txtTotalSaldo = findViewById(R.id.txtTotalSaldo)

        // Setup RecyclerView Dashboard (Menampilkan 5 transaksi terbaru)
        val rvRiwayat = findViewById<RecyclerView>(R.id.rvRiwayat)
        adapter = TransactionAdapter(transactionList)
        rvRiwayat.layoutManager = LinearLayoutManager(this)
        rvRiwayat.adapter = adapter

        refreshDashboard()

        // ----------------------------------------------------
        // AMBIL NAMA PENGGUNA DARI LOGIN & ATUR SUB-HEADER
        // ----------------------------------------------------
        val prefs = getSharedPreferences("NotiPayPrefs", Context.MODE_PRIVATE)
        val savedName = prefs.getString("nama_pengguna", "UMKM")
        val tvSubHeader = findViewById<TextView>(R.id.tvSubHeader)
        tvSubHeader.text = "Halo $savedName, Selamat Datang"

        // ----------------------------------------------------
        // KONTROL TOMBOL UTAMA DAN NAVIGASI MENU BAWAH
        // ----------------------------------------------------
        findViewById<Button>(R.id.btnIzin).setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        findViewById<TextView>(R.id.btnLihatSemua).setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }

        // Klik menu Riwayat di bagian navigasi bawah
        findViewById<TextView>(R.id.menuRiwayat).setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }

        // Klik menu Profil di bagian navigasi bawah
        findViewById<TextView>(R.id.menuProfil).setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        val btnExport = findViewById<Button>(R.id.btnExport)
        btnExport.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/csv"
                putExtra(Intent.EXTRA_TITLE, "Laporan_Keuangan_UMKM.csv")
            }
            createDocumentLauncher.launch(intent)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            transactionReceiver, IntentFilter("DANA_TRANSACTION")
        )

        // ----------------------------------------------------
        // LOGIKA DUAL TOGGLE (MASTER APP & SPEAKER)
        // ----------------------------------------------------
        val switchApp = findViewById<Switch>(R.id.switchApp)
        val btnToggleSpeaker = findViewById<LinearLayout>(R.id.btnToggleSpeaker)
        val tvSpeakerIcon = findViewById<TextView>(R.id.tvSpeakerIcon)

        var isAppEnabled = prefs.getBoolean("isAppEnabled", true)
        var isVoiceEnabled = prefs.getBoolean("isVoiceEnabled", true)

        fun updateSpeakerUI() {
            if (isAppEnabled) {
                btnToggleSpeaker.isEnabled = true
                btnToggleSpeaker.alpha = 1.0f
                tvSpeakerIcon.text = if (isVoiceEnabled) "🔊" else "🔇"
            } else {
                btnToggleSpeaker.isEnabled = false
                btnToggleSpeaker.alpha = 0.4f
                tvSpeakerIcon.text = "🔇"
            }
        }

        // Set status toggle saat aplikasi pertama dibuka
        switchApp.isChecked = isAppEnabled
        updateSpeakerUI()

        // Listener Master Switch (Aplikasi Aktif / Nonaktif)
        switchApp.setOnCheckedChangeListener { _, isChecked ->
            isAppEnabled = isChecked
            prefs.edit().putBoolean("isAppEnabled", isChecked).apply()
            updateSpeakerUI()

            val pesan = if (isChecked) "Sistem Pencatat Aktif" else "Sistem Pencatat Dimatikan"
            Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
        }

        // Listener Tombol Speaker (Suara On / Off)
        btnToggleSpeaker.setOnClickListener {
            if (isAppEnabled) {
                isVoiceEnabled = !isVoiceEnabled
                prefs.edit().putBoolean("isVoiceEnabled", isVoiceEnabled).apply()
                updateSpeakerUI()

                val pesan = if (isVoiceEnabled) "Suara Robot Aktif" else "Suara Robot Dimatikan"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val transactionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshDashboard()
        }
    }

    private fun refreshDashboard() {
        val total = dbHelper.getTotalSaldoHariIni()
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        txtTotalSaldo.text = formatRupiah.format(total).replace("Rp", "Rp ")

        transactionList.clear()
        transactionList.addAll(dbHelper.getRecentTransaksi(5))
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        refreshDashboard()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(transactionReceiver)
        super.onDestroy()
    }

    private fun tulisDataKeCSV(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = OutputStreamWriter(outputStream)
                writer.write("Waktu Transaksi,Sumber Dana,Nominal Transaksi\n")

                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM transaksi", null)

                if (cursor.moveToFirst()) {
                    do {
                        val waktu = cursor.getString(cursor.getColumnIndexOrThrow("waktu"))
                        val sumber = cursor.getString(cursor.getColumnIndexOrThrow("sumber"))
                        val nominal = cursor.getString(cursor.getColumnIndexOrThrow("nominal"))
                        writer.write("\"$waktu\",\"$sumber\",\"$nominal\"\n")
                    } while (cursor.moveToNext())
                }
                cursor.close()
                writer.flush()
                Toast.makeText(this, "Laporan Excel Berhasil Disimpan!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
        }
    }
}