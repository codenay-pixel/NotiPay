package com.example.notipay

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "NotiPayDB"
        private const val DATABASE_VERSION = 2

        const val TABLE_NAME = "transaksi"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMINAL = "nominal"
        const val COLUMN_WAKTU = "waktu"
        const val COLUMN_NOMINAL_ANGKA = "angka"
        const val COLUMN_SUMBER = "sumber"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOMINAL + " TEXT,"
                + COLUMN_WAKTU + " TEXT,"
                + COLUMN_NOMINAL_ANGKA + " INTEGER,"
                + COLUMN_SUMBER + " TEXT" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTransaksi(nominalStr: String, waktu: String, nominalAngka: Long, sumber: String) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NOMINAL, nominalStr)
        values.put(COLUMN_WAKTU, waktu)
        values.put(COLUMN_NOMINAL_ANGKA, nominalAngka)
        values.put(COLUMN_SUMBER, sumber)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getTotalSaldoHariIni(): Long {
        var total: Long = 0
        val db = this.readableDatabase
        val tanggalHariIni = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val query = "SELECT SUM($COLUMN_NOMINAL_ANGKA) FROM $TABLE_NAME WHERE $COLUMN_WAKTU LIKE '$tanggalHariIni%'"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            total = cursor.getLong(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun getRecentTransaksi(limit: Int): List<Transaksi> {
        val list = mutableListOf<Transaksi>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC LIMIT $limit"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val nominal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMINAL))
                val waktu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAKTU))
                val sumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUMBER))
                list.add(Transaksi(nominal, waktu, sumber))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getAllTransaksi(): List<Transaksi> {
        val list = mutableListOf<Transaksi>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val nominal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMINAL))
                val waktu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAKTU))
                val sumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUMBER))
                list.add(Transaksi(nominal, waktu, sumber))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}