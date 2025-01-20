package com.example.pharmacare.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacare.R
import com.example.pharmacare.databinding.ViewCartBinding
import com.example.pharmacare.model.Products
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ItemAdapter2(
    private var context: Context,
    private var list: ArrayList<Products>,
    val id: String,
) :
    RecyclerView.Adapter<ItemAdapter2.IteMList>() {

    class IteMList(val binding: ViewCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IteMList {
        val view = ViewCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IteMList(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: IteMList, position: Int) {
        val item = list[position]
        item.isAvailable?.toBooleanStrictOrNull()?.let {
            holder.binding.apply {
                switch1.isChecked = it
                if (it) {
                    switch1.text = "In Stock"
                } else {
                    switch1.text = "Out of Stock"
                }
                Glide.with(context).load(item.itemPhoto).into(holder.binding.ivProduct)
                switch1.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        switch1.text = "In Stock"
                        switch1.thumbTintList = context.getColorStateList(R.color.light_lavender)
                        updatestatus(status = "true", productId = item.id)
                    } else {
                        switch1.text = "Out of Stock"
                        switch1.thumbTintList = context.getColorStateList(R.color.red)
                        updatestatus(status = "false", productId = item.id)

                    }
                }


                etName.text = "${item.itemName}"
                etPrice.text = "${CartManager.formatRupees((item.price).toDouble())}"
                etQuantity.isVisible = false
            }
        }
    }

    private fun updatestatus(status: String, productId: Int) {
        RetrofitInstance.instance.updateAvailablity(
            isAvailable = status, sellerid = id,
            productId = productId
        ).enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(p0: Call<CommonResponse?>, p1: Response<CommonResponse?>) {
                val response = p1.body()!!
                if (!response.error) {
                    Toast.makeText(context.applicationContext, "Status Updated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context.applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<CommonResponse?>, p1: Throwable) {
                Toast.makeText(context.applicationContext, p1.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}