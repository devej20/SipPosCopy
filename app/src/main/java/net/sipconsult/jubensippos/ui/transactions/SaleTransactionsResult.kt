package net.sipconsult.jubensippos.ui.transactions

import net.sipconsult.jubensippos.data.network.response.SalesTransactions


data class SaleTransactionsResult(
    val success: SalesTransactions? = null,
    val error: Int? = null
)