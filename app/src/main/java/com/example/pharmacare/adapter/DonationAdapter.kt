package com.example.pharmacare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacare.databinding.ViewCartBinding
import com.example.pharmacare.model.Order
import com.example.pharmacare.utils.spanned


class DonationAdapter(
    private var context: Context,
    private var list: ArrayList<Order>,
    var onDonate: (Order) -> Unit,
) :
    RecyclerView.Adapter<DonationAdapter.IteMList>() {

    class IteMList(val binding: ViewCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IteMList {
        val view = ViewCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IteMList(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: IteMList, position: Int) {
        val item = list[position]
        holder.binding.apply {

            holder.binding.root.setOnClickListener {
                onDonate(item)
            }
            switch1.visibility = View.GONE
            switch1.visibility = View.GONE
            Glide.with(context).load(item.prescription).into(ivProduct)
            etName.text = spanned("<b>Item:</b> ${item.itemname}")
            if (item.dateOfExpiry.length == 0) {
                etPrice.visibility = View.GONE
            } else {
                etPrice.text = spanned("<b>DOE:</b> ${item.dateOfExpiry}")
            }
            etQuantity.visibility = View.GONE
            status.visibility = View.VISIBLE

            val statusMessage = if (item.donatedStatus == "Accepted") {
                "<b>Status:</b> Your request has been <b>${item.donatedStatus}</b> by ${item.ngoName}."
            } else {
                ""
            }

            status.text = spanned(statusMessage)


        }
    }

}