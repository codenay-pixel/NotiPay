package com.example.notipay

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Pastikan urutannya: Nominal, Waktu, Bank (Semua String)
data class Transaksi(val nominal: String, val waktu: String, val bank: String)

class TransactionAdapter(private val list: MutableList<Transaksi>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNominal: TextView = view.findViewById(R.id.txtNominal)
        val txtWaktu: TextView = view.findViewById(R.id.txtWaktu)
        val txtBank: TextView = view.findViewById(R.id.txtBank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtNominal.text = list[position].nominal
        holder.txtWaktu.text = list[position].waktu
        holder.txtBank.text = list[position].bank
    }

    override fun getItemCount() = list.size
}