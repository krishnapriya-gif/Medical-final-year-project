package com.example.pharmacare.response

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query



interface API {
    @FormUrlEncoded
    @POST("Entries.php")
    fun userRegister(
        @Field("name") name: String,
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("location") location: String,
        @Field("email") email: String,
        @Field("role") role: String,
        @Field("rating") ratingAndUserKey: String,
        @Field("type") type: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("Login.php")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("Login.php")
    fun getuser(): Call<CommonResponse>

    @FormUrlEncoded
    @POST("GetUserwithjoin.php")
    fun getRole(
        @Field("sellerid") role: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("GetEntries.php")
    fun getRoleonly(
        @Field("role") role: String,
    ): Call<CommonResponse>

    @GET("GetAllProducts.php")
    fun getAllProducts(): Call<ProductResponse>


    @FormUrlEncoded
    @POST("GetProductsById.php")
    fun getProductsById(
        @Field("sellerId") id: String,
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("GetOrdersByOrderId.php")
    fun getOrdersById(
        @Field("orderid") orderid: String,
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("GetOrdersBySellerId.php")
    fun getOrdersBySellerId(
        @Field("sellerid") sellerid: String
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("InsertOrderById.php")
    fun placeTheOrder(
        @Field("orderid") orderid: String,
        @Field("userid") userid: String,
        @Field("sellerid") sellerid: String,
        @Field("status") status: String,
        @Field("itemphoto") itemphoto: String,
        @Field("itemname") itemname: String,
        @Field("qty") qty: String,
        @Field("price") price: String,
        @Field("date") date: String,
        @Field("type") type: String,
        @Field("dateOfExpiry") dateOfExpiry: String,
        @Field("prescription") prescription: String,
        @Field("isAvailable") isAvailable: String,
        @Field("isDonated") isDonated: String,
        @Field("donatedStatus") donatedStatus: String,
        @Field("productId") productid: String,
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("GetOrdersByIdStatus.php")
    fun getOrdersByIdStatus(
        @Field("status") status: String,
        @Field("userid") id: String,
        @Field("sellerid") sellerid: String,
    ): Call<ProductResponse>


    @FormUrlEncoded
    @POST("UpdateOrderStatus.php")
    fun updateOrderStatus(
        @Field("status") status: String,
        @Field("orderid") orderid: String,
        @Field("userid") userid: String,
        @Field("sellerid") sellerid: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("updatestatusforadmin.php")
    fun updateOrderStatusForAdmin(
        @Field("status") status: String,
        @Field("orderid") orderid: String,
    ): Call<CommonResponse>


    @FormUrlEncoded
    @POST("AddItems.php")
    fun AddItem(
        @Field("itemName") itemName: String,
        @Field("itemPhoto") itemPhoto: String,
        @Field("price") price: String,
        @Field("sellerId") sellerId: String,
        @Field("shopName") shopName: String,
        @Field("rating") rating: String,
        @Field("isAvailable") isAvailable: String,
        @Field("type") type: String,
        @Field("expiryDate") expiryDate: String,
    ): Call<CommonResponse>


    @FormUrlEncoded
    @POST("DistinctOrderId.php")
    fun getOrderDetails(
        @Field("userid") userid: String,
        @Field("sellerid") sellerid: String,
        @Field("status") status: String,
    ): Call<ProductResponse>


    @FormUrlEncoded
    @POST("alldistinctOrders.php")
    fun getOrderDetailsforAdmin(
        @Field("status") status: String
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("GetOrderIdByUserId.php")
    fun getOrderDetailsByUser(
        @Field("userid") userid: String,
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("getExpiredMedicineByUserId.php")
    fun getExpiredMedicineByUserId(
        @Field("userid") userid: String,
    ): Call<ProductResponse>




    @FormUrlEncoded
    @POST("updateAvailabiltiy.php")
    fun updateAvailablity(
        @Field("isAvailable") isAvailable: String,
        @Field("sellerId") sellerid: String,
        @Field("id") productId: Int,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("verifyuser.php")
    fun updateUser(
        @Field("id") userId: Int,
        @Field("rating") rating: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("getOrdersngo.php")
    fun getOrdersforNgo(
        @Field("isDonated") isDonated: String,
        @Field("donatedStatus") donatedStatus: String,
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("getaccetpedOrders.php")
    fun getAccetpedOrders(
        @Field("ngoId") ngoId: String,
    ): Call<ProductResponse>




    @GET("GetUserwithjoin.php")
    fun getUserSpecific(
        @Query("sellerid") sellerid: String,
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("acceptRequest.php")
    fun acceptRequest(
        @Field("ngoName") ngoName: String,
        @Field("ngoId") ngoId: String,
        @Field("donatedStatus") donatedStatus: String,
        @Field("id") id: Int
    ):Call<ProductResponse>

    @FormUrlEncoded
    @POST("updatefordonation.php")
    fun putForDonation(
        @Field("isDonated") donatedStatus: String,
        @Field("id") id: String
    ):Call<ProductResponse>


}