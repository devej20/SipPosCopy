package net.sipconsult.jubensippos.data.models


import com.google.gson.annotations.SerializedName


data class Role(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)