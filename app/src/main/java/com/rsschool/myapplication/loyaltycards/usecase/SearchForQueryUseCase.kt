package com.rsschool.myapplication.loyaltycards.usecase

import com.rsschool.myapplication.loyaltycards.datasource.repository.CardsRepository
import javax.inject.Inject

class SearchForQueryUseCase @Inject constructor(private val repo: CardsRepository) {

    operator fun invoke(searchQuery:String) = repo.getCardByNameOrNumber(searchQuery)

}