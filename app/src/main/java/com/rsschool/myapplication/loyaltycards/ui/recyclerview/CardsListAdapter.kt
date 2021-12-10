package com.rsschool.myapplication.loyaltycards.ui.recyclerview

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardViewBinding
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.listener.OnCardClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.String

class CardsListAdapter(private val listener : OnCardClickListener) : ListAdapter<LoyaltyCard, CardsListAdapter.CardViewHolder>(
    DiffCallback()
) {

    inner class CardViewHolder(private val binding: CardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: LoyaltyCard) {
            with(binding) {
                cardName.text = card.cardName
                cardNumber.text = card.cardNumber
                loadBitmap(card, binding)

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

        private fun loadBitmap(card: LoyaltyCard, binding: CardViewBinding) {
            val defaultUri = Uri.Builder()
                .scheme("res")
                .path(String.valueOf(R.drawable.card_default))
                .build().path
            val cardUri = Uri.parse(card.frontImage).path ?: ""
            val imgFile = if (File(cardUri).exists()) File(cardUri) else File(defaultUri)
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                withContext(Dispatchers.Main) {
                    Glide.with(binding.cardView)
                        .load(bitmap)
                        .error(R.drawable.card_default)
                        .fitCenter()
                        .into(binding.imageView)
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
