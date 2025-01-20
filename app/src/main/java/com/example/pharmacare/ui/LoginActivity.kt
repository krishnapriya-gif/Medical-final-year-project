package com.example.pharmacare.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.pharmacare.databinding.ActivityLoginBinding
import com.example.pharmacare.response.CommonResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val bind by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.POST_NOTIFICATIONS
        )

        val permissionsNotGranted = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(permissionsNotGranted.toTypedArray(), 100)
            } else {
                ActivityCompat.requestPermissions(this, permissionsNotGranted.toTypedArray(), 100)
            }
        }

        if (shared.isLoggedIn()) {
            shared.getUserRole()?.let { navigateToDashboard(it) }
        }

        bind.signupText.setOnClickListener {
            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
        }

        bind.loginButton.setOnClickListener {
            val email = bind.email.text.toString().trim()
            val password = bind.password.text.toString().trim()

            if (email.isEmpty()) {
                showToast("Please enter your email")
            } else if (password.isEmpty()) {
                showToast("Please enter your password")
            } else {
                if (email == "admin" && password == "admin") {
                    shared.saveLoginState("-1", "admin", "", "", "", "", "", "")
                    navigateToDashboard("admin")
                    finish()
                } else {
                    bind.progressBar.isVisible = true
                    CoroutineScope(IO).launch {
                        RetrofitInstance.instance.userLogin(email, password)
                            .enqueue(object : Callback<CommonResponse?> {
                                override fun onResponse(
                                    call: Call<CommonResponse?>,
                                    response: Response<CommonResponse?>,
                                ) {
                                    val loginResponse = response.body()!!
                                    if (!loginResponse.error) {
                                        loginResponse.data.firstOrNull()?.let { user ->
                                            if (user.rating == "1") {
                                                shared.saveLoginState(
                                                    "${user.id}",
                                                    user.role,
                                                    user.name,
                                                    user.location,
                                                    user.mobile,
                                                    user.email,
                                                    user.password,
                                                    user.rating
                                                )
                                                navigateToDashboard(user.role)
                                                runOnUiThread {
                                                    showToast("Successfully logged in")
                                                }
                                            } else {
                                                runOnUiThread {
                                                    showToast("Verification pending. Your account will be active once verified")
                                                }
                                            }
                                        }
                                    } else {
                                        showToast("Invalid credentials")
                                    }
                                    bind.progressBar.isVisible = false
                                }


                                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                                    showToast(t.message ?: "Login failed")
                                    bind.progressBar.isVisible = false
                                }
                            })
                    }

                }
            }
        }
    }


    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "NGO" -> Intent(this, NGODashboard::class.java)
            "Chemist" -> Intent(this, ChemistDashboard::class.java)
            "User" -> Intent(this, UserDashboard::class.java)
            else -> Intent(this, AdminDashboard::class.java)
        }
        startActivity(intent)
        finish()
    }

}
