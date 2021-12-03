package com.rsschool.myapplication.loyaltycards.ui.listener

import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel

interface OnCardClickListener {
    fun onItemDetailsClick(card: LoyaltyCard)
    fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean)
    fun onDeleteIconClick(card: LoyaltyCard)
}
