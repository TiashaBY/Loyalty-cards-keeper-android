package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(private val state: SavedStateHandle): ViewModel() {
    val card by lazy { state.get<LoyaltyCard>("card") }
}