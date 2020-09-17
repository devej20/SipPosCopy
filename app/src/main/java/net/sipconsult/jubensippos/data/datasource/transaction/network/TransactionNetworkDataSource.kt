package net.sipconsult.jubensippos.data.datasource.transaction.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.SaleTransactionPostBody
import net.sipconsult.jubensippos.data.network.response.SalesTransactions
import net.sipconsult.jubensippos.data.network.response.TransactionResponse
import net.sipconsult.jubensippos.internal.Result

interface TransactionNetworkDataSource {
    suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse>

    val downloadSaleTransaction: LiveData<SalesTransactions>

    suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions>
}