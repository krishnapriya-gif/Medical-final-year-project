package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.model.OrderId
import com.example.pharmacare.adapter.OrderAdapter
import com.example.pharmacare.databinding.ActivityDistinctOrdersBinding
import com.example.pharmacare.model.Products
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistinctOrders : AppCompatActivity() {
    private val bind by lazy { ActivityDistinctOrdersBinding.inflate(layoutInflater) }
    private lateinit var orderAdapter: OrderAdapter
    private val shared by lazy { SessionManager(this) }
    var id = ""
    var userId = ""
    var status = ""
    var orderStatus = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        id = intent.getStringExtra("id")!!
        userId = shared.getUserId()!!

        loadData()

        status = when (intent.getStringExtra("status")) {
            "Accepted" -> "Accepted"
            "Completed" -> "Completed"
            else -> "Pending"
        }

        bind.textView11.text = "$status Orders".uppercase()

        orderAdapter = OrderAdapter(orders = emptyList(), itemOnClick = {
            startActivity(Intent(this, ProductsActivity::class.java).apply {
                putExtra("jet", it.orderid)
                putExtra("status", status)
            })
        }, statusOnClick = {
            val newStatus = when (status) {
                "Pending" -> "Accepted"
                "Accepted" -> "Completed"
                "Completed" -> "Pending"
                else -> "Pending"
            }

            updatetheStatus(it, newStatus)

        }, viewOnClick = {
            val bottomSheetFragment = ImageBottomSheetFragment.newInstance("${it.prescription}")
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }, status = status, role = "${shared.getUserRole()}")

        bind.rvlist.adapter = orderAdapter
        bind.rvlist.layoutManager = LinearLayoutManager(this)
        Toast.makeText(this, "$id" + "$userId" + "$status", Toast.LENGTH_SHORT).show()


    }

    private fun loadData() {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getOrderDetails(id, userId, status)
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {
                        val response = p1.body()!!
                        Log.d("fffsdasad", "onResponse: ${response.data5} ")
                        orderStatus = response.data5?.get(0)?.status.toString()
                        if (response.error) {
                            showToast("No Records")
                        } else {
                            if (response.data5?.isNotEmpty()!!) {
                                response.data5?.let {
                                    orderAdapter.submitOrders(it)
                                }
                                showToast("Orders Retrieved")
                            } else {
                                showToast("No Records")
                            }

                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })
        }

    }

    private fun updatetheStatus(it: OrderId, newStatus: String) {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.updateOrderStatus(newStatus, it.orderid, id, userId)
                .enqueue(object : Callback<CommonResponse?> {
                    override fun onResponse(
                        p0: Call<CommonResponse?>,
                        p1: Response<CommonResponse?>,
                    ) {
                        val response = p1.body()!!
                        if (response.error) {
                            showToast("Error Occured")
                        } else {
                            showToast("Status Updated to $newStatus")
                            loadData()
                        }
                    }

                    override fun onFailure(p0: Call<CommonResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })
        }
    }
}
