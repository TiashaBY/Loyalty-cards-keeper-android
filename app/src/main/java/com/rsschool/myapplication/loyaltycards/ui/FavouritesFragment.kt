package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.myapplication.loyaltycards.databinding.FavouritesFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.listener.OnLoyaltyCardClickListener
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.CardsListAdapter
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.FavouriteCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding : FavouritesFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel : FavouriteCardsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavouritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardAdapter = CardsListAdapter(OnLoyaltyCardClickListener(viewModel))

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
}
