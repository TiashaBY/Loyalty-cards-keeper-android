package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardsDashboardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.listener.OnLoyaltyCardClickListener
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.CardsListAdapter
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardsDashboardViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.DBResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CardsDashboardFragment : Fragment() {

    private var _binding : CardsDashboardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel : CardsDashboardViewModel by viewModels()
    lateinit var searchView : SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.setHasOptionsMenu(true)
        _binding = CardsDashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardAdapter = CardsListAdapter(OnLoyaltyCardClickListener(viewModel))

        binding.apply {
            listRecyclerView.apply {
                adapter = cardAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.onLoad()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.islistEmpty.collect() {
                if (it) {
                    binding.noCardsMsg.visibility = View.VISIBLE
                } else {
                    binding.noCardsMsg.visibility = View.GONE
                }
            }
        }

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        val menuItem = menu.findItem(R.id.app_bar_search)
        searchView = menuItem.actionView as SearchView
        searchView.queryHint = "Type name or number"
        searchView.setQuery(viewModel.searchQueryValue, true) //restore saved state

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onSearchQueryChange(newText)
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.userPrefsFragment -> {
                item.onNavDestinationSelected(findNavController())
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
