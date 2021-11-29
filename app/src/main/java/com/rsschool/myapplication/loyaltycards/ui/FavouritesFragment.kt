package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.rsschool.myapplication.loyaltycards.databinding.FavouritesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FavouritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}
