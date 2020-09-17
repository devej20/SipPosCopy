package net.sipconsult.jubensippos.data.network.response


import com.google.gson.annotations.SerializedName
import net.sipconsult.jubensippos.data.models.LoggedInUser

data class LoginResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("successful")
    val successful: Boolean,
    @SerializedName("user")
    val user: LoggedInUser
)