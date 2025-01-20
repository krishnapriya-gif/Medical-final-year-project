package com.example.pharmacare.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pharmacare.databinding.OrderLayoutBinding
import com.example.pharmacare.model.OrderId
import com.example.pharmacare.utils.spanned


class OrderAdapter(
    private var orders: List<OrderId>,
    private val itemOnClick: (OrderId) -> Unit,
    private val statusOnClick: (OrderId) -> Unit,
    private val viewOnClick: (OrderId) -> Unit,
    val status:String,
    var role: String
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: OrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val orderIdTextView = binding.orderId
        val btnAccept = binding.btnClickON
        val time = binding.time
        val paymentInfo = binding.paymentInfo

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = OrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderIdTextView.text = spanned("<b>Order Id:</b> ${order.orderid}")
        holder.time.text = spanned("<b>Time:</b> ${order.date}")
     // holder.paymentInfo.text = spanned("<b>Status:</b> ${status}")
        holder.binding.viewInfo.setOnClickListener {
            viewOnClick(order)

        }

        if (role == "User"){

            when (order.status) {
                "unverified" ->{
                    holder.paymentInfo.text = spanned("<b>Status:</b> Order is pending for verification by the admin.")

                }
                "Pending" -> {
                    holder.paymentInfo.text = spanned("<b>Status:</b> ${status}")
                    // holder.statusTextView.visibility = View.GONE
                }

                "Accepted" -> {

                    holder.paymentInfo.text = spanned("<b>Status:</b> Request has been accepted and is being processed.")

                    // holder.statusTextView.visibility = View.GONE
                }

                "Completed" -> {

                    holder.paymentInfo.text = spanned("<b>Status:</b> Products has been successfully delivered.")

                    //  holder.statusTextView.visibility = View.VISIBLE
                }

                else -> {
                    holder.btnAccept.visibility = View.GONE
                    // holder.statusTextView.visibility = View.GONE
                }
            }

        }else{
            when (status) {
                "unverified" ->{
                    holder.paymentInfo.text = spanned("<b>Status:</b> Order is pending for verification by the admin.")

                }
                "Pending" -> {
                    holder.btnAccept.text = "Approve Order"
                    holder.btnAccept.visibility = View.VISIBLE
                    holder.paymentInfo.text = spanned("<b>Status:</b> ${status}")
                    // holder.statusTextView.visibility = View.GONE
                }

                "Accepted" -> {
                    holder.btnAccept.text = "Close Order"
                    holder.btnAccept.visibility = View.VISIBLE
                    holder.paymentInfo.text = spanned("<b>Status:</b> Request has been accepted and is being processed.")

                    // holder.statusTextView.visibility = View.GONE
                }

                "Completed" -> {
                    holder.btnAccept.visibility = View.GONE
                    holder.paymentInfo.text = spanned("<b>Status:</b> Products has been successfully delivered.")

                    //  holder.statusTextView.visibility = View.VISIBLE
                }

                else -> {
                    holder.btnAccept.visibility = View.GONE
                    // holder.statusTextView.visibility = View.GONE
                }
            }
        }






        holder.binding.root.setOnClickListener {
            itemOnClick(order)
        }

        holder.btnAccept.setOnClickListener {
            statusOnClick(order)
        }

        holder.binding.viewInfo.setOnClickListener {
            viewOnClick(order)
        }




    }

    override fun getItemCount(): Int = orders.size


    fun submitOrders(list22: List<OrderId>) {
        orders = list22
        notifyDataSetChanged()
    }
}


