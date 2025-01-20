package com.example.pharmacare.ui

import android.os.Bundle
import android.se.omapi.Session
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.R
import com.example.pharmacare.adapter.NgoRequestAdapter
import com.example.pharmacare.databinding.ActivityNgoaccetedOrdersBinding
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.response.RetrofitInstance.TYPE
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NGOAccetedOrders : AppCompatActivity() {
    private val bind by lazy { ActivityNgoaccetedOrdersBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    var id = ""
    private lateinit var ngoRequestAdapter: NgoRequestAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(bind.root)
        id = shared.getUserId()!!

        ngoRequestAdapter = NgoRequestAdapter(
            context = applicationContext,
            list = emptyList(),
            onclick = {
                showToast("${it.dateOfExpiry}")
            },
            ondelete = {

            }
        )

        bind.recycler2.adapter = ngoRequestAdapter
        bind.recycler2.layoutManager = LinearLayoutManager(this)

        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getAccetpedOrders("$id")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {
                        val response = p1.body()!!
                        if (!response.error) {
                            val list = response.data2!!.filter { it.type == TYPE }
                            if (list.isNotEmpty()) {
                                ngoRequestAdapter.submitList(list)
                            } else {
                                showToast("No Donation Placed")
                            }

                        } else {
                            showToast(p1.message())
                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })
        }

    }
}