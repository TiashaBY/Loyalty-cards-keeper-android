package com.rsschool.myapplication.loyaltycards.datasource.room

import androidx.room.*
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface LoyaltyCardDao {
    @Insert
    suspend fun insert(card: LoyaltyCard): Long

    @Update
    suspend fun update(card: LoyaltyCard): Int

    @Delete
    suspend fun delete(card: LoyaltyCard): Int

    @Query("""SELECT distinct * 
            FROM CardsTable
            WHERE cardName like '%' || :query || '%'or cardNumber like '%' || :query || '%'
            order by cardName ASC """)
    fun getCardByNameOrNumber(query: String = ""): Flow<List<LoyaltyCard>>

    @Query("""SELECT * 
            FROM CardsTable
            WHERE isFavourite = 1
            order by cardName ASC""")
    fun getFavouritesCarts(): Flow<List<LoyaltyCard>>
}
