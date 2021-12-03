package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DBResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CardsDashboardViewModel @Inject constructor(private val cardUseCase: LoyaltyCardUseCases,
                                                  state: SavedStateHandle): BaseCardsViewModel(cardUseCase) {

    private val _searchQuery = MutableStateFlow(state.get<String>("searchQuery") ?: "")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChange(query : String) {
        _searchQuery.value = query
        Log.d("dashboardEvent", "search=$query")
    }

    override fun onLoad() = viewModelScope.launch {
        try {
            val res = _searchQuery.flatMapLatest { cardUseCase.getCards.invoke(it) }
            res.collectLatest { list ->
                if (list.isEmpty()) {
                    _isListEmpty.value = true
                    _uiState.value = DBResult.Empty
                } else {
                    _isListEmpty.value = false
                    _uiState.value = DBResult.Success(list)
                }
            }
        } catch (e : Exception) {
            _uiState.value = DBResult.Failure(e)
        }
    }
}
