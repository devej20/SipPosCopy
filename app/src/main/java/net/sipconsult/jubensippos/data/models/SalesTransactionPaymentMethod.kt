package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionPaymentMethod(
    @SerializedName("id")
    val id: Int,
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethodItem,
    @SerializedName("paymentMethodId")
    val paymentMethodId: Int,
    @SerializedName("transactionId")
    val transactionId: Int
)