# 📱 NotiPay

NotiPay adalah aplikasi Android yang membantu pelaku UMKM menerima konfirmasi pembayaran secara otomatis melalui **Text-to-Speech (TTS)**. Aplikasi memanfaatkan **Notification Listener Service** untuk mendeteksi notifikasi transaksi dari berbagai dompet digital, kemudian membacakan informasi pembayaran secara real-time sehingga penjual tidak perlu selalu melihat layar ponsel.

---

## ✨ Fitur Utama

* 🔔 Deteksi notifikasi transaksi secara otomatis.
* 🗣️ Konfirmasi pembayaran menggunakan **Text-to-Speech (Bahasa Indonesia)**.
* 💰 Mendeteksi nominal transaksi yang masuk.
* 📋 Menyimpan riwayat transaksi ke database lokal.
* 📊 Menampilkan daftar riwayat transaksi.
* 🎛️ Mengaktifkan atau menonaktifkan fitur pembacaan suara.
* 🔄 Mendukung beberapa platform pembayaran digital.

---

## 💳 Platform yang Didukung

* DANA
* GoPay
* ShopeePay
* OVO

Jenis transaksi yang didukung:

* Transfer saldo
* Pembayaran QRIS

---

## 🛠️ Teknologi yang Digunakan

| Teknologi                     | Keterangan                       |
| ----------------------------- | -------------------------------- |
| Kotlin                        | Bahasa pemrograman utama         |
| Android Studio                | IDE pengembangan                 |
| Notification Listener Service | Membaca notifikasi transaksi     |
| Text-to-Speech (TTS)          | Membacakan notifikasi pembayaran |
| SQLite                        | Penyimpanan data lokal           |
| SharedPreferences             | Penyimpanan pengaturan aplikasi  |
| LocalBroadcastManager         | Sinkronisasi data antarkomponen  |

---
## 📂 Struktur Project

```
NotiPay
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── README.md
```

---

## 🚀 Cara Menjalankan Project

1. Clone repository.

```
git clone https://github.com/codenay-pixel/NotiPay.git
```

2. Buka project menggunakan Android Studio.

3. Tunggu proses Gradle Sync selesai.

4. Jalankan aplikasi pada emulator atau perangkat Android.

5. Berikan izin:

   * Notification Access
   * Text-to-Speech
   * Notifikasi

6. Lakukan pengujian dengan mengirim transaksi ke akun e-wallet yang didukung.

---

## ⚙️ Cara Kerja Aplikasi

1. Aplikasi berjalan di latar belakang.
2. Notification Listener Service mendeteksi notifikasi baru.
3. Sistem memeriksa apakah notifikasi berasal dari aplikasi pembayaran digital.
4. Nominal transaksi dan informasi pengirim diekstraksi.
5. Data disimpan ke database SQLite.
6. Text-to-Speech membacakan informasi pembayaran.
7. Riwayat transaksi diperbarui secara otomatis.

---

## 📄 Lisensi

Project ini dibuat untuk keperluan pembelajaran, penelitian, dan pengembangan aplikasi Android. Silakan gunakan sebagai referensi dengan tetap mencantumkan atribusi yang sesuai.

---

## 👨‍💻 Pengembang

**Kim Naya**

Android Developer • Kotlin Developer

GitHub: https://github.com/codenay-pixel

---

## ⭐ Dukungan

Jika project ini bermanfaat, jangan lupa memberikan **Star ⭐** pada repository ini agar dapat membantu pengembangan selanjutnya.
