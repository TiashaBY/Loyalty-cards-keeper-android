package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.SelectBarcodeFragmentBinding
import com.rsschool.myapplication.loyaltycards.domain.utils.BarcodeGenerator
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.allViews
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SelectBarcodeFragment : DialogFragment() {

    private var _binding : SelectBarcodeFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel: AddCardViewModel by viewModels({requireParentFragment().childFragmentManager.primaryNavigationFragment!!})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectBarcodeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.barcodeFormat.collect {
                val bitmap = it?.let { BarcodeGenerator().generateBarcode(Barcode(viewModel.number.value!!, it)) }
                Glide.with(this@SelectBarcodeFragment)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.barcode)
            }
        }

        val radioGroup = binding.barcodeTypes
        val rb = RadioButton(requireContext())
        rb.id = 0
        rb.setText("None")
        radioGroup.addView(rb)

        BarcodeFormat.values().forEach { format ->
            val rb = RadioButton(requireContext())
            rb.id = format.hashCode()
            rb.setText(format.name)
            if (format == viewModel.barcodeFormat.value) {
                rb.isChecked = true
            }
            radioGroup.addView(rb)

        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == 0) {
                viewModel.onFormatChange(null)
            } else {
                val radioButton = radioGroup.allViews.first {
                    it.id == checkedId
                } as RadioButton
                viewModel.onFormatChange(BarcodeFormat.valueOf(radioButton.text.toString()))
            }
        }

        binding.selectType.setOnClickListener {
            this.findNavController().navigateUp()
        }

    }
}