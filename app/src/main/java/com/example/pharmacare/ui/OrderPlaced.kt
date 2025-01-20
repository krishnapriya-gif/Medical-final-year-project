package com.example.pharmacare.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacare.databinding.ActivityOrderPlacedBinding

class OrderPlaced : AppCompatActivity() {
    private val bind by lazy { ActivityOrderPlacedBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        val intent = intent.getStringExtra("orderId")

        bind.orderId.text = "OrderId : $intent"

        bind.intentDash.setOnClickListener {
            startActivity(Intent(this, UserDashboard::class.java))
            finishAffinity()
        }
    }
}