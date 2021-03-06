package net.sipconsult.jubensippos.ui.payment.paymentmethod.loyalty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.jubensippos.data.repository.paymentMethod.PaymentMethodRepository

class LoyaltyViewModelFactory(private val paymentMethodRepository: PaymentMethodRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoyaltyViewModel(paymentMethodRepository) as T
    }
}