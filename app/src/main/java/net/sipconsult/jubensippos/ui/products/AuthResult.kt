package net.sipconsult.jubensippos.ui.products

import net.sipconsult.jubensippos.data.models.LoggedInUser

class AuthResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)
