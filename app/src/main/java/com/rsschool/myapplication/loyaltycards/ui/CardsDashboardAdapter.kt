package com.rsschool.myapplication.loyaltycards.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rsschool.myapplication.loyaltycards.databinding.CardViewBinding
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard

class CardsDashboardAdapter : ListAdapter<LoyaltyCard, CardsDashboardAdapter.CardViewHolder>(DiffCallback()) {

    class CardViewHolder(private val binding: CardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: LoyaltyCard) {
            with(binding) {
                cardName.text = card.cardName
                cardNumber.text = card.cardNumber
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currItem = getItem(position)
        holder.bind(currItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<LoyaltyCard>() {
        override fun areItemsTheSame(oldItem: LoyaltyCard, newItem: LoyaltyCard): Boolean {
            return oldItem.cardId == newItem.cardId
        }

        override fun areContentsTheSame(oldItem: LoyaltyCard, newItem: LoyaltyCard) =
            oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(card: LoyaltyCard)
        fun onFavIconClick(card : LoyaltyCard, isChecked: Boolean)
    }

}
