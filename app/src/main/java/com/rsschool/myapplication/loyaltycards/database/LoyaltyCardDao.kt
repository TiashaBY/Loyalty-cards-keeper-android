package com.rsschool.myapplication.loyaltycards.database

import androidx.room.*
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard

@Dao
interface LoyaltyCardDao {
    @Insert
    suspend fun insert(card: LoyaltyCard): Long

    @Update
    suspend fun update(card: LoyaltyCard): Int

    @Delete
    suspend fun delete(card: LoyaltyCard): Int

    @Query("select * from CardsTable")
    suspend fun getAll() : List<LoyaltyCard>
}