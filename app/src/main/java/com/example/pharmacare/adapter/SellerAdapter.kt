package com.example.pharmacare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacare.databinding.ViewFarmersBinding
import com.example.pharmacare.model.Entries
import com.example.pharmacare.utils.spanned

class SellerAdapter(var list: List<Entries>, var onclick: (Entries) -> Unit) :
    RecyclerView.Adapter<SellerAdapter.ViewFarmers>() {
    class ViewFarmers(val binding: ViewFarmersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entries: Entries) {
            binding.sellerName.text = spanned("<b>Name:</b> ${entries.name}")
            binding.sellerLocation.text = spanned("<b>Mobile:</b> ${entries.mobile}")

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewFarmers {
        val view = ViewFarmersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewFarmers(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewFarmers, position: Int) {
        holder.bind(list[position])
        holder.binding.ratingBar.visibility = View.GONE
        holder.binding.root.setOnClickListener {
            onclick(list[position])
        }
    }
}