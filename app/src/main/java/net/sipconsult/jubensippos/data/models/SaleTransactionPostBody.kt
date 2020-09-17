package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName

data class SaleTransactionPostBody(
    @SerializedName("OperatorUserId")
    val operatorUserId: String,
    @SerializedName("SalesAgentUserId")
    val salesAgentUserId: String,
    @SerializedName("LocationCode")
    val locationCode: String,
    @SerializedName("ReceiptNumber")
    val receiptNumber: String,
    @SerializedName("Description")
    val description: String = "",
    @SerializedName("discountTypeId")
    var discountTypeId: Int? = null,
    @SerializedName("SalesTransactionPaymentMethod")
    val salesTransactionPaymentMethod: List<SalesTransactionPostPaymentMethod>,
    @SerializedName("SalesTransactionProduct")
    val salesTransactionProduct: List<SalesTransactionPostProduct>
)