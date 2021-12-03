package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.myapplication.loyaltycards.databinding.FavouritesFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.recyclerview.CardsListAdapter
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.FavouriteCardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavouritesFragment : CardsFragment() {

    private var _binding : FavouritesFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    override val viewModel : FavouriteCardsViewModel by viewModels()

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
        binding.apply {
            listRecyclerView.apply {
                adapter = cardAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isListEmpty.collect() {
                if (it) {
                    binding.noFavMsg.visibility = View.VISIBLE
                } else {
                    binding.noFavMsg.visibility = View.GONE
                }
            }
        }
    }
}
