package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacare.databinding.ActivityAdminDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.pharmacare.utils.SessionManager


class AdminDashboard : AppCompatActivity() {
    private val bind by lazy { ActivityAdminDashboardBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        bind.logout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("LOGOUT")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Confirm") { _, _ ->
                    shared.clearLoginState()
                    finishAffinity()
                    startActivity(Intent(this@AdminDashboard, LoginActivity::class.java))
                }
                .setNegativeButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        bind.addChemist.setOnClickListener {
            startActivity(Intent(applicationContext,AddCommonDataActivity::class.java).apply {
                putExtra("added","Chemist")
            })
        }
        bind.viewChemist.setOnClickListener {
            startActivity(Intent(applicationContext, ViewList::class.java).apply {
                putExtra("listed","Chemist")
            })
        }
        bind.addNgo.setOnClickListener {
            startActivity(Intent(applicationContext,AddCommonDataActivity::class.java).apply {
                putExtra("added","NGO")
            })
        }
        bind.viewNgo.setOnClickListener {
            startActivity(Intent(applicationContext, ViewList::class.java).apply {
                putExtra("listed","NGO")

            })
        }
        bind.verifyUser.setOnClickListener {
            startActivity(Intent(applicationContext, ViewList::class.java).apply {
                putExtra("listed","User")


            })
        }
        bind.verifyOrders.setOnClickListener {
            startActivity(Intent(applicationContext, ViewList::class.java).apply {
                putExtra("listed","Orders")

            })
        }





    }
}