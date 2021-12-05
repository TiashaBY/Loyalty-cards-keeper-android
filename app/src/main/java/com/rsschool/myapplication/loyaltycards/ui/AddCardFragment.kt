package com.rsschool.myapplication.loyaltycards.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.databinding.AddCardFragmentBinding
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.AddCardViewModel
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraActionsRequest
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.CameraMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import android.widget.AdapterView
import com.rsschool.myapplication.loyaltycards.R

@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private val viewModel: AddCardViewModel by navGraphViewModels(R.id.add_card_graph)
    { defaultViewModelProviderFactory }

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
        viewModel.handleArgs()

        with(binding.addCardTop) {
            cardName.setText(viewModel.name.value)
            cardNumber.setText(viewModel.number.value)

            saveButton.setOnClickListener {
                viewModel.onSaveClick()
            }

            cardNumber.doOnTextChanged { text, _, _, _ ->
                viewModel.onCardNumberChange(text.toString())
            }
            cardName.doOnTextChanged { text, _, _, _ ->
                viewModel.onNameChange(text.toString())//
            }

            val values = BarcodeFormat.values()
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                values
            )
            barcodeTypeSpinner.adapter = spinnerAdapter
            barcodeTypeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View?, pos: Int, id: Long
                    ) {
                        viewModel.onBarcodeTypeChange(values[pos])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Another interface callback
                    }
                }
        }

        binding.addCardTop.scanBarcode.setOnClickListener {
            viewModel.onScanBarcodeClick()
        }

        binding.addCardFront.setOnClickListener {
            viewModel.addCardFront()
        }
        binding.addCardBack.setOnClickListener {
            viewModel.addCardBack()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addCardEventsFlow.collect { event ->
                when (event) {
                    is AddCardEvent.ShowInvalidInputMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                    }
                    is AddCardEvent.NavigateBackWithResult -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.cardsDashboardFragment)
                    }
                    is AddCardEvent.NavigateToCameraScanBarcode -> {
                        val action = AddCardFragmentDirections
                            .actionAddCardFragmentToCameraFragment(CameraActionsRequest.ScanBarcodeAction)
                        findNavController().navigate(action)
                    }
                    is AddCardEvent.NavigateToCameraTakeFrontImage -> {
                        val action = AddCardFragmentDirections
                            .actionAddCardFragmentToCameraFragment(
                                CameraActionsRequest.CaptureImageAction(
                                    CameraMode.FRONT
                                )
                            )
                        findNavController().navigate(action)
                    }
                    is AddCardEvent.NavigateToCameraTakeBackImage -> {
                        val action = AddCardFragmentDirections
                            .actionAddCardFragmentToCameraFragment(
                                CameraActionsRequest.CaptureImageAction(
                                    CameraMode.CAPTURE_BACK
                                )
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.number.collect { barcode ->
                with(binding.addCardTop.cardNumber) {
                    if (text.toString() != barcode) {
                        setText(barcode)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.imageBitmap.collect { bitmap ->
                Glide.with(this@AddCardFragment)
                    .load(bitmap)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.addCardTop.barcode)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.frontImageUri.collect { uri ->
                val imgFile = File(uri?.path ?: "")
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                    Glide.with(this@AddCardFragment)
                        .load(bitmap)
                        .error(R.drawable.ic_baseline_image_not_supported_24)
                        .centerInside()
                        .into(binding.cardFrontImage)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.backImageUri.collect { uri ->
                val imgFile = File(uri?.path ?: "")
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                    Glide.with(this@AddCardFragment)
                        .load(bitmap)
                        .error(R.drawable.ic_baseline_image_not_supported_24)
                        .centerInside()
                        .into(binding.cardBackImage)
                }
            }
        }
    }
}
