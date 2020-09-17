package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName

data class Voucher(
    @SerializedName("code")
    val code: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("value")
    val value: Int,
    @SerializedName("valueRemains")
    val valueRemains: Int
)