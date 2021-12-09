package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DashboardUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteCardsViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases) :
    BaseCardsViewModel(useCase) {

    override fun fetchData() = useCase.getFavoriteCards()
}
