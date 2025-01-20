package com.example.pharmacare.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.ItemAdapter
import com.example.pharmacare.databinding.ActivityProductsBinding
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

class ProductsActivity: AppCompatActivity() {
    var id = ""
    var role = ""
    var tameId = ""
    private val bind by lazy { ActivityProductsBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        id = intent.getStringExtra("jet")!!
        role = shared.getUserRole()!!


        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getOrdersById(id)
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {
                        val list = p1.body()!!

                        if (list.error) {
                            showToast("Error Occurred")
                        } else {
                            showToast("List Loaded")
                            tameId = list.data2?.get(0)!!.id.toString()

                            val productAdapter = ItemAdapter(
                                applicationContext, list.data2!!
                            ) {
                                if (role == "User" && it.status == "Completed") {
                                    openDialog(it)
                                }else{
                                    showToast("you can't donate this product")
                                }

                            }
                            bind.rcProductsList.adapter = productAdapter
                            bind.rcProductsList.layoutManager =
                                LinearLayoutManager(this@ProductsActivity)

                        }
                    }

                    override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                        showToast(p1.message!!)
                    }
                })
        }

    }

    private fun openDialog(it: Order) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Update this for donation")
            .setMessage("Are you sure you want to donate ${it.itemname}?")
            .setPositiveButton("Confirm") { dialog, _ ->
                UpdateDonation(it, dialog)

            }
            .setNegativeButton("Dismiss") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun UpdateDonation(it: Order, dialog: DialogInterface?) {
        RetrofitInstance.instance.putForDonation("true", it.id.toString())
            .enqueue(object : Callback<ProductResponse?> {
                override fun onResponse(
                    p0: Call<ProductResponse?>,
                    p1: Response<ProductResponse?>,
                ) {
                    val resonse = p1.body()!!
                    if (resonse.error) {
                        showToast("Error Occurred")
                    } else {
                        showToast("Updated")
                        dialog?.dismiss()

                    }
                }

                override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                    showToast(p1.message!!)
                }
            })
    }


}
