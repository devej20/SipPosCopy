package net.sipconsult.jubensippos.data.datasource.transaction.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.jubensippos.data.models.SaleTransactionPostBody
import net.sipconsult.jubensippos.data.network.SipShopApiService
import net.sipconsult.jubensippos.data.network.response.SalesTransactions
import net.sipconsult.jubensippos.data.network.response.TransactionResponse
import net.sipconsult.jubensippos.internal.NoConnectivityException
import net.sipconsult.jubensippos.internal.Result
import java.io.IOException

class TransactionNetworkDataSourceImpl(private val sipShopApiService: SipShopApiService) :
    TransactionNetworkDataSource {
    override suspend fun postTransaction(body: SaleTransactionPostBody): Result<TransactionResponse> {
        try {
            val postTransaction = sipShopApiService.postTransactionAsync(body)

            return if (postTransaction.successful) {
                Result.Success(
                    postTransaction
                )

            } else {
                Result.Error(
                    IOException("Error logging in")
                )
            }
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "postTransaction: No internet Connection ", e)
            return Result.Error(
                IOException("Error logging in", e)
            )
        }
    }

    private val _downloadSaleTransaction = MutableLiveData<SalesTransactions>()

    override val downloadSaleTransaction: LiveData<SalesTransactions>
        get() = _downloadSaleTransaction

    override suspend fun fetchSaleTransactions(operatorId: String): Result<SalesTransactions> {
        return try {

            val call = sipShopApiService.getTransactionAsync(operatorId)

            Result.Success(
                call
            )


        } catch (e: Throwable) {
            return Result.Error(
                IOException("Error logging in", e)
            )
        }
    }


    companion object {
        private const val TAG: String = "TransNetworkDataSrc"
    }
}