package com.example.pharmacare.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.pharmacare.databinding.ActivityRegistrationBinding
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

class RegistrationActivity : AppCompatActivity() {
    private val bind by lazy { ActivityRegistrationBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        bind.loginText.setOnClickListener {
            finish()
        }

        bind.registerButton.setOnClickListener {
            val name = bind.name.text.toString().trim()
            val mobile = bind.mobile.text.toString().trim()
            val password = bind.password.text.toString().trim()
            val email = bind.email.text.toString().trim()

            when {
                name.isEmpty() -> showToast("Please enter your name")

                mobile.isEmpty() || mobile.length < 10 -> showToast("Please enter a valid 10-digit mobile number")

                password.isEmpty() || password.length < 6 -> showToast("Password must be at least 6 characters long")

                email.isEmpty() || !email.contains("@gmail.com") -> showToast("Please enter a valid email")


                else -> {

                    bind.progressBar.isVisible = true
                    CoroutineScope(IO).launch {

                        RetrofitInstance.instance.userRegister(
                            name, mobile, password, "location",
                            email, "User", "0",TYPE
                        ).enqueue(object : Callback<CommonResponse?> {
                            override fun onResponse(
                                call: Call<CommonResponse?>,
                                response: Response<CommonResponse?>
                            ) {
                                val registerResponse = response.body()!!
                                if (!registerResponse.error) {
                                    finish()
                                    runOnUiThread {
                                        showToast("Registration Successful")
                                    }
                                } else {
                                    showToast(
                                        response.body()?.message
                                            ?: "Registration failed"
                                    )
                                }
                                bind.progressBar.isVisible = false
                            }

                            override fun onFailure(
                                call: Call<CommonResponse?>,
                                t: Throwable
                            ) {
                                showToast("Error: ${t.message}")
                                bind.progressBar.isVisible = false
                            }
                        }
                        )
                    }


                }
            }
        }

    }
}