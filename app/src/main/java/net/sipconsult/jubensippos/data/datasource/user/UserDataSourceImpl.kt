package net.sipconsult.jubensippos.data.datasource.user

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.db.UserDao
import net.sipconsult.jubensippos.data.models.LoggedInUser
import net.sipconsult.jubensippos.data.models.SignInBody
import net.sipconsult.jubensippos.data.network.SipShopApiService
import net.sipconsult.jubensippos.internal.Result
import java.io.IOException

class UserDataSourceImpl(
    private val sipShopApiService: SipShopApiService,
    private val userDao: UserDao
) : UserDataSource {
    override suspend fun login(signInBody: SignInBody): Result<LoggedInUser> {

        try {

            val loginResponse = sipShopApiService.authenticateAsync(signInBody)

            return if (loginResponse.successful) {
                Result.Success(
                    loginResponse.user
                )

            } else {
                Result.Error(
                    IOException("Error logging in")
                )
            }

        } catch (e: Throwable) {
            return Result.Error(
                IOException("Error logging in", e)
            )
        }
    }

    override fun logout() {
        userDao.delete()
    }

    override fun setLoggedInUser(loggedInUser: LoggedInUser) {
        userDao.upsert(loggedInUser)
    }

    override val loggedInUser: LiveData<LoggedInUser>
        get() = userDao.getUser()


}