package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardsDashboardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.FavouriteCardsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesDashboardFragment : CardsFragment() {

    override val viewModel: FavouriteCardsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CardsDashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            listRecyclerView.apply {
                adapter = cardAdapter
                setHasFixedSize(true)
            }
        }
    }

    override fun showEmptyState() {
        val msg = resources.getString(R.string.add_into_favourites_text)
        binding.noCardsMsg.apply {
            text = msg
            visibility = View.VISIBLE
        }
    }

    override fun clearEmptyState() {
        binding.noCardsMsg.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
