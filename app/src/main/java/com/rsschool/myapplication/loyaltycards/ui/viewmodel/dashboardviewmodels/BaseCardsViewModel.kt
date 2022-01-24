package com.rsschool.myapplication.loyaltycards.ui.viewmodel.dashboardviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

//TODO: handle failure DB events
abstract class BaseCardsViewModel(private val useCase: LoyaltyCardUseCases) : ViewModel() {

    val uiState: StateFlow<DashboardUIState> by lazy {
        fetchData().mapNotNull { res ->
            res.handleResult()
        }.stateIn(viewModelScope, SharingStarted.Lazily, DashboardUIState.Loading)
    }

    private val _dashboardEvent = MutableSharedFlow<DashboardEvent>()
    val dashboardEvent = _dashboardEvent.asSharedFlow()

    private val job = Job()
    private val coroutineContext: CoroutineContext = job + Dispatchers.IO

    protected abstract fun fetchData(): Flow<ResultContainer<*>>

    private fun <T> ResultContainer<T>.handleResult(): DashboardUIState {
        return when (this) {
            is ResultContainer.Success -> {
                if ((data as List<*>).isEmpty()) {
                    DashboardUIState.Empty
                } else {
                    DashboardUIState.Success(data as List<LoyaltyCard>)
                }
            }
            is ResultContainer.Failure -> {
                DashboardUIState.Error(exception.message.toString())
            }
        }
    }

    fun onItemDetailsClick(card: LoyaltyCard) {
        viewModelScope.launch {
            withContext(this@BaseCardsViewModel.coroutineContext) {
                _dashboardEvent.emit(DashboardEvent.NavigateToDetailsView(card))
            }
        }
    }

    fun onFavIconClick(card: LoyaltyCard, checked: Boolean) {
        viewModelScope.launch {
            withContext(this@BaseCardsViewModel.coroutineContext) {
                useCase.updateFavorites(card, checked)
            }
        }
    }

    fun onDeleteIconClick(card: LoyaltyCard) {
        viewModelScope.launch {
            withContext(this@BaseCardsViewModel.coroutineContext) {
                useCase.deleteCard(card)
            }
        }
    }
}

sealed class DashboardEvent {
    data class NavigateToDetailsView(val card: LoyaltyCard) : DashboardEvent()
}

sealed class DashboardUIState {
    object Empty : DashboardUIState()
    object Loading : DashboardUIState()
    data class Success(val data: List<LoyaltyCard>) : DashboardUIState()
    data class Error(val msg: String) : DashboardUIState()
}

