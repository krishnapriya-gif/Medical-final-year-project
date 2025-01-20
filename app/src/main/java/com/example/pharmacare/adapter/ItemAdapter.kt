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
import com.example.pharmacare.utils.CartManager
import com.example.pharmacare.utils.spanned

class ItemAdapter(private var context: Context, private var list: ArrayList<Order>, var onDonate:(Order)->Unit) :
    RecyclerView.Adapter<ItemAdapter.IteMList>() {

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
            Glide.with(context).load(item.itemphoto).into(ivProduct)
            etName.text = item.itemname
            etPrice.text = CartManager.formatRupees((item.price).toDouble())
            etQuantity.text = "Qty: ${item.qty}"

            val statusMessage = if (item.donatedStatus == "Accepted" && item.isDonated == "true") {
                "<b>Status:</b> Your request has been <b>${item.donatedStatus}</b> by ${item.ngoName}."
            } else if (item.donatedStatus == "Pending" && item.isDonated == "true") {
                "<b>Status:</b> Your product has been successfully posted for donation."
            }else{
                ""
            }

            status.text = spanned(statusMessage)
        }
    }

}