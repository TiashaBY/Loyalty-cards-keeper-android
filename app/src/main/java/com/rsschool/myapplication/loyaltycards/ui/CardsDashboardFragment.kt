package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardsDashboardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.CardsListAdapter
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardsDashboardViewModel
import com.rsschool.myapplication.loyaltycards.usecase.AuthentificationState
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

        lifecycleScope.launchWhenCreated {
            viewModel.authState.collect { state ->
                when (state) {
                    AuthentificationState.AUTH -> {
                        Log.d("auth", "user is logged in")
                    }
                    AuthentificationState.NOT_AUTH -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.signInFragment)
                        Log.d("auth", "navigate to sign in")
                    }
                }
            }
        }
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.cards.collect {
                cardAdapter.submitList(it)
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
            R.id.logout -> {
                viewModel.onLogoutClick()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}