package com.example.pharmacare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacare.databinding.ViewCartBinding
import com.example.pharmacare.model.Products
import com.example.pharmacare.utils.CartManager


class CartAdapter(private val context: Context, private val cartItems: Map<Products, Int>) :
    RecyclerView.Adapter<CartAdapter.CartHolder>() {

    class CartHolder(val bind: ViewCartBinding) : RecyclerView.ViewHolder(bind.root) {
        @SuppressLint("SetTextI18n")

        fun bind(products: Products, quantity: Int, context: Context) {
            bind.apply {
                Glide.with(context).load(products.itemPhoto).into(ivProduct)
                etName.text = products.itemName
                etPrice.text =
                    CartManager.formatRupees(CartManager.calculateItemTotal(products))
                etQuantity.text = "Qty: $quantity"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val view = ViewCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartHolder(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val items = cartItems.entries.toList()[position]
        holder.bind.switch1.visibility = View.GONE
        holder.bind(items.key, items.value, context.applicationContext)
    }
}