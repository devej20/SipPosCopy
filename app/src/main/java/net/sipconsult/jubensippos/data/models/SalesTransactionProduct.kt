package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("product")
    val product: ProductItem,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("tansactionId")
    val tansactionId: Int
)