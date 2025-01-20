package com.example.pharmacare.utils

import com.example.pharmacare.model.Products
import java.text.NumberFormat
import java.util.Locale

object CartManager {

    private val cart = mutableMapOf<Products, Int>()

    fun addToCart(products: Products, quantity: Int = 1) {
        cart[products] = cart.getOrDefault(products, 0) + quantity
    }

    fun getCartItems(): Map<Products, Int> {
        return cart
    }

    fun clearCart() {
        cart.clear()
    }

    fun formatRupees(totalPrice: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return numberFormat.format(totalPrice)

    }

    fun calculatePrice(): Double {
        var total = 0.0
        for ((products, quantity) in cart) {
            total += products.price * quantity
        }
        return total
    }

    fun calculateItemTotal(products: Products): Double {
        val quantity = cart[products] ?: 0
        return (products.price * quantity).toDouble()
    }

    fun getFormattedPrice(): String {
        val totalPrice = calculatePrice()
        return formatRupees(totalPrice)
    }


    fun updateQuantity(products: Products, quantity: Int) {
        if (quantity <= 0) {
            cart.remove(products)
        } else {
            cart[products] = quantity
        }
    }

    fun getTotalQuantity(): Int {
        return cart.values.sum()
    }
}