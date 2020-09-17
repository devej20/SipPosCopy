package net.sipconsult.jubensippos.ui.payment.paymentmethod.loyalty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.repository.paymentMethod.PaymentMethodRepository
import net.sipconsult.jubensippos.internal.lazyDeferred

class LoyaltyViewModel(private val paymentMethodRepository: PaymentMethodRepository) : ViewModel() {

    var voucherCode: String = ""

    val _voucherResult = MutableLiveData<VoucherResult>()
    val voucherResult: LiveData<VoucherResult> = _voucherResult

    val getVoucher by lazyDeferred {

        // can be launched in a separate asynchronous job
        paymentMethodRepository.getVoucher(voucherCode)

    }


}