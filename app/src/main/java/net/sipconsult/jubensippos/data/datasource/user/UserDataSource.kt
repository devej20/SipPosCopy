package net.sipconsult.jubensippos.data.datasource.user

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.LoggedInUser
import net.sipconsult.jubensippos.data.models.SignInBody
import net.sipconsult.jubensippos.internal.Result

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
interface UserDataSource {

    suspend fun login(signInBody: SignInBody): Result<LoggedInUser>

    fun logout()

    fun setLoggedInUser(loggedInUser: LoggedInUser)

    val loggedInUser: LiveData<LoggedInUser>
}