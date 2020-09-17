package net.sipconsult.jubensippos.ui.payment.paymentmethod.loyalty

import net.sipconsult.jubensippos.data.models.Voucher

data class VoucherResult(
    val success: Voucher? = null,
    val error: Int? = null
)