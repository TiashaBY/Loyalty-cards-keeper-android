package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouriteCardsViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases) :
    BaseCardsViewModel(useCase) {

    override fun fetchData() = useCase.getFavoriteCards()
}
