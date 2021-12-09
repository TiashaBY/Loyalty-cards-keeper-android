package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CardsDashboardViewModel @Inject constructor(
    private val cardUseCase: LoyaltyCardUseCases,
    state: SavedStateHandle
) : BaseCardsViewModel(cardUseCase) {

    val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        state.get<String>("searchQuery")?.let {
            _searchQuery.value = it
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    override fun fetchData() = _searchQuery.flatMapLatest {
        cardUseCase.getCards(it)
    }
}
