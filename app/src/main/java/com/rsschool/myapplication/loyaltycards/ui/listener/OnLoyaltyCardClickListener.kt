package com.rsschool.myapplication.loyaltycards.ui.listener

import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.base.BaseCardsViewModel


interface OnCardClickListener {
    fun onItemDetailsClick(card: LoyaltyCard)
    fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean)
    fun onDeleteIconClick(card: LoyaltyCard)
}
class OnLoyaltyCardClickListener(private val viewModel: BaseCardsViewModel): OnCardClickListener  {

    override fun onItemDetailsClick(card: LoyaltyCard) {
        viewModel.onItemDetailsClick(card)
    }
    override fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean) {
        viewModel.onFavIconClick(card, isChecked)
    }
    override fun onDeleteIconClick(card: LoyaltyCard) {
        viewModel.onDeleteIconClick(card)
    }
}
