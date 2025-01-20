package com.example.pharmacare.model




import android.os.Parcel
import android.os.Parcelable


data class Order(
    var id:Int,
    var userid: String,
    var orderid:String,
    var productId: String,
    var itemname: String,
    var qty: String,
    var totalPrice: String,
    val itemphoto: String,
    var status: String,
    var date: String,
    var price: String,
    var dateOfExpiry: String,
    var prescription:String,
    var isAvailable:String,
    var isDonated:String,
    var donatedStatus:String,
    var ngoId:String,
    var ngoName:String,
    var type:String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(userid)
        parcel.writeString(orderid)
        parcel.writeString(productId)
        parcel.writeString(itemname)
        parcel.writeString(qty)
        parcel.writeString(totalPrice)
        parcel.writeString(itemphoto)
        parcel.writeString(status)
        parcel.writeString(date)
        parcel.writeString(price)
        parcel.writeString(dateOfExpiry)
        parcel.writeString(prescription)
        parcel.writeString(isAvailable)
        parcel.writeString(isDonated)
        parcel.writeString(donatedStatus)
        parcel.writeString(ngoId)
        parcel.writeString(ngoName)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }

}