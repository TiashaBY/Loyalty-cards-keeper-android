package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.CardDetailsFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CardDetailsViewModel

class CardDetailsViewFragment : Fragment() {

    private var _binding: CardDetailsFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: CardDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CardDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(viewModel.bitmap)
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .into(binding.barcode)
        binding.number.text = viewModel.cardNumber
    }
}