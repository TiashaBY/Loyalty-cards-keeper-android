package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.NavGraphDirections
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.CardsListAdapter
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DBResult
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DashboardEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
abstract class CardsFragment : Fragment() {

    abstract val viewModel : BaseCardsViewModel
    abstract val cardAdapter: CardsListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onLoad()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it) {
                    is DBResult.Success -> {
                        cardAdapter.submitList(it.value)
                    }
                    is DBResult.Empty -> {
                        cardAdapter.submitList(emptyList())
                    }
                    is DBResult.Failure -> {
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
