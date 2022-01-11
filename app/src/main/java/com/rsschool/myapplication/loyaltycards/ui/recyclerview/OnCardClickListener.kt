package com.rsschool.myapplication.loyaltycards.ui.listener

import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard

interface OnCardClickListener {
    fun onItemDetailsClick(card: LoyaltyCard)
    fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean)
    fun onDeleteIconClick(card: LoyaltyCard)
}
