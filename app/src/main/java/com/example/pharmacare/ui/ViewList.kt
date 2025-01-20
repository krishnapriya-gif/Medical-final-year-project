package com.example.pharmacare.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.AdminOrderAdapter
import com.example.pharmacare.adapter.SellerAdapter
import com.example.pharmacare.databinding.ActivityViewListBinding
import com.example.pharmacare.model.Entries
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.response.RetrofitInstance.TYPE
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewList : AppCompatActivity() {
    private val bind by lazy { ActivityViewListBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        val intent = intent.getStringExtra("listed")!!
        bind.textView2.text = "$intent's List"

        when (intent) {
            "Chemist" -> getList("Chemist")
            "User" -> getList("User")
            "NGO" -> getList("NGO")
            else -> getOrders()
        }


    }

    private fun getOrders() {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getOrderDetailsforAdmin("unverified")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {

                        val response = p1.body()!!
                        Log.d("fffsdasad", "onResponse: ${response.data5} ")
                        if (response.error) {
                            showToast("No Records")
                        } else {
                            if (response.data5?.isNotEmpty()!!) {
                                response.data5?.let {
                                    val list1 = it.filter { it.type == "PharmaCare1" }
                                    val list = list1.sortedBy { it.status == "unverified" }
                                    val orderAdapter = AdminOrderAdapter(
                                        orders = list,
                                        context = applicationContext,
                                        itemOnClick = {

                                        },
                                        onviewClick = {
                                            val bottomSheetFragment =
                                                ImageBottomSheetFragment.newInstance("${it.prescription}")
                                            bottomSheetFragment.show(
                                                supportFragmentManager,
                                                bottomSheetFragment.tag
                                            )
                                        }
                                    )

                                    bind.rvzlIst.adapter = orderAdapter
                                    bind.rvzlIst.layoutManager = LinearLayoutManager(this@ViewList)
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


    private fun getList(s: String) {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getRoleonly(s).enqueue(object :
                Callback<CommonResponse?> {
                override fun onResponse(
                    p0: Call<CommonResponse?>,
                    p1: Response<CommonResponse?>,
                ) {
                    val load = p1.body()
                    load?.let { response ->
                        if (response.error) {
                            showToast("Error Occurred")
                        } else {
                            val sellers = response.data.filter { it.type == TYPE }
                            if (sellers.isEmpty()) {
                                showToast("No Records")
                            } else {
                                bind.rvzlIst.adapter = SellerAdapter(sellers) {
                                    if (it.role == "User") {
                                        openDialog(it)
                                    } else {
                                        showToast(it.name)
                                    }
                                }
                                bind.rvzlIst.layoutManager = LinearLayoutManager(this@ViewList)
                            }
                        }

                    }



                    bind.progressBar4.isVisible = false
                }

                override fun onFailure(p0: Call<CommonResponse?>, p1: Throwable) {
                    showToast(p1.message!!)
                    bind.progressBar4.isVisible = false

                }
            })
        }
    }

    private fun openDialog(it: Entries) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Verify the user")
            .setMessage("Are you sure you want to ${it.name}?")
            .setPositiveButton("Verify") { dialog, _ ->
                updateUser(it, dialog)

            }
            .setNegativeButton("Dismiss") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateUser(it: Entries, dialog: DialogInterface) {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.updateUser(it.id,"1").enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(p0: Call<CommonResponse?>, p1: Response<CommonResponse?>) {
                    val response = p1.body()!!
                    if (!response.error) {
                        runOnUiThread {
                            showToast("User Verified")
                            dialog.dismiss()
                        }

                    } else {
                        showToast("Unverified")
                    }
                }

                override fun onFailure(p0: Call<CommonResponse?>, p1: Throwable) {
                    showToast(p1.message!!)
                }
            })

        }
    }


}



