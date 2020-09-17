package net.sipconsult.jubensippos.data.repository.transaction

import net.sipconsult.jubensippos.data.models.SaleTransactionPostBody
import net.sipconsult.jubensippos.data.network.response.SalesTransactions
import net.sipconsult.jubensippos.data.network.response.TransactionResponse
import net.sipconsult.jubensippos.internal.Result

interface TransactionRepository {

    suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse>

    suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions>
}