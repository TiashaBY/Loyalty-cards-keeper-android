package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import javax.inject.Inject

class AddCardUseCase @Inject constructor(private val repo: CardsRepository) {

    suspend operator fun invoke(card: LoyaltyCard): MyResult<*> {
        val result = repo.insert(card)
        return if (result > 0) {
            MyResult.Success(result)
        } else {
            MyResult.Failure(Exception(result.toString()))
        }
    }
}
