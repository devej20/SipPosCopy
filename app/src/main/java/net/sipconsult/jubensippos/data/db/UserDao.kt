package net.sipconsult.jubensippos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.jubensippos.data.models.CURRENT_USER_ID
import net.sipconsult.jubensippos.data.models.LoggedInUser

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(user: LoggedInUser)

    @Query("DELETE FROM users WHERE id = $CURRENT_USER_ID")
    fun delete()

    @Query("SELECT * FROM users WHERE id = $CURRENT_USER_ID ")
    fun getUser(): LiveData<LoggedInUser>
}