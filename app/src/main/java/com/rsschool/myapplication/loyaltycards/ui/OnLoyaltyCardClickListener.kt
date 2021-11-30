package com.rsschool.myapplication.loyaltycards.ui

import androidx.lifecycle.ViewModel
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard


interface OnCardClickListener {
    fun onItemClick(card: LoyaltyCard)
    fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean)
    fun onDeleteIconClick(card: LoyaltyCard)
}
class OnLoyaltyCardClickListener(viewModel: ViewModel): OnCardClickListener  {
    override fun onItemClick(card: LoyaltyCard) {
        TODO("Not yet implemented")
    }

    override fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onDeleteIconClick(card: LoyaltyCard) {
        TODO("Not yet implemented")
    }
}