package com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.Serializable

abstract class BaseCardsViewModel (private val useCase: LoyaltyCardUseCases): ViewModel() {

    protected val _uiState = MutableStateFlow<MyResult<List<LoyaltyCard>>>(MyResult.Empty)
    val uiState = _uiState.asStateFlow()

    protected val _isListEmpty = MutableStateFlow(false)
    val isListEmpty = _isListEmpty.asStateFlow()

    private val _dashboardEvent = MutableSharedFlow<DashboardEvent>()
    val dashboardEvent = _dashboardEvent.asSharedFlow()

    fun onItemDetailsClick(card: LoyaltyCard) {
        viewModelScope.launch {
            _dashboardEvent.emit(DashboardEvent.NavigateToDetailsView(card))
        }
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

sealed class DashboardEvent {
    data class NavigateToDetailsView(val card: LoyaltyCard) : DashboardEvent()
}
