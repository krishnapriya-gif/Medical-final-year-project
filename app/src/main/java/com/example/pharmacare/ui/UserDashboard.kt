package com.example.pharmacare.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.ProductAdapter
import com.example.pharmacare.chatbot.ChatActivity
import com.example.pharmacare.databinding.ActivityUserDashboardBinding
import com.example.pharmacare.model.Order
import com.example.pharmacare.response.MyService
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.response.RetrofitInstance.TYPE
import com.example.pharmacare.utils.CartManager
import com.example.pharmacare.utils.Extensionfile.filterByDonationStatus
import com.example.pharmacare.utils.Extensionfile.filterByExpiryDate
import com.example.pharmacare.utils.Extensionfile.sortByExpiryDate
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class UserDashboard : AppCompatActivity() {
    private val bind by lazy { ActivityUserDashboardBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }


    val simple = SimpleDateFormat("dd/MMMM/yyyy(hh:mm:ss)", Locale.getDefault())
    var status = ""




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        getData()
        CartManager.clearCart()
        startSchemesService()


        bind.floatingActionButton.setOnClickListener {
            val bottomSheet = BottomSheetAddProductFragment2()
            bottomSheet.show(supportFragmentManager, "AddProductBottomSheet")
        }

        bind.ngo.setOnClickListener{
            startActivity(Intent(this@UserDashboard, NGORequests::class.java))
        }

        bind.textView10.setOnClickListener {
            getData()
        }

        bind.chatbot.setOnClickListener {
            startActivity(Intent(this@UserDashboard, ChatActivity::class.java))
        }


        bind.usetTxt.text = "Welcome\n ${shared.getUserName()} 😀"

        bind.profile.setOnClickListener {
            startActivity(Intent(this@UserDashboard, ProfileActivity::class.java))
            getData()

        }

        bind.orders.setOnClickListener {
            startActivity(Intent(this, ViewUserOrderActivity::class.java))

        }

        bind.cart.setOnClickListener {
            if (CartManager.getTotalQuantity() < 1) {
                showToast("Empty Cart")
            } else {
                val intent = Intent(applicationContext, CartActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }

    private fun getData() {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getAllProducts().enqueue(object : Callback<ProductResponse?> {
                override fun onResponse(
                    p0: Call<ProductResponse?>,
                    p1: Response<ProductResponse?>,
                ) {
                    val response = p1.body()!!
                    val products = response.data!!
                    val filteredProducts = products.filter { it.type == TYPE }

                    if (response.error) {
                        showToast(response.message)
                    } else {
                        if (products.isEmpty()) {
                            showToast("No Products added yet")
                        } else {
                            Log.d("fjkfj", "onResponse: $filteredProducts")
                            val productAdapter = ProductAdapter(
                                applicationContext,
                                filteredProducts,"${shared.getUserId()}",
                             {

                            },{

                            },this@UserDashboard)
                            bind.rvzl.adapter = productAdapter
                            bind.rvzl.layoutManager = LinearLayoutManager(this@UserDashboard)


                        }
                    }
                    bind.progressBar3.isVisible = false
                }

                override fun onFailure(p0: Call<ProductResponse?>, p1: Throwable) {
                    showToast(p1.message!!)
                }
            })

        }

    }

    private fun startSchemesService() {

        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getExpiredMedicineByUserId("${shared.getUserId()}")
                .enqueue(object : Callback<ProductResponse?> {
                    override fun onResponse(
                        p0: Call<ProductResponse?>,
                        p1: Response<ProductResponse?>,
                    ) {
                        val response = p1.body()!!
                        val list = response.data2?.filter { it.status == "Completed" }
                        val converytelist = list?.let { ArrayList<Order>(it) }
                        if (response.error) {
                            showToast("Error occurred")
                        } else {
                            converytelist?.let {
                                val filteredProducts = it
                                    .filterByExpiryDate()
                                    .filterByDonationStatus(isDonated = false)
                                    .sortByExpiryDate()

                                Log.d("fhdfjkhdsj", "onResponse:$filteredProducts ")


                                if (filteredProducts.isNotEmpty()) {
                                    val intent = Intent(this@UserDashboard, MyService::class.java)

                                    intent.putParcelableArrayListExtra(
                                        "myList",
                                        ArrayList(filteredProducts)
                                    )
                                    startService(intent)
                                } else {
                                    showToast("No Products available")
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



    override fun onResume() {
        super.onResume()
        getData()
    }





}
