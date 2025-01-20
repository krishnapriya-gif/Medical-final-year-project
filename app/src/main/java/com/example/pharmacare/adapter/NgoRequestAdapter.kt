package com.example.pharmacare.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacare.databinding.ViewProductBinding
import com.example.pharmacare.model.Products
import com.example.pharmacare.model.Order
import java.text.SimpleDateFormat
import java.util.Locale


class NgoRequestAdapter(
    private val context: Context,
    private var list: List<Order>,
    val onclick: (Order) -> Unit,
    val ondelete: (Products) -> Unit,
) : RecyclerView.Adapter<NgoRequestAdapter.MyProducts>() {

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
            etName.text = listed.itemname
            etPrice.visibility = View.INVISIBLE
            seller.text = listed.dateOfExpiry
            Glide.with(context).load(listed.itemphoto).into(ivProduct)
        }

        holder.binding.apply {
            root.setOnClickListener {
                onclick(listed)

            }
        }

    }


    fun submitList(newList: List<Order>) {
        list = newList
        notifyDataSetChanged()

    }


}