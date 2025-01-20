package com.example.pharmacare.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pharmacare.adapter.CartAdapter
import com.example.pharmacare.databinding.ActivityCartBinding
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.response.RetrofitInstance.TYPE
import com.example.pharmacare.utils.CartManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private val bind by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    private lateinit var pickImageResult: ActivityResultLauncher<Intent>
    private var selectedImagePath: Uri? = null
    val simple = SimpleDateFormat("dd/MMMM/yyyy(hh:mm:ss)", Locale.getDefault())
    var price = ""
    var di = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        pickImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result.data?.data?.let { uri ->
                    selectedImagePath = uri

                }
            }
        val cartAdapter = CartAdapter(applicationContext, CartManager.getCartItems())
        bind.rvOrdersList.adapter = cartAdapter
        bind.rvOrdersList.layoutManager = LinearLayoutManager(this)

        price = CartManager.getFormattedPrice()
        bind.totalValue.text = price

        bind.uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            pickImageResult.launch(intent)
        }

        bind.btn1Place.setOnClickListener {
            if (selectedImagePath != null) {
                val userId = "${shared.getUserId()}"
                di = "PHAR${System.currentTimeMillis()}"
                CartManager.getCartItems().forEach { (products, quantity) ->

                    contentResolver?.openInputStream(selectedImagePath!!)?.readBytes()
                        ?.let { it: ByteArray ->
                            CoroutineScope(IO).launch {
                                RetrofitInstance.instance.placeTheOrder(
                                    "$di",
                                    "$userId",
                                    sellerid = "${products.sellerId}",
                                    status = "unverified",
                                    itemphoto = products.itemPhoto,
                                    itemname = products.itemName,
                                    qty = "$quantity",
                                    price = "${products.price * quantity}",
                                    date = simple.format(Date()),
                                    type = TYPE,
                                    dateOfExpiry = "${products.expiryDate}",
                                    prescription = Base64.encodeToString(
                                        it, Base64.NO_WRAP
                                    ),
                                    isAvailable = "true",
                                    isDonated = "false",
                                    donatedStatus = "Pending",
                                    "${products.id}"
                                ).enqueue(object : Callback<ProductResponse?> {
                                    override fun onResponse(
                                        p0: Call<ProductResponse?>,
                                        p1: Response<ProductResponse?>,
                                    ) {
                                        val response = p1.body()!!
                                        if (response.error) {
                                            showToast("Failed")
                                        } else {
                                            showToast("Order Placed Successfully")
                                            CartManager.clearCart()
                                            initiateUPIPayment(CartManager.calculatePrice())

                                        }
                                    }

                                    override fun onFailure(
                                        p0: Call<ProductResponse?>,
                                        p1: Throwable,
                                    ) {
                                        showToast(p1.message!!)
                                    }
                                })
                            }
                        }


                }
            } else {
                showToast("Please upload prescription ☝")
            }

        }

    }

    private fun initiateUPIPayment(formattedPrice: Double) {
        val amount = formattedPrice.toInt().toString()
        val uri = android.net.Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", "8074456123@upi")
            .appendQueryParameter("pn", "PharmaCare")
            .appendQueryParameter("tn", "Order id $di")
            .appendQueryParameter("am", "50")
            .appendQueryParameter("cu", "INR")
            .build()

        val upiIntent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }
        val chooser = Intent.createChooser(upiIntent, "Pay with")
        paymentLauncher.launch(chooser)
    }

    private fun navigateToTransactionSuccess() {
        finishAffinity()
        startActivity(
            Intent(
                this@CartActivity,
                OrderPlaced::class.java
            ).putExtra("orderId", di)
        )
        showToast("Order Placed Successfully")
    }

    private val paymentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val transactionResponse = result.data?.getStringExtra("response")
                    val status = transactionResponse?.split("&")?.find { it.startsWith("status=") }
                    when {
                        status != null && status.split("=")[1] == "success" -> {
                            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {
                    Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show()
                }
            }
            navigateToTransactionSuccess()
        }
}