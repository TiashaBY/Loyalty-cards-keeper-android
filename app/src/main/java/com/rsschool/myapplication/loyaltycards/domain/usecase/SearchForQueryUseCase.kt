package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import javax.inject.Inject

class SearchForQueryUseCase @Inject constructor(private val repo: CardsRepository) {
    operator fun invoke(searchQuery:String) = repo.getCardByNameOrNumber(searchQuery)
}
