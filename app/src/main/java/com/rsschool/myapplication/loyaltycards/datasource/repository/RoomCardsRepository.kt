package com.rsschool.myapplication.loyaltycards.datasource.repository

import com.rsschool.myapplication.loyaltycards.datasource.room.LoyaltyCardDao
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomCardsRepository @Inject constructor(private val dao: LoyaltyCardDao) : CardsRepository {

    override suspend fun insert(card: LoyaltyCard) = dao.insert(card)

    override suspend fun update(card: LoyaltyCard) = dao.update(card)

    override suspend fun delete(card: LoyaltyCard) = dao.delete(card)

    override fun getCardByNameOrNumber(query: String) = dao.getCardByNameOrNumber(query)

    override fun getFavouritesCarts(): Flow<List<LoyaltyCard>> = dao.getFavouritesCarts()
}
