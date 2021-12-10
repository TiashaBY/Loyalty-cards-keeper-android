package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetFavouritesListUseCase constructor(private val repo: CardsRepository) {

    operator fun invoke(): Flow<ResultContainer<*>> = repo.getFavouritesCarts().map {
        ResultContainer.Success(it)
    }.catch {
        ResultContainer.Failure(it)
    }
}

