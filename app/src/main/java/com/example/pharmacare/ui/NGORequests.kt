package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.DonationAdapter
import com.example.pharmacare.databinding.ActivityNgodashboardBinding
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NGORequests : AppCompatActivity() {
    private val bind by lazy { ActivityNgodashboardBinding.inflate(layoutInflater) }
    private lateinit var orderAdapter: DonationAdapter
    private val shared by lazy { SessionManager(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        loadData()
    }

    private fun loadData() {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getExpiredMedicineByUserId("${shared.getUserId()}")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>
                    ) {
                        val response = p1.body()

                        if (response != null) {
                            val list = response.data2?.filter { it.productId == "Donated" }
                            Log.d("dksnfjkdsbnjf", "onResponse: $list")
                            if (list.isNullOrEmpty()) {
                                showToast("No donated products available")
                            } else {
                                val arrayList = ArrayList(list)

                                // Update RecyclerView on the main thread
                                CoroutineScope(Dispatchers.Main).launch {
                                    orderAdapter = DonationAdapter(applicationContext, arrayList) {

                                    }
                                    bind.recycler.layoutManager = LinearLayoutManager(this@NGORequests)
                                    bind.recycler.adapter = orderAdapter
                                }
                            }
                        } else {
                            showToast("Failed to load products")
                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message ?: "Error occurred")
                    }
                })
        }
    }
}