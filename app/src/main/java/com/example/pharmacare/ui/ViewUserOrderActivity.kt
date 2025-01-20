package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.OrderAdapter
import com.example.pharmacare.databinding.ActivityViewUserOrderBinding
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

class ViewUserOrderActivity : AppCompatActivity() {
    private val bind by lazy { ActivityViewUserOrderBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    private lateinit var orderAdapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        val id = shared.getUserId()!!

        orderAdapter = OrderAdapter(emptyList(), {
            startActivity(Intent(this, ProductsActivity::class.java).apply {
                putExtra("jet", it.orderid)
            })
        }, {

        },{
            val bottomSheetFragment = ImageBottomSheetFragment.newInstance("${it.prescription}")
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        } ,"","${shared.getUserRole()}")

        bind.rvList.layoutManager = LinearLayoutManager(this)
        bind.rvList.adapter = orderAdapter

        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getOrderDetailsByUser("$id")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>
                    ) {

                        val response = p1.body()!!
                        val list = response.data5?.filter { it.productId != "Donated" }
                        Log.d("fjdhf", "onResponse: $list")
                        if (response.error) {
                            showToast("Error occurred")
                        } else {
                            list?.let {
                                if (it.isEmpty()) {
                                    showToast("No Records")
                                } else {
                                    orderAdapter.submitOrders(it)
                                }

                            }
                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })
        }


    }
}