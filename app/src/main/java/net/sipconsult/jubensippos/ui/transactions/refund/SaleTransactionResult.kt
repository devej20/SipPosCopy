package net.sipconsult.jubensippos.ui.transactions.refund

import net.sipconsult.jubensippos.data.models.SalesTransactionsItem


data class SaleTransactionResult(
    val success: SalesTransactionsItem? = null,
    val error: Int? = null
)