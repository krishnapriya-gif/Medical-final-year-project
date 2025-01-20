package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacare.databinding.ActivityProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.pharmacare.utils.SessionManager

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.userLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("LOGOUT")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    shared.clearLoginState()
                    finishAffinity()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        binding.editTextName.text = "${shared.getUserName()}"
        binding.editTextAdress.text = "${shared.getUserLocation()}"
        binding.editTextEmail.text = "${shared.getUserEmail()}"
        binding.editTextMoblie.text = "${shared.getUserMobile()}"
        binding.editTextPassword.text = "${shared.getUserPassword()}"


    }
}