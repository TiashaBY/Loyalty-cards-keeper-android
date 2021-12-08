package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.NavGraphDirections
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import com.rsschool.myapplication.loyaltycards.ui.listener.OnCardClickListener
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.CardsListAdapter
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DashboardEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
abstract class CardsFragment : Fragment() {

    abstract val viewModel : BaseCardsViewModel
    protected lateinit var cardAdapter: CardsListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onLoad()
        cardAdapter = CardsListAdapter(object: OnCardClickListener {
            override fun onItemDetailsClick(card: LoyaltyCard) {
                viewModel.onItemDetailsClick(card)
            }
            override fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean) {
                viewModel.onFavIconClick(card, isChecked)
            }
            override fun onDeleteIconClick(card: LoyaltyCard) {
                viewModel.onDeleteIconClick(card)
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it) {
                    is MyResult.Success -> {
                        cardAdapter.submitList(it.data)
                    }
                    is MyResult.Empty -> {
                        cardAdapter.submitList(emptyList())
                    }
                    is MyResult.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_on_db_get),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.dashboardEvent.collect { event ->
                    when (event) {
                        is DashboardEvent.NavigateToDetailsView -> {
                            val action = NavGraphDirections.actionToCardsDetailsFragment(event.card)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
}
