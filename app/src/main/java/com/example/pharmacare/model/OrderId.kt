package com.example.pharmacare.model

import android.os.Parcel
import android.os.Parcelable


data class OrderId(
    val orderid: String,
    var date: String,
    var status: String,
    var prescription:String?,
    var type:String?,
    var donatedStatus:String?,
    var productId:String?

)