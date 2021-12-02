package com.rsschool.myapplication.loyaltycards.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rsschool.myapplication.loyaltycards.databinding.CardViewBinding
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.listener.OnCardClickListener

class CardsListAdapter(private val listener : OnCardClickListener) : ListAdapter<LoyaltyCard, CardsListAdapter.CardViewHolder>(
    DiffCallback()
) {

    inner class CardViewHolder(private val binding: CardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: LoyaltyCard) {
            with(binding) {
                cardName.text = card.cardName
                cardNumber.text = card.cardNumber
                likeButtonCb.isChecked = card.isFavourite

                binding.details.setOnClickListener {
                    listener.onItemDetailsClick(card)
                }
                binding.deleteButton.setOnClickListener {
                    listener.onDeleteIconClick(card)
                }
                binding.likeButtonCb.setOnCheckedChangeListener { _, isChecked ->
                    listener.onFavIconClick(card, isChecked)
                }
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
            oldItem.cardId == newItem.cardId &&
                    oldItem.cardName == newItem.cardName &&
                    oldItem.cardNumber == newItem.cardNumber
    }
}
