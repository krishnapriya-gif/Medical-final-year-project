package com.example.pharmacare.utils

import com.example.pharmacare.model.Order
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Extensionfile {

    fun ArrayList<Order>.filterByExpiryDate(): ArrayList<Order> {
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        return ArrayList(this.filter {
            try {
                val expiryDate = sdf.parse(it.dateOfExpiry)
                if (expiryDate != null) {
                    val diffInMillis = abs(expiryDate.time - currentDate.time)
                    val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)
                    diffInDays <= 5
                } else {
                    false
                }
            } catch (e: Exception) {

                e.printStackTrace()
                false
            }
        })
    }

    fun ArrayList<Order>.filterByDonationStatus(isDonated: Boolean): ArrayList<Order> {
        return ArrayList(this.filter { it.isDonated == isDonated.toString() })
    }

    fun ArrayList<Order>.sortByExpiryDate(): ArrayList<Order> {
        return ArrayList(this.sortedBy {
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.dateOfExpiry)
            } catch (e: Exception) {

                e.printStackTrace()
                Date(Long.MAX_VALUE)
            }
        })
    }
}
