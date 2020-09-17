package net.sipconsult.jubensippos.data.network.response


import com.google.gson.annotations.SerializedName
import net.sipconsult.jubensippos.data.models.Voucher

data class VoucherResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("successful")
    val successful: Boolean,
    @SerializedName("voucher")
    val voucher: Voucher
)