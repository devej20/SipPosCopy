package net.sipconsult.jubensippos.data.repository.transaction

import net.sipconsult.jubensippos.data.models.RefundTransactionPostBody
import net.sipconsult.jubensippos.data.models.SaleTransactionPostBody
import net.sipconsult.jubensippos.data.models.SalesTransactionsItem
import net.sipconsult.jubensippos.data.network.response.SalesTransactions
import net.sipconsult.jubensippos.data.network.response.TransactionResponse
import net.sipconsult.jubensippos.internal.Result

interface TransactionRepository {

    suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse>

    suspend fun postRefundTransaction(body: RefundTransactionPostBody): Result<TransactionResponse>

    suspend fun fetchSaleTransaction(transactionId: Int): Result<SalesTransactionsItem>

    suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions>

    suspend fun fetchLocationSaleTransactions(locationCode: String): Result<SalesTransactions>
}