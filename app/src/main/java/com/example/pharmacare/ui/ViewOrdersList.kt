package com.example.pharmacare.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.ItemAdapter
import com.example.pharmacare.databinding.ActivityViewOrdersListBinding
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

class ViewOrdersList : AppCompatActivity() {
    private val bind by lazy { ActivityViewOrdersListBinding.inflate(layoutInflater) }
    private lateinit var itemAdapter: ItemAdapter
    private val shared by lazy { SessionManager(this) }
    var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        val id = intent.getStringExtra("id")
        val status = intent.getStringExtra("status")
        userid = shared.getUserId()!!

        bind.ordersView.text = "Products".uppercase()



        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getOrdersByIdStatus("$status", "$id", "$userid")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>
                    ) {
                        val response = p1.body()!!
                        Log.d("fhsdhf", "onResponse: ${response.data2}")
                        if (response.error) {
                            showToast("Failed")
                        } else {
                            itemAdapter = ItemAdapter(this@ViewOrdersList, response.data2!!){}
                            bind.rvList.adapter = itemAdapter
                            bind.rvList.layoutManager = LinearLayoutManager(this@ViewOrdersList)
                            showToast("Success")

                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })
        }


    }

}
