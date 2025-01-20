package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.NgoRequestAdapter
import com.example.pharmacare.databinding.ActivityNgorequestsBinding
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.pharmacare.model.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NGODashboard : AppCompatActivity() {
    private val bind by lazy { ActivityNgorequestsBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }
    private lateinit var ngoRequestAdapter: NgoRequestAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        bind.imageView4.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        bind.imageView5.setOnClickListener {
            startActivity(Intent(this,NGOAccetedOrders::class.java))

        }


       ngoRequestAdapter =NgoRequestAdapter(
            context = applicationContext,
            list = emptyList(),
            onclick = {
                updateOrders(it)
            },
            ondelete = {

            }
        )

        bind.recylcer.adapter = ngoRequestAdapter
        bind.recylcer.layoutManager = LinearLayoutManager(this)




        getData()

    }

    private fun getData() {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getOrdersforNgo("true","Pending")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {
                        val response = p1.body()!!
                        if (!response.error) {
                            val list = response.data2!!
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

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun updateOrders(it: Order) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Are you sure you want to accept the request?")
            .setPositiveButton("Yes") { _, _ ->
                CoroutineScope(IO).launch {
                    RetrofitInstance.instance.acceptRequest(
                        shared.getUserName()!!,
                        shared.getUserId()!!,
                        "Accepted",
                        it.id
                    ).enqueue(object : Callback<ProductResponse?> {
                        override fun onResponse(
                            p0: Call<ProductResponse?>,
                            p1: Response<ProductResponse?>,
                        ) {
                            val response = p1.body()!!
                            if (!response.error) {
                                showToast("Accepted")
                                getData()
                            } else {
                                showToast("Failed")
                            }
                        }

                        override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                            showToast(p1.message!!)
                        }
                    })
                }
            }.setNegativeButton("Dismiss") { dialog, _ ->
                dialog.dismiss()
            }.show()

    }
}
