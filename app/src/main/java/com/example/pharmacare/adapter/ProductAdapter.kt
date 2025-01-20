package com.example.pharmacare.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacare.databinding.ViewProductBinding
import com.example.pharmacare.model.Products
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.CartManager
import com.example.pharmacare.utils.spanned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class ProductAdapter(
    private val context: Context,
    private var list: List<Products>,
    val userId: String,
    val onclick: (Products) -> Unit,
    val onDonate: (Products) -> Unit,
    val activity: Activity
) : RecyclerView.Adapter<ProductAdapter.MyProducts>() {

    class MyProducts(val binding: ViewProductBinding) : RecyclerView.ViewHolder(binding.root)


    val simple = SimpleDateFormat("dd/MMMM/yyyy(hh:mm:ss)", Locale.getDefault())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProducts {
        val aView =
            ViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyProducts(aView)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyProducts, position: Int) {
        val listed = list[position]


        holder.binding.apply {

            root.setOnClickListener {
                onDonate(listed)
            }

            val price = CartManager.formatRupees((listed.price).toDouble())

            etName.text = listed.itemName
            etPrice.text = price
            val ratingValue = listed.rating?.toFloatOrNull() ?: 0.0f
            ratingBar.rating = ratingValue
            seller.text = listed.expiryDate
            Glide.with(context).load(listed.itemPhoto).into(ivProduct)

            val quantity = CartManager.getCartItems()[listed] ?: 0


            if (CartManager.getCartItems()[listed] != null && quantity > 0) {
                quantityLayout.visibility = View.VISIBLE
                add.visibility = View.GONE
                val totalPriceForItem = CartManager.calculateItemTotal(listed)
                val formattedTotalPrice = CartManager.formatRupees(totalPriceForItem)
                etQuantity.text =
                    spanned("<b>Qty:</b> ${CartManager.getCartItems()[listed]} <br><b>Price:</b> ${formattedTotalPrice}").toString()
            } else {
                quantityLayout.visibility = View.GONE
                add.visibility = View.VISIBLE
            }

            add.setOnClickListener {
                quantityLayout.visibility = View.VISIBLE
                add.visibility = View.GONE
                addToCart(listed, holder)
            }
            btnPlus.setOnClickListener {
                updateQuantity(listed, true, holder)
            }
            btnMinus.setOnClickListener {
                updateQuantity(listed, false, holder)
            }


        }
    }
}


private fun updateQuantity(
    products: Products,
    isIncrement: Boolean,
    holder: ProductAdapter.MyProducts,
) {
    val quantity = CartManager.getCartItems()[products] ?: 0
    if (isIncrement) {
        CartManager.updateQuantity(products, quantity + 1)
    } else if (quantity > 0) {
        CartManager.updateQuantity(products, quantity - 1)
    }
    updateQuantityAndPrice(products, holder)
}

private fun addToCart(products: Products, holder: ProductAdapter.MyProducts) {
    CartManager.addToCart(products)
    updateQuantityAndPrice(products, holder)

}

private fun updateQuantityAndPrice(products: Products, holder: ProductAdapter.MyProducts) {
    val quantity = CartManager.getCartItems()[products] ?: 0
    val totalPrice = CartManager.calculateItemTotal(products)
    val formattedPrice = CartManager.formatRupees(totalPrice)

    if (quantity > 0) {
        holder.binding.quantityLayout.visibility = View.VISIBLE
        holder.binding.add.visibility = View.GONE
        holder.binding.etQuantity.text =
            spanned("<b><u>Qty:</u></b> $quantity <br><b>Price:</b> $formattedPrice")
    } else {
        holder.binding.quantityLayout.visibility = View.GONE
        holder.binding.add.visibility = View.VISIBLE
    }

}


