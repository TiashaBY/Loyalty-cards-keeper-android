package com.rsschool.myapplication.loyaltycards.ui.recyclerview

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardViewBinding
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.OnCardClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CardsListAdapter(private val listener : OnCardClickListener) : ListAdapter<LoyaltyCard, CardsListAdapter.CardViewHolder>(
    DiffCallback()
) {

    inner class CardViewHolder(private val binding: CardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: LoyaltyCard) {
            with(binding) {
                cardName.text = card.cardName
                cardNumber.text = card.cardNumber
                loadBitmap(card, this)

                likeButtonCb.isChecked = card.isFavourite

                details.setOnClickListener {
                    listener.onItemDetailsClick(card)
                }
                deleteButton.setOnClickListener {
                    listener.onDeleteIconClick(card)
                }
                likeButtonCb.setOnCheckedChangeListener { _, isChecked ->
                    listener.onFavIconClick(card, isChecked)
                }
            }
        }

        private fun loadBitmap(card: LoyaltyCard, binding: CardViewBinding) {
            val cardUri = Uri.parse(card.frontImage).path ?: ""
            val imgFile = File(cardUri)
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                withContext(Dispatchers.Main) {
                    Glide.with(binding.cardView)
                        .load(bitmap)
                        .error(R.drawable.card_default)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
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

    override fun onViewRecycled(holder: CardViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.itemView)
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

interface OnCardClickListener {
    fun onItemDetailsClick(card: LoyaltyCard)
    fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean)
    fun onDeleteIconClick(card: LoyaltyCard)
}