package net.sipconsult.jubensippos.data.network.response.xp


import com.google.gson.annotations.SerializedName

data class person(
    @SerializedName("error")
    val error: String,
    @SerializedName("successful")
    val successful: Boolean,
    @SerializedName("user")
    val user: User
)