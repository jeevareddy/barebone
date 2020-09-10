package com.barebone.app

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * from user")
    fun getUsers(): List<ModelUser>

    @Query("SELECT * FROM user WHERE id=:id LIMIT 1")
    fun findUser(id: Int): ModelUser

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun write(u: ModelUser)

    @Update
    fun update(u: ModelUser)

    @Delete
    fun delete(u: ModelUser)
}