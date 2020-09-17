package net.sipconsult.jubensippos.data.repository.paymentMethod

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.PaymentMethodItem
import net.sipconsult.jubensippos.data.models.Voucher
import net.sipconsult.jubensippos.internal.Result

interface PaymentMethodRepository {
    suspend fun getPaymentMethods(): LiveData<List<PaymentMethodItem>>
    suspend fun getVoucher(code: String): Result<Voucher>
}