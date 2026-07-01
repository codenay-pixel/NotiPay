package com.example.notipay

import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.text.SimpleDateFormat
import java.util.*

class NotificationHandler : NotificationListenerService(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate() {
        super.onCreate()
        dbHelper = DatabaseHelper(this)
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("id", "ID"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
                tts?.setSpeechRate(1.1f)
                tts?.setPitch(1.15f)
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // --- PENJAGA PINTU (MASTER SWITCH) ---
        val prefs = getSharedPreferences("NotiPayPrefs", Context.MODE_PRIVATE)
        val isAppEnabled = prefs.getBoolean("isAppEnabled", true)

        if (!isAppEnabled) {
            Log.d("NotiPay", "Aplikasi dimatikan. Notifikasi diabaikan.")
            return
        }
        // -------------------------------------

        val packageName = sbn?.packageName?.lowercase() ?: ""
        val extras = sbn?.notification?.extras

        val title = extras?.getString("android.title")?.lowercase() ?: ""
        val text = extras?.getString("android.text")?.lowercase() ?: ""

        var isTransactionValid = false
        var jenisTransaksi = ""
        var detailSumber = ""

        fun deteksiBankQRIS(textNotif: String): String {
            return when {
                textNotif.contains("bca") -> "Bank BCA"
                textNotif.contains("mandiri") -> "Bank Mandiri"
                textNotif.contains("bri") -> "Bank BRI"
                textNotif.contains("bni") -> "Bank BNI"
                textNotif.contains("seabank") -> "Bank Seabank"
                textNotif.contains("gopay") && !packageName.contains("gopay") -> "GoPay"
                textNotif.contains("ovo") && !packageName.contains("ovo") -> "OVO"
                textNotif.contains("shopee") && !packageName.contains("shopee") -> "ShopeePay"
                else -> ""
            }
        }

        // 1. DANA
        if (packageName.contains("id.dana") || title.contains("dana")) {
            if (title.contains("pembayaran masuk") || text.contains("dana bisnis")) {
                isTransactionValid = true
                jenisTransaksi = "QRIS DANA"
                val bank = deteksiBankQRIS(text)
                if (bank.isNotEmpty()) detailSumber = " - $bank"
            } else if (title == "dana" && text.contains("telah diterima dari")) {
                isTransactionValid = true
                jenisTransaksi = "Transfer DANA"
                val nama = text.substringAfter("dari ", "").replace(Regex("[^a-z ]"), "").trim().uppercase()
                if (nama.isNotEmpty()) detailSumber = " - $nama"
            }
        }
        // 2. GOPAY
        else if (packageName.contains("gojek") || packageName.contains("gopay") || title.contains("gopay")) {
            if (title.contains("gopay merchant") || text.contains("pembayaran qris")) {
                isTransactionValid = true
                jenisTransaksi = "QRIS GoPay"
                val bank = deteksiBankQRIS(text)
                if (bank.isNotEmpty()) detailSumber = " - $bank"
            } else if (text.contains("udah masuk ke gopay") || text.contains("transfer masuk")) {
                isTransactionValid = true
                jenisTransaksi = "Transfer GoPay"
                val nama = text.substringAfter("dari ", "").substringBefore(" udah").replace(Regex("[^a-z ]"), "").trim().uppercase()
                if (nama.isNotEmpty()) detailSumber = " - $nama"
            }
        }
        // 3. SHOPEEPAY
        else if (packageName.contains("shopee") || title.contains("shopee")) {
            if (title.contains("shopee partner") || (text.contains("pembayaran sebesar") && text.contains("transaksi"))) {
                isTransactionValid = true
                jenisTransaksi = "QRIS ShopeePay"
                val bank = deteksiBankQRIS(text)
                if (bank.isNotEmpty()) detailSumber = " - $bank"
            } else if (text.contains("saldo shopeepay diterima") || text.contains("telah diterima dari")) {
                isTransactionValid = true
                jenisTransaksi = "Transfer ShopeePay"
                val nama = text.substringAfter("dari ", "").substringBefore(".").replace(Regex("[^a-z ]"), "").trim().uppercase()
                if (nama.isNotEmpty()) detailSumber = " - $nama"
            }
        }
        // 4. OVO
        else if (packageName.contains("ovo") || title.contains("ovo")) {
            if (text.contains("mengirimkan dana sebesar")) {
                isTransactionValid = true
                jenisTransaksi = "Transfer OVO"
                val nama = text.substringBefore(" mengirimkan").replace(Regex("[^a-z ]"), "").trim().uppercase()
                if (nama.isNotEmpty()) detailSumber = " - $nama"
            }
        }

        if (isTransactionValid) {
            val nominalStr = extractNominal(text)
            val angkaHanya = nominalStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0
            val waktuSekarang = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault()).format(Date())

            val bankDetected = jenisTransaksi + detailSumber

            dbHelper.insertTransaksi(nominalStr, waktuSekarang, angkaHanya, bankDetected)
            Log.d("NotiPay", "Data berhasil disimpan: $nominalStr via $bankDetected")

            val intent = Intent("DANA_TRANSACTION")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            if (isTtsReady) {
                val isVoiceEnabled = prefs.getBoolean("isVoiceEnabled", true)
                if (isVoiceEnabled) {
                    val namaPembacaan = detailSumber.replace(" - ", "")
                    val pesanSuara = if (namaPembacaan.isNotEmpty()) {
                        "Mantap! Pembayaran $jenisTransaksi sebesar $nominalStr dari $namaPembacaan telah masuk!"
                    } else {
                        "Mantap! Pembayaran $jenisTransaksi sebesar $nominalStr telah masuk!"
                    }
                    tts?.speak(pesanSuara, TextToSpeech.QUEUE_FLUSH, null, "ID")
                }
            }
        }
    }

    private fun extractNominal(text: String): String {
        val regex = Regex("rp\\s?([\\d.]+)")
        val match = regex.find(text)
        return match?.value?.uppercase() ?: "Rp 0"
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }
}