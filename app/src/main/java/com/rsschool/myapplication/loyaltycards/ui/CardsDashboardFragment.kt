package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CardsDashboardFragment : CardsFragment() {

    private var _binding : CardsDashboardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    override val viewModel : CardsDashboardViewModel by viewModels()
    override lateinit var cardAdapter : CardsListAdapter
    private lateinit var searchView : SearchView

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
        cardAdapter = CardsListAdapter(OnLoyaltyCardClickListener(viewModel))

        binding.apply {
            listRecyclerView.apply {
                adapter = cardAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isListEmpty.collect {
                if (it) {
                    binding.noCardsMsg.visibility = View.VISIBLE
                } else {
                    binding.noCardsMsg.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.queryHint = getString(R.string.search_field_hint)
        searchView.onActionViewExpanded()
        searchView.clearFocus()
        searchView.setQuery(viewModel.searchQuery.value, false)

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

    override fun onDestroy() {
        super.onDestroy()
        searchView.setOnQueryTextListener(null)
    }
}
