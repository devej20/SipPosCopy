package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName

data class SalesTransactionsItem(
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("discountType")
    val discountType: DiscountTypesItem?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("operator")
    val operator: Operator,
    @SerializedName("operatorId")
    val operatorId: String,
    @SerializedName("receiptNumber")
    val receiptNumber: String,
    @SerializedName("salesAgent")
    val salesAgentsItem: SalesAgentsItem,
    @SerializedName("salesTransactionPaymentMethod")
    val salesTransactionPaymentMethod: List<SalesTransactionPaymentMethod>,
    @SerializedName("salesTransactionProduct")
    val salesTransactionProduct: List<SalesTransactionProduct>
) {
    val totalSales: Double
        get() {
            var productTotalSales: Double = 0.0
            for (p in salesTransactionProduct) {
                productTotalSales += (p.product.price.salePrice * p.quantity)
            }

            if (discountType != null) {
                val salePriceDiscount = discountType.percentage.toDouble() / 100
                val discountPrice: Double = salePriceDiscount * productTotalSales
                val newSalesPrice: Double = productTotalSales - discountPrice

                productTotalSales = newSalesPrice
            }

            return productTotalSales
        }
}