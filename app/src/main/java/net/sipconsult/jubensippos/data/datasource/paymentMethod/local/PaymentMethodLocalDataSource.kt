package net.sipconsult.jubensippos.data.datasource.paymentMethod.local

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.PaymentMethodItem

interface PaymentMethodLocalDataSource {

    val paymentMethods: LiveData<List<PaymentMethodItem>>

    fun updatePaymentMethods(paymentMethods: List<PaymentMethodItem>)
}