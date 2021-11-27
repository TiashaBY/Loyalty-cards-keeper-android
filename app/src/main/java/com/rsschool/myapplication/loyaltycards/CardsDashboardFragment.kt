package com.rsschool.myapplication.loyaltycards

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.myapplication.loyaltycards.databinding.CardsDashboardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.CardsDashboardAdapter
import com.rsschool.myapplication.loyaltycards.viewmodel.CardsDashboardViewModel

class CardsDashboardFragment : Fragment() {

    private val viewModel : CardsDashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = CardsDashboardFragmentBinding.bind(view)
        val cardAdapter = CardsDashboardAdapter()
        binding.apply {
            listRecyclerView.apply {
                adapter = cardAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //????
            }
        }
    }
}