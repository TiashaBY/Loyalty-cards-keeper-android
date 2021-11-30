package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardsDashboardFragmentBinding
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.model.SearchEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardsDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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
    ): View? {
        this.setHasOptionsMenu(true)
        _binding = CardsDashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardAdapter = CardsDashboardAdapter(object: CardsDashboardAdapter.OnCardClickListener {
            override fun onItemClick(card: LoyaltyCard) {
                viewModel.onItemClick(card)
            }
            override fun onFavIconClick(card: LoyaltyCard, isChecked: Boolean) {
                viewModel.onFavIconClick(card, isChecked)
            }
            override fun onDeleteIconClick(card: LoyaltyCard) {
                viewModel.onDeleteIconClick(card)
            }
        })

        binding.apply {
            listRecyclerView.apply {
                adapter = cardAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //????
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.cards.collect {
                cardAdapter.submitList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.app_bar_search)
        searchView = menuItem.actionView as SearchView
        searchView.queryHint = "Type name or number"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onEvent(SearchEvent.SearchQueryInput(newText))
                return true
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
    }
}