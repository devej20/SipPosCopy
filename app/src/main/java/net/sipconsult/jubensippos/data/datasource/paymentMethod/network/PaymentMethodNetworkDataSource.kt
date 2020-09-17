package net.sipconsult.jubensippos.data.datasource.paymentMethod.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.Voucher
import net.sipconsult.jubensippos.data.network.response.PaymentMethods
import net.sipconsult.jubensippos.internal.Result

interface PaymentMethodNetworkDataSource {
    val downloadPaymentMethods: LiveData<PaymentMethods>

    suspend fun fetchPaymentMethods()

    suspend fun getVoucher(code: String): Result<Voucher>
}