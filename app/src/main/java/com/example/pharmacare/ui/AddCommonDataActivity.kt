package com.example.pharmacare.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.pharmacare.R
import com.example.pharmacare.databinding.ActivityAddSellerBinding
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.response.RetrofitInstance.TYPE
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCommonDataActivity : AppCompatActivity() {
    private val bind by lazy { ActivityAddSellerBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        val intent = intent.getStringExtra("added")!!

        if (intent == "NGO") {
            bind.icon.setImageResource(R.drawable.ngo)
        } else {
            bind.icon.setImageResource(R.drawable.chemist)
        }

        bind.tiName.hint = "Add $intent title"
        bind.tiEmail.hint = "Add $intent email"
        bind.tiPassword.hint = "Add $intent password"
        bind.location.hint = "Add $intent location"
        bind.mobile.hint = "Add $intent mobile"


        bind.submit.setOnClickListener {
            val title = bind.tiName.text.toString()
            val email = bind.tiEmail.text.toString()
            val password = bind.tiPassword.text.toString()
            val location = bind.location.text.toString()
            val mobile = bind.mobile.text.toString()
            val rating = bind.ratingBar.rating.toString()

            when {
                title.isEmpty() -> showToast("Please enter a valid name")
                email.isEmpty() || !email.contains("@gmail.com") -> showToast("Please enter a valid email")
                password.isEmpty() || password.length < 6 -> showToast("Please enter a valid password")
                location.isEmpty() -> showToast("Please enter a valid location")
                mobile.isEmpty() || mobile.length < 10 -> showToast("Please enter a valid mobile no.")
                rating.isEmpty() -> showToast("Please enter a valid entering")
                else -> {
                    bind.loadingBar.isVisible = true
                    CoroutineScope(IO).launch {
                        RetrofitInstance.instance.userRegister(
                            name = title,
                            mobile = mobile,
                            password = password,
                            location = location,
                            email = email,
                            role = intent,
                            ratingAndUserKey = "1",
                            type = TYPE
                        ).enqueue(object : Callback<CommonResponse?> {
                            override fun onResponse(
                                p0: Call<CommonResponse?>,
                                p1: Response<CommonResponse?>,
                            ) {
                                val response = p1.body()!!
                                if (!response.error) {
                                    runOnUiThread {
                                        showToast("$intent Added Successfully")
                                    }

                                    finish()
                                } else {
                                    showToast(response.message)
                                }
                                bind.loadingBar.isVisible = false
                            }

                            override fun onFailure(p0: Call<CommonResponse?>, p1: Throwable) {
                                showToast(p1.message!!)
                                bind.loadingBar.isVisible = false
                            }
                        })
                    }

                }

            }
        }


    }
}