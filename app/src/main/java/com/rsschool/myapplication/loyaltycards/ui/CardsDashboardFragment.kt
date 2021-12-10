package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardsDashboardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardsDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CardsDashboardFragment : CardsFragment() {

    override val viewModel : CardsDashboardViewModel by viewModels()
    private var searchView : SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
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
        binding.noCardsMsg.visibility = View.VISIBLE
    }

    override fun clearEmptyState() {
        binding.noCardsMsg.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView?.let {
            it.queryHint = getString(R.string.search_field_hint)
            it.onActionViewExpanded()
            it.clearFocus()
            it.setQuery(viewModel.searchQuery.value, false)

            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onSearchQueryChange(newText)
                    return true
                }
                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }
            })
        }
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
        searchView?.setOnQueryTextListener(null)
        _binding = null
    }
}
