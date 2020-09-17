package net.sipconsult.jubensippos.data.repository.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sipconsult.jubensippos.data.datasource.transaction.local.TransactionLocalDataSource
import net.sipconsult.jubensippos.data.datasource.transaction.network.TransactionNetworkDataSource
import net.sipconsult.jubensippos.data.models.SaleTransactionPostBody
import net.sipconsult.jubensippos.data.network.response.SalesTransactions
import net.sipconsult.jubensippos.data.network.response.TransactionResponse
import net.sipconsult.jubensippos.internal.Result

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
    private val networkDataSource: TransactionNetworkDataSource
) :
    TransactionRepository {


    override suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.postTransaction(body)
        }
    }

    override suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions> {
        return withContext(Dispatchers.IO) {
            return@withContext networkDataSource.fetchSaleTransactions(operatorId)
        }
    }
}