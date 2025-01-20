package com.example.pharmacare.response

import com.example.pharmacare.model.Entries

data class CommonResponse(
    var error:Boolean,
    var message:String,
    var data: ArrayList<Entries>

)
