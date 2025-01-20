package com.example.pharmacare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacare.R
import com.example.pharmacare.databinding.OrderLayoutBinding
import com.example.pharmacare.model.OrderId
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.spanned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderAdapter(
    private var orders: List<OrderId>,
    private val context: Context,
    private val itemOnClick: (OrderId) -> Unit,
    private val onviewClick: (OrderId) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: OrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val orderIdTextView = binding.orderId
        val btnVerify = binding.btnClickON
        val btnReject = binding.rejectBtn
        val time = binding.time
        val paymentInfo = binding.paymentInfo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        enableButtons(holder)
        holder.orderIdTextView.text = spanned("<b>Order ID:</b> ${order.orderid}")
        holder.time.text = spanned("<b>Time:</b> ${order.date}")
        holder.paymentInfo.text = spanned("<b>Status:</b> ${order.status}")

        holder.btnVerify.isEnabled = true
        holder.btnReject.isEnabled = true
        holder.btnVerify.alpha = 1.0f
        holder.btnReject.alpha = 1.0f

        when (order.status) {
            "unverified" -> {
                // Show Verify and Reject buttons
                holder.btnVerify.apply {
                    text = "Verify"
                    visibility = View.VISIBLE
                    setBackgroundColor(context.getColor(R.color.background_Tint))
                    setOnClickListener {
                        disableButtons(holder)
                        updateOrderForAdmin(order, holder.adapterPosition,"Pending")
                    }
                }
                holder.btnReject.apply {
                    text = "Reject"
                    visibility = View.VISIBLE
                    setBackgroundColor(context.getColor(R.color.red))
                    setOnClickListener {
                        disableButtons(holder)
                        updateOrderForAdmin(order, holder.adapterPosition,"rejected")
                    }
                }
            }

            "Pending" -> {
                holder.btnVerify.apply {
                    text = "Verified"
                    visibility = View.VISIBLE
                    setBackgroundColor(context.getColor(R.color.soft_green))
                    isEnabled = false
                    alpha = 0.6f
                }
                holder.btnReject.visibility = View.GONE
            }

            "rejected" -> {
                holder.btnVerify.apply {
                    text = "Rejected"
                    visibility = View.VISIBLE
                    setBackgroundColor(context.getColor(R.color.red))
                    isEnabled = false
                    alpha = 0.6f
                }
                holder.btnReject.visibility = View.GONE
            }

            else -> {
                holder.btnVerify.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
        }

        holder.binding.root.setOnClickListener {
            itemOnClick(order)
        }

        holder.binding.viewInfo.setOnClickListener {
            onviewClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size


    private fun disableButtons(holder: OrderViewHolder) {
        holder.btnVerify.isEnabled = false
        holder.btnReject.isEnabled = false
        holder.btnVerify.alpha = 0.6f
        holder.btnReject.alpha = 0.6f
    }


    private fun enableButtons(holder: OrderViewHolder) {
        holder.btnVerify.isEnabled = true
        holder.btnReject.isEnabled = true
        holder.btnVerify.alpha = 1.0f
        holder.btnReject.alpha = 1.0f
    }


    private fun updateOrderForAdmin(order: OrderId, position: Int, status: String) {

        CoroutineScope(IO).launch {

            RetrofitInstance.instance.updateOrderStatusForAdmin(status, order.orderid)
                .enqueue(object : Callback<CommonResponse?> {
                    override fun onResponse(
                        call: Call<CommonResponse?>,
                        response: Response<CommonResponse?>,
                    ) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                context,
                                "Order $status: ${responseBody.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            orders[position].status = status
                            notifyItemChanged(position)
                        } else {
                            Toast.makeText(
                                context,
                                "Verification Failed: ${responseBody?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            notifyItemChanged(position)
                        }

                    }

                    override fun onFailure(call: Call<CommonResponse?>, response: Throwable) {
                        Toast.makeText(
                            context,
                            "Network Failure: ${response.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        notifyItemChanged(position)
                    }
                })

        }

    }


}
