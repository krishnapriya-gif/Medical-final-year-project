package com.example.pharmacare.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.ItemAdapter2
import com.example.pharmacare.databinding.ActivityInventoryBinding
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

class InventoryActivity : AppCompatActivity() {
    private val bind by lazy { ActivityInventoryBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }
    private lateinit var itemAdapter: ItemAdapter2
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)


        id = shared.getUserId()!!
        showData()

    }

    private fun showData() {
        CoroutineScope(IO).launch {
            bind.progressBar2.isVisible = true

            RetrofitInstance.instance.getProductsById(id)
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {
                        val response = p1.body()!!

                        if (response.error) {
                            showToast("List failed to load")
                            bind.progressBar2.isVisible = false
                        } else {
                            val list = response.data!!
                            if (list.isEmpty()) {
                                showToast("Out of stock")
                            } else {

                                itemAdapter = ItemAdapter2(
                                    this@InventoryActivity,
                                    list,
                                    "${shared.getUserId()}"
                                )
                                bind.rvProductList.layoutManager =
                                    LinearLayoutManager(this@InventoryActivity)
                                bind.rvProductList.adapter = itemAdapter
                                showToast("Loaded")
                                bind.progressBar2.isVisible = false
                            }
                        }

                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        launch(Dispatchers.Main) {
                            showToast(p1.message!!)
                            bind.progressBar2.isVisible = false
                        }
                    }
                })
        }
    }
}