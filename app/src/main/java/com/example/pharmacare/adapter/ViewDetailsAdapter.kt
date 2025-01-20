package com.example.pharmacare.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacare.databinding.ViewCustomerBinding
import com.example.pharmacare.model.Entries
import com.example.pharmacare.utils.spanned


class ViewDetailsAdapter(
    private var list: List<Entries>,
    private var onclickIntent: (Entries) -> Unit
) :
    RecyclerView.Adapter<ViewDetailsAdapter.ViewYHolder>() {


    inner class ViewYHolder(val binding: ViewCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(user: Entries) {
            binding.apply {
                userIdTV.text = spanned("<b> UserId:</b> ${user.id}")
                itemNameTV.text = spanned("<b> Consumer:</b> ${user.name}")
                ItemQuantityTV.text = spanned("<b> Location:</b> ${user.location}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewYHolder {
        val view = ViewCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewYHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewYHolder, position: Int) {
        holder.bind(list[position])

        holder.binding.userIdTV.isVisible = false

        holder.binding.root.setOnClickListener {
            onclickIntent(list[position])
        }
    }

    fun submitList(newList: List<Entries>) {
        list = newList
        notifyDataSetChanged()
    }

}
