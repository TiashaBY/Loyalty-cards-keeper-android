package com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseCardsViewModel (private val useCase: LoyaltyCardUseCases): ViewModel() {

    protected val _uiState = MutableStateFlow<DBResult>(DBResult.Empty)
    val uiState = _uiState.asStateFlow()

    protected val _isListEmpty = MutableStateFlow(false)
    val isListEmpty = _isListEmpty.asStateFlow()

    protected val _dashboardEvent = MutableStateFlow<DashboardEvent?>(null)
    val dashboardEvent = _dashboardEvent.asStateFlow()

    fun onItemDetailsClick(card: LoyaltyCard) {
       _dashboardEvent.value = DashboardEvent.NavigateToDetailsView(card)
        _dashboardEvent.value = null
    }

    fun onFavIconClick(card: LoyaltyCard, checked: Boolean) {
        viewModelScope.launch {
            useCase.updateFavorites(card, checked)
        }
    }

    fun onDeleteIconClick(card: LoyaltyCard) {
        viewModelScope.launch {
            useCase.deleteCard(card)
        }
    }

    abstract fun onLoad(): Job
}

sealed class DBResult {
    data class Success(val value: List<LoyaltyCard>) : DBResult()
    object Empty : DBResult()
    data class Failure(val msg: Throwable) : DBResult()
}

sealed class DashboardEvent {
    data class NavigateToDetailsView(val card: LoyaltyCard) : DashboardEvent()
}
