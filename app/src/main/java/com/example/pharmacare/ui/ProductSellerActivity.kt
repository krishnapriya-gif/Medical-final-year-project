package com.example.pharmacare.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.ItemAdapter
import com.example.pharmacare.databinding.ActivityProductSellerBinding
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductSellerActivity : AppCompatActivity() {
    var sellerid = ""
    private val bind by lazy { ActivityProductSellerBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        sellerid = intent.getStringExtra("id")!!

        CoroutineScope(IO).launch {

            RetrofitInstance.instance.getOrdersBySellerId(sellerid)
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>
                    ) {
                        val list = p1.body()!!
                        Log.d("jfhdhf", "onResponse: ${list.data}")
                        if (list.error) {
                            showToast("Error Occurred")
                        } else {
                            showToast("List Loaded")

                            val productAdapter = ItemAdapter(
                                applicationContext, list.data2!!
                            ){}

                            bind.rcProductsList.adapter = productAdapter
                            bind.rcProductsList.layoutManager =
                                LinearLayoutManager(this@ProductSellerActivity)

                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })

        }


    }


}
