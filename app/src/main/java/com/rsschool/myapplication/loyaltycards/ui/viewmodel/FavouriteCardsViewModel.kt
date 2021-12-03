package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DBResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteCardsViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases) :
    BaseCardsViewModel(useCase) {

    override fun onLoad() = viewModelScope.launch {
        try {
            val res = useCase.getFavoriteCards()
            res.collectLatest { list ->
                if (list.isEmpty()) {
                    _isListEmpty.value = true
                    _uiState.value = DBResult.Empty
                } else {
                    _isListEmpty.value = false
                    _uiState.value = DBResult.Success(list)
                }
            }
        } catch (e: Exception) {
            _uiState.value = DBResult.Failure(e)
        }
    }
}
