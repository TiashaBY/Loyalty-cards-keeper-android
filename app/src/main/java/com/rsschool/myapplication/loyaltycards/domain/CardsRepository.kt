package com.rsschool.myapplication.loyaltycards.domain

import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CardsRepository {
    suspend fun insert(card: LoyaltyCard): Long

    suspend fun update(card: LoyaltyCard): Int

    suspend fun delete(card: LoyaltyCard): Int

    fun getCardByNameOrNumber(query: String): Flow<List<LoyaltyCard>>

    fun getFavouritesCarts() : Flow<List<LoyaltyCard>>
}
