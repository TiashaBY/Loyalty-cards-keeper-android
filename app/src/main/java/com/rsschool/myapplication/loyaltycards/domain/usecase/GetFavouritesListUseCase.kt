package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetFavouritesListUseCase constructor(private val repo: CardsRepository) {

    operator fun invoke(): Flow<MyResult<*>> = repo.getFavouritesCarts().map {
        MyResult.Success(it)
    }.catch {
        MyResult.Failure(it)
    }
}

