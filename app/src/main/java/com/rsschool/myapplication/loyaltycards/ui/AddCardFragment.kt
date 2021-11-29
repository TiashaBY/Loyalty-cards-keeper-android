package com.rsschool.myapplication.loyaltycards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rsschool.myapplication.loyaltycards.R
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.model.Barcode
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import com.rsschool.myapplication.loyaltycards.utils.BarcodeGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private val viewModel: AddCardViewModel by viewModels()

    private var _binding: AddCardFragmentBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scanBarcode.setOnClickListener {
            findNavController().navigate(R.id.cameraPreviewFragment)
        }
        binding.selectBarcodeType.setOnClickListener {
            val number = viewModel.number.value
            if (number.isNullOrEmpty()) {
                Toast.makeText(context, "Enter card number first", Toast.LENGTH_LONG).show()
            } else {
                val barcode = Barcode(number, viewModel.barcodeFormat.value)
                val action: AddCardFragmentDirections.ActionAddCardFragmentToSelectBarcodeFragment =
                    AddCardFragmentDirections.actionAddCardFragmentToSelectBarcodeFragment(barcode)
                findNavController().navigate(action)
            }
        }
        binding.saveButton.setOnClickListener {
            viewModel.onSaveClick()
        }

        binding.cardNumber.doOnTextChanged { text, start, count, after ->
            viewModel.onCardNumberChange(text.toString())// action which will be invoked when the text is changing
        }
        binding.cardName.doOnTextChanged { text, start, count, after ->
            viewModel.onNameChange(text.toString())// action which will be invoked when the text is changing
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.number.collect { barcode ->
                with(binding.cardNumber) {
                    if (text.toString() != barcode) {
                        setText(barcode)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.barcodeFormat.collect { format ->
                    binding.barcodeType.setText(format?.name)
                }
            }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.imageBitmap.collect { bitmap ->
                    binding.barcode.setImageBitmap(bitmap)
                }
            }
    }
}

