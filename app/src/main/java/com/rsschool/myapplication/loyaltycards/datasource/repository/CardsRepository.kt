package com.rsschool.myapplication.loyaltycards.datasource.repository

import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    suspend fun insert(card: LoyaltyCard): Long

    suspend fun update(card: LoyaltyCard): Int

    suspend fun delete(card: LoyaltyCard): Int

    fun getCardByNameOrNumber(query: String): Flow<List<LoyaltyCard>>
}