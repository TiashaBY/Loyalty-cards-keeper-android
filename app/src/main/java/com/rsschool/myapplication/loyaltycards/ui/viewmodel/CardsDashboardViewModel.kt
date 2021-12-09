package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DashboardUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CardsDashboardViewModel @Inject constructor(
    private val cardUseCase: LoyaltyCardUseCases,
    state: SavedStateHandle
) : BaseCardsViewModel(cardUseCase) {

    private val _searchQuery = MutableStateFlow(state.get<String>("searchQuery") ?: "")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        Log.d("dashboardEvent", "search=$query")
    }

    override fun fetchData() =_searchQuery.flatMapLatest {
        cardUseCase.getCards(it)
    }
}
