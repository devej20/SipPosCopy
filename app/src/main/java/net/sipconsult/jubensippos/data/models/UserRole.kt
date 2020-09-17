package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName

data class UserRole(
    @SerializedName("role")
    val role: Role
)