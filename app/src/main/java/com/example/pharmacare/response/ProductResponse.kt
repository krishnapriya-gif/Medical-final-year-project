package com.example.pharmacare.response

import com.example.pharmacare.model.Order
import com.example.pharmacare.model.OrderId
import com.example.pharmacare.model.Products


data class ProductResponse(
    var error: Boolean,
    var message: String,
    var data: ArrayList<Products>? = arrayListOf(),
    var data2: ArrayList<Order>? = arrayListOf(),
    var data5: ArrayList<OrderId>? = arrayListOf(),
    )